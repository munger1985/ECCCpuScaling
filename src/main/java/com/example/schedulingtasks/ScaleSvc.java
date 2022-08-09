package com.example.schedulingtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ScaleSvc {
    private final Logger log = LoggerFactory.getLogger(ScaleSvc.class);


    RunCmd runCmd;


    boolean checkBeforeRunCmd(String targetCores) {
        int i = Integer.parseInt(targetCores);
        if (i % 2 != 0) {
            log.error("This target core number is {} not an even!!! Exit ", i);
            return false;
        }
        int cores;
        CpuMetricModel c1 = Constant.metricModelHashMap.get("1");
        CpuMetricModel c2 = Constant.metricModelHashMap.get("2");

        if (Integer.parseInt(c1.getCores() )!= 0) {
            cores = Integer.parseInt(c1.getCores());
        } else {
            cores = Integer.parseInt(c2.getCores());
        }

        int targetC = Integer.parseInt(targetCores);
//        log.info("qwqwqw {} ,{}}", cores, targetC);
        return targetC != cores;

    }

    public void scaleCpu(String targetCores) {
        runCmd = new RunCmd("scaleCpu.sh");
        boolean b = checkBeforeRunCmd(targetCores);
        if (!b) {
            log.info("No need to run oci cli, exit!");
            return;
        }
        log.info("<============= ready to apply {} OCPU =============> ", targetCores);

        runCmd.param[0] = targetCores;
        String result = runCmd.run();
        log.info("command result : {} ", result);
        if (result == null) {
            log.info("Not a valid command, exit!");
            return;
        }

        if (result.contains("UPDATING") || result.contains("AVAILABLE")) {
            log.info(result);
            if (upOrDown(targetCores)) {
                log.info("↑↑↑↑↑↑↑↑↑↑↑↑↑↑ scaled up to {} OCPU", targetCores);
            } else {
                log.info("↓↓↓↓↓↓↓↓↓↓↓↓↓↓ scaled down to {} OCPU", targetCores);
            }

        }

    }

    private boolean upOrDown(String targetCores) {
        //current cores
        int cores;
        if (Constant.metricModelHashMap.get("1") != null) {
            CpuMetricModel c1 = Constant.metricModelHashMap.get("1");
            cores = Integer.parseInt(c1.getCores());
        } else {
            CpuMetricModel c2 = Constant.metricModelHashMap.get("2");
            cores = Integer.parseInt(c2.getCores());
        }
        int targetC = Integer.parseInt(targetCores);

        Constant.metricModelHashMap.clear();
        return cores < targetC;
    }

//    public static void main(String[] args) {
//
//        Double d = 123.3;
//        System.out.println(d / 100);
//    }


}