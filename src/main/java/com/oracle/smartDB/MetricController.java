package com.oracle.smartDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class MetricController {
    @Resource
    DataFilter dataFilter;
    @Value("${budget}")
    String budget;
    @Value("${startDayOfMonth}")
    int startDayOfMonth;
    @Value("${price}")
    String price;
    boolean close;
//    @Resource
//    MonitorSvc monitorSvc;
    private final Logger log = LoggerFactory.getLogger(MetricController.class);
    @Autowired
    CostRepo costRepo;

    private synchronized void addCost(  BigDecimal core) {
        CostDo costDo = null;
//        log.info("debug {}",ocid_vmid);
//        CpuMetricModel ttt = Constant.metricModelHashMap.get(ocid_vmid);
//        log.info("tt: {}" ,ttt);
        try {
            costDo = costRepo.findById("all").get();

        } catch (NoSuchElementException noSuchElementException) {
            costDo = new CostDo();
            costDo.setCostKey("all");
            costDo.setCostValue("0");
        }

        String costV = costDo.getCostValue();
        BigDecimal all = new BigDecimal(costV);
        BigDecimal minutecost = core.multiply(new BigDecimal(price));
        all = all.add(minutecost);
        costDo.setCostValue(all.toString());
        if(all.compareTo(new BigDecimal(budget))>=0){
            log.warn("warning!! cost {}$ is now over budget {}$",all,budget);
        }
        costRepo.save(costDo);
        resetCost();//reset every month

        log.info("$ $ $ Current Cost: {} $", all);
    }

    private void resetCost() {
//            Date startD = sdf.parse(startDate);
            //startD.getCalendarDate().getMonth()

            Calendar calendarToday = Calendar.getInstance();
            int today=calendarToday.get(Calendar.DAY_OF_MONTH);
//            int hour=calendarToday.get(Calendar.HOUR);
            int minute=calendarToday.get(Calendar.MINUTE);
//            int second = calendarToday.get(Calendar.SECOND);
//today==12
            if ( today == startDayOfMonth && minute == 0 && close==false)
            {
                CostDo costDo=new CostDo();
                costDo.setCostValue("0");
                costDo.setCostKey("all");
                costRepo.save(costDo);
                close=true;
            }
            if(minute==1){
                close=false;
            }
          //  int currentM=calendarToday.get(Calendar.MONTH)+1;

//            if(today==startD.getCalendarDate().getDayOfMonth()){
//
//
//            }



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
        log.info("====> got one metric from node {} of cluster", cpuMetricModel.getVmId());
        String ocid_vmid =cpuMetricModel.getClusterId() + "=" + cpuMetricModel.getVmId();
        Constant.metricModelHashMap.put(ocid_vmid, cpuMetricModel);
        BigDecimal core = new BigDecimal(cpuMetricModel.getCores());
        addCost( core);
        new Thread(() -> dataFilter.setNodeMetric(cpuMetricModel)).start();
        return new ResponseEntity<>("Metric is posted successfully", HttpStatus.CREATED);
    }

}

