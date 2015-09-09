package br.net.mirante.singular.flow.core.entity;

import java.util.Date;

import br.net.mirante.singular.flow.core.MUser;

public interface IEntityTaskHistoric extends IEntityByCod {
    
    IEntityTaskInstance getTaskInstance();

    Date getBeginDateAllocation();

    Date getEndDateAllocation();

    MUser getAllocatedUser();

    MUser getAllocatorUser();

    String getDescription();

    IEntityTaskHistoricType getType();
}
