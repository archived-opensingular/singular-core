package br.net.mirante.singular.dto;

import br.net.mirante.singular.flow.core.dto.IStatusDTO;

public class StatusDTO implements IStatusDTO {

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

    @Override
    public String getProcessCode() {
        return processCode;
    }

    @Override
    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    @Override
    public Integer getAmount() {
        return abs(amount);
    }

    @Override
    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public Integer getAverageTimeInDays() {
        return abs(averageTimeInDays);
    }

    @Override
    public void setAverageTimeInDays(Integer averageTimeInDays) {
        this.averageTimeInDays = averageTimeInDays;
    }

    @Override
    public Integer getOpenedInstancesLast30Days() {
        return abs(openedInstancesLast30Days);
    }

    @Override
    public void setOpenedInstancesLast30Days(Integer openedInstancesLast30Days) {
        this.openedInstancesLast30Days = openedInstancesLast30Days;
    }

    @Override
    public Integer getFinishedInstancesLast30Days() {
        return abs(finishedInstancesLast30Days);
    }

    @Override
    public void setFinishedInstancesLast30Days(Integer finishedInstancesLast30Days) {
        this.finishedInstancesLast30Days = finishedInstancesLast30Days;
    }

    private Integer abs(Integer i) {
        if (i != null) {
            return Math.abs(i);
        }
        return 0;
    }
}
