package com.oracle.smartDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataFilter {
    private final Logger log = LoggerFactory.getLogger(DataFilter.class);
    /**
     * clusterId: timer
     */
    Map<String, Boolean> switchMap = Collections.synchronizedMap(new HashMap<>());
    @Resource
    ScaleRule scaleRule;

    @Value("${datafilter_interval}")
    Integer datafilter_interval;

    public void setNodeMetric(CpuMetricModel cpuMetricModel) {
        System.out.println(datafilter_interval);
        boolean b= CheckAndPrintNodeInfo(cpuMetricModel);
        if (!b){
            log.warn("Node data is broken, ignore!");
            return;
        }
        CpuMetricModel finalCpuMetricModel;
        try {
            finalCpuMetricModel = compare2Nodes(cpuMetricModel);

        } catch (IllegalStateException illegalStateException) {
            log.error("{}", illegalStateException);
            return;
        }
        if(finalCpuMetricModel!=null){
            scaleRule.compute(finalCpuMetricModel);
        }

    }

    private boolean CheckAndPrintNodeInfo(CpuMetricModel cpuMetricModel) {
        log.info("===>>)) got a new metric"  );
        log.info("clusterId: {}",cpuMetricModel.getClusterId());
        log.info("<============= node {} =============>", cpuMetricModel.getVmId());
        log.info("  ld_1 {}", cpuMetricModel.getLd_1());
        log.info("  ld_5 {}", cpuMetricModel.getLd_5());
        log.info("  ld_15 {}", cpuMetricModel.getLd_15());
        log.info("  cpu usage {} %", cpuMetricModel.getCpuUsage());
        log.info("  getMincore {}  ", cpuMetricModel.getMincore());
        log.info("  getMaxcore {}  ", cpuMetricModel.getMaxcore());
        log.info("  getQweight {}  ", cpuMetricModel.getQweight());
        log.info("  getCpuCeiling {}  ", cpuMetricModel.getCpuCeiling());
        log.info("  current cores {}  ", cpuMetricModel.getCores());

        if(Double.parseDouble(cpuMetricModel.getCpuUsage())==0.){
            return false;
        }
        return true;

    }


    /**
     * @param cpuMetricModel cpuMetricModel is always newer
     * @return
     */
    CpuMetricModel compare2Nodes(CpuMetricModel cpuMetricModel) throws IllegalStateException {
        String clusterId = cpuMetricModel.getClusterId();
        String vmId = cpuMetricModel.getVmId();
        String otherVm = otherVmId(vmId);

        CpuMetricModel cpuMetricModel2 = Constant.metricModelHashMap.get(clusterId + "=" + otherVm);

        if (cpuMetricModel2 != null) {
//            TimeOutTask timeOutTask = switchMap.get(cpuMetricModel.clusterId);
//        log.error("TimeOutTask {}",timeOutTask);
            log.info("<============= Print to Compare  =============>" );

            log.info("clusterId: {}",cpuMetricModel.getClusterId());
            log.info("<============= node {} =============>", cpuMetricModel.getVmId());
            log.info("  ld_1 {}", cpuMetricModel.getLd_1());
            log.info("  ld_5 {}", cpuMetricModel.getLd_5());
            log.info("  ld_15 {}", cpuMetricModel.getLd_15());
            log.info("  cpu usage {} %", cpuMetricModel.getCpuUsage());
            log.info("  getMincore {}  ", cpuMetricModel.getMincore());
            log.info("  getMaxcore {}  ", cpuMetricModel.getMaxcore());
            log.info("  getQweight {}  ", cpuMetricModel.getQweight());
            log.info("  getCpuCeiling {}  ", cpuMetricModel.getCpuCeiling());
            log.info("  current cores {}  ", cpuMetricModel.getCores());

            log.info("clusterId: {}",cpuMetricModel2.getClusterId());
            log.info("<============= node {} =============>", cpuMetricModel2.getVmId());
            log.info("  ld_1 {}", cpuMetricModel2.getLd_1());
            log.info("  ld_5 {}", cpuMetricModel2.getLd_5());
            log.info("  ld_15 {}", cpuMetricModel2.getLd_15());
            log.info("  cpu usage {} %", cpuMetricModel2.getCpuUsage());
            log.info("  getMincore {}  ", cpuMetricModel2.getMincore());
            log.info("  getMaxcore {}  ", cpuMetricModel2.getMaxcore());
            log.info("  getQweight {}  ", cpuMetricModel2.getQweight());
            log.info("  getCpuCeiling {}  ", cpuMetricModel2.getCpuCeiling());
            log.info("  current cores {}  ", cpuMetricModel2.getCores());
            switchMap.put(cpuMetricModel.clusterId, true);

//            timeOutTask.open = true;
            Double cpuUsage1 = Double.parseDouble(cpuMetricModel.getCpuUsage());
            Double cpuUsage2 = Double.parseDouble(cpuMetricModel2.getCpuUsage());
            Double node1_ld_5 = Double.parseDouble(cpuMetricModel.getLd_5() );
            Double node2_ld_5 = Double.parseDouble(cpuMetricModel2.getLd_5() );
            if (cpuUsage1*0.6+node1_ld_5*0.4 >= cpuUsage2*0.6+node2_ld_5*0.4) {
                log.info("Solar selected node {} metric",cpuMetricModel.getVmId());
                return cpuMetricModel;
            } else {
                log.info("Solar selected node {} metric",cpuMetricModel2.getVmId());
                return cpuMetricModel2;
            }

        } else {/// get node x in a cluster, will wait for other node metric
//            Timer timer = new Timer();
//            TimeOutTask timeOutTask = new TimeOutTask( );
              boolean timeoutLock=false;
            switchMap.put(cpuMetricModel.clusterId, timeoutLock);

//            timer.schedule(timeOutTask, 120 * 1000, 1000);
            long start = System.currentTimeMillis();
            while (switchMap.get(cpuMetricModel.clusterId)  == false) {//waiting here
                try {
                    Thread.sleep(333);
                    long end=System.currentTimeMillis();
                    if (end-start>= datafilter_interval*1000){
                        log.info("await for other node timed out, Solar selected node {} metric right away",cpuMetricModel.getVmId());
                        return  cpuMetricModel;
                    }
                } catch (InterruptedException e) {
                    log.error(e.getCause().toString());
                }
            }
            return null;
//            if (timeOutTask.returnRightAway){
//                return null;
//            }
//             timerHashMap.remove(cpuMetricModel.clusterId);
//            return cpuMetricModel;
//             HashMap<String,CpuMetricModel> vmMap = new HashMap();
//             vmMap.put(cpuMetricModel.getVmId(), cpuMetricModel);
//             holderMap.put(cpuMetricModel.getClusterId(), vmMap);
        }
    }

    String otherVmId(String vmId) {
        if ("1".equals(vmId)) {
            return "2";
        } else return "1";
    }


}

class TimeOutTask  {
//    Timer timer;
    /**
     * open is a switch for the timeout
     */
    volatile public boolean open = false;
//    /**
//     * is for other node metric comes in, end the timer task right away
//     */
//    volatile public boolean returnRightAway = false;

}