package com.example.schedulingtasks;

import io.reactivex.rxjava3.subjects.PublishSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * rule file  or policy file defined here,
 * it will call monitorSvc to get info
 * call ScaleSvc to scale CPU
 */
@Component
public class ScaleRule {
    @Autowired
    MonitorSvc monitorSvc;
    @Autowired
    ScaleSvc scaleSvc;
    @Value("${coolDown}")
    Integer coolDown;

    @Value("${upTrigger}")
    Double upTrigger;
    @Value("${downTrigger}")
    Double downTrigger;
    @Value("${q_weight}")
    Double q_weight;
    @Value("${minCores}")
    Integer minCores;
    @Value("${maxCores}")
    Integer maxCores;
    @Value("${RatioPerScale}")
    Integer RatioPerScale;
    public PublishSubject<String> publishSubject = PublishSubject.create();
    private final Logger log = LoggerFactory.getLogger(ScaleRule.class);

    @PostConstruct
    public void setup() {
        //window(coolDown, TimeUnit.MINUTES)
        publishSubject
                .subscribe(x -> scaleSvc.scaleCpu(x),err-> log.error(err.toString()));
    }
    Integer capCores(Integer cores){
        if(cores>=maxCores){
            cores=maxCores;
        }
        if(cores<=minCores){
            cores=minCores;
        }
        return  cores;
    }
    Double compareDouble(String s1, String s2){
        Double d1 = Double.parseDouble(s1);
        Double d2 = Double.parseDouble(s2);
        if(d1>=d2){
            return d1;
        }else
        {
            return d2;
        }
    }
    Integer compareInteger(String s1, String s2){
        s1 = s1.split("\\.")[0];
        s2 = s2.split("\\.")[0];

        Integer d1 = Integer.parseInt(s1);
        Integer d2 = Integer.parseInt(s2);
        if(d1>=d2){
            return d1;
        }else
        {
            return d2;
        }
    }
    HashMap<String, CpuMetricModel> resetModel(HashMap<String, CpuMetricModel> metricModelHashMap){
        if(metricModelHashMap.get("1")==null)
        {
            log.info("node1 can not get metric at the moment");
            CpuMetricModel cpuMetricModel= new CpuMetricModel();
            cpuMetricModel.setLd_1("0.0");
            cpuMetricModel.setLd_5("0.0");
            cpuMetricModel.setLd_15("0.0");
            cpuMetricModel.setCores("0");
            cpuMetricModel.setCpuUsage("0.0");
            metricModelHashMap.put("1",cpuMetricModel);
        }
        if(metricModelHashMap.get("2")==null)
        {
            log.info("node2 can not get metric at the moment");
            CpuMetricModel cpuMetricModel= new CpuMetricModel();
            cpuMetricModel.setLd_1("0.0");
            cpuMetricModel.setLd_5("0.0");
            cpuMetricModel.setLd_15("0.0");
            cpuMetricModel.setCores("0");
            cpuMetricModel.setCpuUsage("0.0");
            metricModelHashMap.put("2",cpuMetricModel);
        }
        return metricModelHashMap;


    }
    /**
     * just give it a signal to trigger
     */

