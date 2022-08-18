package com.oracle.smartDB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MonitorSvc {
    RunCmd runCmd;
    private   final Logger log = LoggerFactory.getLogger(MonitorSvc.class);

     public Double getCpuUsage(  ) {
        runCmd =new  RunCmd("getCpuUsage.sh");
        String cpuU = runCmd.run();
        return Double.parseDouble(cpuU);
    }

    public Integer getCores(  ) {
        runCmd =new  RunCmd("getCores.sh");
        String cores = runCmd.run();
        return Integer.parseInt(cores);
    }
    public String getScaleLog(  ) {
        runCmd =new  RunCmd("getScaleLog.sh");
        String record=  runCmd.run();
        log.info("record {}",record);

        return record;
    }


}