package br.net.mirante.singular.dao;

import java.io.Serializable;

public class StatusDTO implements Serializable {

    private String processCode;
    private Integer amount;
    private Integer averageTimeInDays;

    public StatusDTO() {
        /* CONSTRUTOR VAZIO */
    }

    public StatusDTO(String processCode, Integer amount, Integer averageTimeInDays) {
        this.processCode = processCode;
        this.amount = amount;
        this.averageTimeInDays = averageTimeInDays;
    }

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getAverageTimeInDays() {
        return averageTimeInDays;
    }

    public void setAverageTimeInDays(Integer averageTimeInDays) {
        this.averageTimeInDays = averageTimeInDays;
    }
}
