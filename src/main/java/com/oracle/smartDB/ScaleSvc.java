package com.oracle.smartDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ScaleSvc {
    private final Logger log = LoggerFactory.getLogger(ScaleSvc.class);


    RunCmd runCmd;

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
        runCmd = new RunCmd("scaleCpu.sh");
        boolean b = checkBeforeRunCmd(ocid,targetCores);
        if (!b) {
            log.info("Current cores equals target cores, No need to run oci cli, exit!");
            return;
        }
        log.info("<============= ready to apply {} OCPU =============> ", targetCores);

        runCmd.param[0] = ocid;
        runCmd.param[1]=targetCores;
        String result = runCmd.run();
//        log.info("command result : {} ", result);
        if (  "did not run".equals(result)) {
            log.info("{} =》{} Exited, not a valid command or failed to run due to above cause ",ocid,targetCores);
//            Constant.metricModelHashMap.remove(ocid+"=1");
//            Constant.metricModelHashMap.remove(ocid+"=2");
            return;
        }

        if (result.contains("UPDATING") || result.contains("AVAILABLE")) {
            log.info(result);
            String clus = ocid.substring(ocid.length() - 4);
            log.info("scale_success_log: {},{}",clus,targetCores);
//            log.info("model1: {},{}", ocid,  Constant.metricModelHashMap.get(ocid+"=1")  );
//            log.info("model2: {},{}", ocid,  Constant.metricModelHashMap.get(ocid+"=2")  );

            if (upOrDown(ocid,targetCores)) {

                log.info("↑↑↑ {} scaled up to {} OCPU", clus,targetCores);
            } else {
                log.info("↓↓↓ {} scaled down to {} OCPU",clus, targetCores);
            }

        }
//        Constant.metricModelHashMap.clear();

        Constant.metricModelHashMap.remove(ocid+"=1");
        Constant.metricModelHashMap.remove(ocid+"=2");



    }

    private boolean upOrDown(String ocid, String targetCores) {
        //current cores
        int cores;
        if (Constant.metricModelHashMap.get(ocid+"=1") != null) {
            CpuMetricModel c1 = Constant.metricModelHashMap.get(ocid+"=1");
            cores = Integer.parseInt(c1.getCores());
        } else {
            CpuMetricModel c2 = Constant.metricModelHashMap.get(ocid+"=2");
            cores = Integer.parseInt(c2.getCores());
        }
        int targetC = Integer.parseInt(targetCores);

        return cores < targetC;
    }



}