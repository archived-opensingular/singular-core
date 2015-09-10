package br.net.mirante.singular.dao;

import java.io.Serializable;

public class StatusDTO implements Serializable {

    private String processCode;
    private Integer amount;
    private Integer averageTimeInDays;
    private Integer openedInstancesLast30Days;
    private Integer finishedInstancesLast30Days;

    public StatusDTO() {
        /* CONSTRUTOR VAZIO */
    }

    public StatusDTO(String processCode, Integer amount, Integer averageTimeInDays, Integer openedInstancesLast30Days,
            Integer finishedInstancesLast30Days) {
        this.processCode = processCode;
        this.amount = amount;
        this.averageTimeInDays = averageTimeInDays;
        this.openedInstancesLast30Days = openedInstancesLast30Days;
        this.finishedInstancesLast30Days = finishedInstancesLast30Days;
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

    public Integer getOpenedInstancesLast30Days() {
        return openedInstancesLast30Days;
    }

    public void setOpenedInstancesLast30Days(Integer openedInstancesLast30Days) {
        this.openedInstancesLast30Days = openedInstancesLast30Days;
    }

    public Integer getFinishedInstancesLast30Days() {
        return finishedInstancesLast30Days;
    }

    public void setFinishedInstancesLast30Days(Integer finishedInstancesLast30Days) {
        this.finishedInstancesLast30Days = finishedInstancesLast30Days;
    }
}
