package com.oracle.smartDB;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constant {
    /**
     * structure:  clusterId=vmId, CpuMetricModel
     */
    static Map<String, CpuMetricModel> metricModelHashMap = Collections.synchronizedMap(new HashMap<>()) ;
    final static Double budget = 100000.0;


}