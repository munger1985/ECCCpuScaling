package com.example.schedulingtasks;


/**
 * including cpu usage and queue
 */
class CpuMetricModel {
    String vmId;
    String ld_1;
    String ld_5;
    String ld_15;
    String cpuUsage;
    String cores;

    public String getCores() {
        return cores;
    }

    public void setCores(String cores) {
        this.cores = cores;
    }

    public String getLd_1() {
        return ld_1;
    }

    public void setLd_1(String ld_1) {
        this.ld_1 = ld_1;
    }



    public String getVmId() {
        return vmId;
    }

    public void setVmId(String vmId) {
        this.vmId = vmId;
    }

    public String getLd_5() {
        return ld_5;
    }

    public void setLd_5(String ld_5) {
        this.ld_5 = ld_5;
    }

    public String getLd_15() {
        return ld_15;
    }

    public void setLd_15(String ld_15) {
        this.ld_15 = ld_15;
    }

    public String getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(String cpuUsage) {
        this.cpuUsage = cpuUsage;
    }
}