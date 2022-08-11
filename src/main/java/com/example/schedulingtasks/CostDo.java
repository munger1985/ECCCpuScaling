package com.example.schedulingtasks;

import javax.persistence.Entity;
import javax.persistence.Id;

//@Entity
@Entity
public class CostDo {
    @Id
    private String costKey;
    String costValue;

    public String getCostKey() {
        return costKey;
    }

    public void setCostKey(String costKey) {
        this.costKey = costKey;
    }

    public String getCostValue() {
        return costValue;
    }

    public void setCostValue(String costValue) {
        this.costValue = costValue;
    }
}