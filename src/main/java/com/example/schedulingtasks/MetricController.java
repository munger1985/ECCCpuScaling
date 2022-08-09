package com.example.schedulingtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class MetricController {
    @Resource
    ScaleRule scaleRule;
    @Resource
    MonitorSvc monitorSvc;
    private final Logger log = LoggerFactory.getLogger(MetricController.class);

    //    @RequestMapping(value = "/collec2t", method = RequestMethod.POST  )
//    public ResponseEntity<Object> CpuUsage(@RequestBody CpuUsage cpuUsage) {
//        System.out.println(cpuUsage.data);
//        String data=cpuUsage.data;
//        data=data.trim();
//        String[] sarr=data.split("\\s+");
//        Double cpuload = Double.parseDouble(sarr[3]) + Double.parseDouble(sarr[5]);
//
//
//        return new ResponseEntity<>("Metric is posted successfully", HttpStatus.CREATED);
//    }
    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @RequestMapping(value = "/collect", method = RequestMethod.POST)
    public ResponseEntity<Object> collect(@RequestBody CpuMetricModel cpuMetricModel) {
        log.info("====> got one metric from node {}",cpuMetricModel.getVmId());
        Constant.metricModelHashMap.put(cpuMetricModel.getVmId(),cpuMetricModel);
//        System.out.println("json");
//        System.out.println(cpuMetricModel.getCores());
//        System.out.println(cpuMetricModel.getCpuUsage());
//        System.out.println(cpuMetricModel.getLd_1());
//        System.out.println(cpuMetricModel.getLd_5());
//        System.out.println(cpuMetricModel.getLd_15());
         new Thread(()-> scaleRule.compute( Constant.metricModelHashMap)).start();



        return new ResponseEntity<>("Metric is posted successfully", HttpStatus.CREATED);
    }

}

