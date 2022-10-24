package com.oracle.smartDB;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

//   record for daily
@Entity
public class DailyCostDo {
    @Id
    private String clusterId;
    Date date;

    String costValue;

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCostValue() {
        return costValue;
    }

    public void setCostValue(String costValue) {
        this.costValue = costValue;
    }
}