    public void compute(HashMap<String, CpuMetricModel> metricModelHashMap) {
        //metric is not complete
        /// what if one vm down,
//        log.info("1212");
        log.info("===> ===> start one computation ===> ===> ===>");

        log.info("threshold top == {} %", upTrigger);
        log.info("threshold buttom == {} %", downTrigger);

        metricModelHashMap = resetModel(metricModelHashMap);

        CpuMetricModel cpuMetricModel1 = metricModelHashMap.get("1");
        CpuMetricModel cpuMetricModel2 = metricModelHashMap.get("2");
        log.info("<============= node1 =============>");
        log.info("node1 ld_1 {}",cpuMetricModel1.getLd_1());
        log.info("node1 ld_5 {}",cpuMetricModel1.getLd_5());
        log.info("node1 ld_15 {}",cpuMetricModel1.getLd_15());
        log.info("node1 cpu usage {} %",cpuMetricModel1.getCpuUsage());
        log.info("<============= node2 =============>");
        log.info("node2 ld_1 {}",cpuMetricModel1.getLd_1());
        log.info("node2 ld_5 {}",cpuMetricModel2.getLd_5());
        log.info("node2 ld_15 {}",cpuMetricModel2.getLd_15());
        log.info("node2 cpu usage {} %",cpuMetricModel2.getCpuUsage());
        log.info("***********************************");



        Double cpuUsage = compareDouble(metricModelHashMap.get("1").cpuUsage, metricModelHashMap.get("2").cpuUsage);
        Integer ld_1 = compareInteger (metricModelHashMap.get("1").ld_1,metricModelHashMap.get("2").ld_1);
        Integer ld_5 = compareInteger (metricModelHashMap.get("1").ld_5,metricModelHashMap.get("2").ld_5);
        Integer ld_15 = compareInteger (metricModelHashMap.get("1").ld_15,metricModelHashMap.get("2").ld_15);
        Integer cores = compareInteger( metricModelHashMap.get("1").getCores() , metricModelHashMap.get("2").getCores());
        if( (ld_5> maxCores && cores.equals(maxCores))   || (ld_5<minCores   && cores.equals(minCores)) ){
            log.info("Out of the range of cpu core adjustment, Exit!");
            return;
        }
        log.info("current: cores {} ",cores);
        log.info("current: effective cpu usage {} %",cpuUsage);
        if (cpuUsage > upTrigger) {
            if(ld_15*q_weight > cores || ld_1 > 2 * ld_15){
                if (ld_5*q_weight > ld_15*q_weight) {

                    int i = Integer.parseInt(String.valueOf(ld_5));
                    if (i %2!=0 ){
                        i = i + 1;
                    }
                    i = capCores(i);
                    log.info("will change to cores ===> {}",i);

                    publishSubject.onNext(String.valueOf(i));
                }
            }

        }
//        log.info("scaledown  cpuUsage  {}   ", cpuUsage);
//        log.info("scaledown  cores  {}   ", cores);

        if (cpuUsage < downTrigger) {
            if (ld_15*q_weight < cores) {
//                log.info("scaledown {} {} ", ld_5,ld_15);
                if (ld_5*q_weight < ld_15*q_weight) {
                    int i = Integer.parseInt(String.valueOf(ld_5));
                    if (i %2!=0 ){
                        i = i + 1;
                    }
                    i = capCores(i);
                    log.info("will change to cores ===> {}",i);

                    publishSubject.onNext(String.valueOf(i));

                }

            }
        }



    }

    /**
     * @deprecated
     */
    public void scale() {

        Integer variedCores = 0;
        Double cpuUsage = monitorSvc.getCpuUsage();
        cpuUsage = (cpuUsage / 100);


        Integer currentCores = monitorSvc.getCores();
        System.out.println("currecore " + currentCores);
        log.info("@@ Current CPU usage is {}", cpuUsage);

        if (cpuUsage >= upTrigger) {
            variedCores = (maxCores - currentCores) / RatioPerScale;


        } else if (cpuUsage <= downTrigger) {

            variedCores = (minCores - currentCores) / RatioPerScale;
        }
        int targetCores = currentCores + variedCores;

        log.info("targetCores {}", targetCores);
        monitorSvc.getScaleLog();

        if (variedCores != 0) {
            log.info("now trigger scale  --- ---");

            monitorSvc.getScaleLog();
//            scaleSvc.scaleCpu(String.valueOf(currentCores), String.valueOf(targetCores));
//            basecmd = basecmd.replaceAll("targetCores", targetCores)
//                    .replaceAll("currentCores", currentCores);
//            scaleSvc.scaleCpu( );
        }
    }
}