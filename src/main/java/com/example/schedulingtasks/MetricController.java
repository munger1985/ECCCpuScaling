package com.example.schedulingtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class MetricController {
    @Resource
    ScaleRule scaleRule;
    @Resource
    MonitorSvc monitorSvc;
    private final Logger log = LoggerFactory.getLogger(MetricController.class);
@Autowired
CostRepo costRepo;

    private synchronized void addCost(String ocid_vmid) {
        CostDo costDo = null;

        try {
              costDo = costRepo.findById( "all").get();

        }catch (NoSuchElementException noSuchElementException){
              costDo = new CostDo();
            costDo.setCostKey("all");
            costDo.setCostValue("0");
        }

        String costV = costDo.getCostValue();
        BigDecimal all = new BigDecimal(costV);
        BigDecimal core = new BigDecimal(Constant.metricModelHashMap.get(ocid_vmid).getCores());
        BigDecimal minutecost = core.multiply(new BigDecimal("0.125"));
        all = all.add(minutecost);
        costDo.setCostValue(all.toString());

        costRepo.save(costDo);
        log.info("$ $ $ Current Cost: {} $", all);
    }

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
        log.info("====> got one metric from node {} of cluster {}", cpuMetricModel.getVmId(), cpuMetricModel.getClusterId());
        Constant.metricModelHashMap.put(cpuMetricModel.getClusterId() + "=" + cpuMetricModel.getVmId(), cpuMetricModel);
        new Thread(() -> scaleRule.compute(cpuMetricModel)).start();
        addCost(cpuMetricModel.getClusterId() + "=" + cpuMetricModel.getVmId());
        return new ResponseEntity<>("Metric is posted successfully", HttpStatus.CREATED);
    }

}

