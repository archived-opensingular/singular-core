package br.net.mirante.singular.dao;

import java.io.Serializable;

public class StatusDTO implements Serializable {

    private String processCode;
    private Integer amount;
    private Integer averageTimeInDays;

    public StatusDTO(String processCode, Integer amount, Integer averageTimeInDays) {
        this.processCode = processCode;
        this.amount = amount;
        this.averageTimeInDays = averageTimeInDays;
    }

    public String getProcessCode() {
        return processCode;
    }

    public Integer getAmount() {
        return amount;
    }

    public Integer getAverageTimeInDays() {
        return averageTimeInDays;
    }
}
