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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class MetricController {
    @Autowired
    DataFilter dataFilter;
    @Value("${budget}")
    String budget;
    @Value("${startDayOfMonth}")
    int startDayOfMonth;
    @Value("${price}")
    String price;
    boolean close;

    boolean dailyClose;
    //    @Resource
//    MonitorSvc monitorSvc;
    private final Logger log = LoggerFactory.getLogger(MetricController.class);
    @Autowired
    CostRepo costRepo;
    @Autowired
    DailyCostRepo dailyCostRepo;

    private synchronized void addDailyCost(String clusterId, BigDecimal core) {
        resetDaily(clusterId);
        DailyCostDo dailyCostDo = null;
//        log.info("debug {}",ocid_vmid);
//        CpuMetricModel ttt = Constant.metricModelHashMap.get(ocid_vmid);
//        log.info("tt: {}" ,ttt);
        try {
            dailyCostDo = dailyCostRepo.findById(clusterId).get();

        } catch (NoSuchElementException noSuchElementException) {
            dailyCostDo = new DailyCostDo();
            dailyCostDo.setDate(new Date());
            dailyCostDo.setClusterId(clusterId);
            dailyCostDo.setCostValue("0");
        }
        addDailyTotalCost(  core);

        String costV = dailyCostDo.getCostValue();
        BigDecimal all = new BigDecimal(costV);
        BigDecimal minutecost = core.multiply(new BigDecimal(price));
        all = all.add(minutecost);
        dailyCostDo.setCostValue(all.toString());
        dailyCostDo.setDate(new Date());
        dailyCostRepo.save(dailyCostDo);
//        resetCost();//reset every month

        log.info("$ $ $ daily accrual Cost, time: {}  cluster {} cost: {}$", dailyCostDo.getDate(), dailyCostDo.getClusterId(), dailyCostDo.getCostValue());
    }

    private void addDailyTotalCost(  BigDecimal core) {
        DailyCostDo allDo=null;
        try {
              allDo = dailyCostRepo.findById("all").get();
        }
        catch (NoSuchElementException noSuchElementException) {
            allDo = new DailyCostDo();
            allDo.setClusterId("all");
            allDo.setDate(new Date());
            allDo.setCostValue("0");
        }
        String costV = allDo.getCostValue();
        BigDecimal all = new BigDecimal(costV);
        BigDecimal minutecost = core.multiply(new BigDecimal(price));
        all = all.add(minutecost);
        allDo.setCostValue(all.toString());
        allDo.setDate(new Date());
        dailyCostRepo.save(allDo);
        log.info("$ $ $ daily accrual Cost Total, time: {}  cost: {} $", allDo.getDate(), allDo.getCostValue());

    }

    private void resetDaily(String clusterId) {
        Calendar calendarToday = Calendar.getInstance();

        int hour = calendarToday.get(Calendar.HOUR_OF_DAY);
        int minute = calendarToday.get(Calendar.MINUTE);
        if (hour == 0 && minute == 0 && dailyClose == false) {
           DailyCostDo dailyCostDoPre= dailyCostRepo.findById(clusterId).get();
           log.info("yesterday cluster {} cost {} $", clusterId.substring(clusterId.length()-4),dailyCostDoPre.getCostValue());
            DailyCostDo dailyCostDo = new DailyCostDo();
            dailyCostDo.setClusterId(clusterId);
            dailyCostDo.setCostValue("0");
            dailyCostRepo.save(dailyCostDo);
            dailyClose = true;
            dailyCostDo = new DailyCostDo();
            dailyCostDo.setClusterId("all");
            dailyCostDo.setCostValue("0");
            dailyCostRepo.save(dailyCostDo);
        }
        if (minute == 2) {
            dailyClose = false;
        }

    }

    private synchronized void addCost(BigDecimal core) {
        resetCost();//reset every month
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
        if (all.compareTo(new BigDecimal(budget)) >= 0) {
            log.warn("warning!! cost {}$ is now over budget {}$", all, budget);
        }
        costRepo.save(costDo);

        log.info("$ $ $ monthly accrual Cost: {} $", all);
    }

    private void resetCost() {
//            Date startD = sdf.parse(startDate);
        //startD.getCalendarDate().getMonth()

        Calendar calendarToday = Calendar.getInstance();
        int today = calendarToday.get(Calendar.DAY_OF_MONTH);
//            int hour=calendarToday.get(Calendar.HOUR);
        int minute = calendarToday.get(Calendar.MINUTE);
//            int second = calendarToday.get(Calendar.SECOND);
//today==12
        if (today == startDayOfMonth && minute == 0 && close == false) {
            CostDo costDo = new CostDo();
            costDo.setCostValue("0");
            costDo.setCostKey("all");
            costRepo.save(costDo);
            close = true;
        }
        if (minute == 2) {
            close = false;
        }
        //  int currentM=calendarToday.get(Calendar.MONTH)+1;

//            if(today==startD.getCalendarDate().getDayOfMonth()){
//
//
//            }


    }

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    @RequestMapping(value = "/collect", method = RequestMethod.POST)
    public ResponseEntity<Object> collect(@RequestBody CpuMetricModel cpuMetricModel) {
        String ocid_vmid = cpuMetricModel.getClusterId() + "=" + cpuMetricModel.getVmId();
        Constant.metricModelHashMap.put(ocid_vmid, cpuMetricModel);
        BigDecimal core = new BigDecimal(cpuMetricModel.getCores());
        addCost(core);
        String back4clusterId = cpuMetricModel.getClusterId().substring(cpuMetricModel.getClusterId().length() - 4);

        addDailyCost(back4clusterId, core);
        new Thread(() -> dataFilter.setNodeMetric(cpuMetricModel)).start();
        return new ResponseEntity<>("Metric is posted successfully", HttpStatus.CREATED);
    }

}

