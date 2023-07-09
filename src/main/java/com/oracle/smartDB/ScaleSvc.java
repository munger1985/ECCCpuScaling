package com.oracle.smartDB;

import com.oracle.bmc.database.DatabaseClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScaleSvc {
    private final Logger log = LoggerFactory.getLogger(ScaleSvc.class);
    @Autowired
    DatabaseClient databaseClient;

    DbApi dbApi;

    /**
     *
     * @param ocid vmcluster ocid
     * @param targetCores
     * @return
     */
    boolean checkBeforeRunCmd(String ocid, String targetCores) {
        int targetC = Integer.parseInt(targetCores);
        if (targetC % 2 != 0) {
            log.error("This target core number is {} not an even!!! Exit ", targetC);
            return false;
        }
        int cores=0;
        CpuMetricModel c1 = Constant.metricModelHashMap.get(ocid+"=1");
        CpuMetricModel c2 = Constant.metricModelHashMap.get(ocid+"=2");

        if (c1 !=null && Integer.parseInt(c1.getCores()) !=0) {
            cores = Integer.parseInt(c1.getCores());
        } else if (c2 !=null && Integer.parseInt(c2.getCores()) !=0){
            cores = Integer.parseInt(c2.getCores());
        }

//        log.info("qwqwqw {} ,{}}", cores, targetC);
        return targetC != cores;

    }

    public void scaleCpu(String ocid_targetCores) {
        log.info("enter method scaleCpu!");
        String[] strarr = ocid_targetCores.split("=");
        String ocid=strarr[0];
        String targetCores = strarr[1];

        dbApi = new DbApi(databaseClient);
        Integer currentCpuCount = dbApi.getCurrentCpuCount(ocid);
        boolean b = checkBeforeRunCmd(ocid,targetCores);
        if (!b) {
            log.info("Current cores equals target cores, No need to run oci cli, exit!");
            return;
        }
        log.info("<============= ready to apply {} OCPU =============> ", targetCores);

        dbApi.param[0] = ocid;
        dbApi.param[1]=targetCores;
        String result = dbApi.run();
//        log.info("command result : {} ", result);
        if (  "Fail".equals(result)) {
            log.info("{} =》{} Exited, not a valid command or failed to run due to above cause ",ocid,targetCores);
        }
        if("Success".equals(result)){
            log.info(result);
            String clus = ocid.substring(ocid.length() - 4);
            log.info("scale_success_log: {} ==>> {}",clus,targetCores);
    //            log.info("model1: {},{}", ocid,  Constant.metricModelHashMap.get(ocid+"=1")  );
    //            log.info("model2: {},{}", ocid,  Constant.metricModelHashMap.get(ocid+"=2")  );

            if (upOrDown(currentCpuCount,targetCores)) {
                log.info("↑↑↑ {} scaled up to {} OCPU", clus,targetCores);
            } else {
                log.info("↓↓↓ {} scaled down to {} OCPU",clus, targetCores);
            }
        }
//        if (result.contains("UPDATING") || result.contains("AVAILABLE")) {
//
//
//        }
//        Constant.metricModelHashMap.clear();

        Constant.metricModelHashMap.remove(ocid+"=1");
        Constant.metricModelHashMap.remove(ocid+"=2");



    }

    private boolean upOrDown(Integer  currentcpuCount, String targetCores) {

        int targetC = Integer.parseInt(targetCores);

        return currentcpuCount < targetC;
    }



}