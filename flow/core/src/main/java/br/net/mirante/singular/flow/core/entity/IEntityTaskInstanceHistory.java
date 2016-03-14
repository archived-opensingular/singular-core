package br.net.mirante.singular.flow.core.entity;

import java.util.Date;

import br.net.mirante.singular.flow.core.MUser;

public interface IEntityTaskInstanceHistory extends IEntityByCod<Integer> {

    IEntityTaskInstance getTaskInstance();

    Date getBeginDateAllocation();

    void setBeginDateAllocation(Date begin);

    Date getEndDateAllocation();

    void setEndDateAllocation(Date endDateAllocation);

    MUser getAllocatedUser();

    MUser getAllocatorUser();

    String getDescription();

    void setDescription(String description);

    IEntityTaskHistoricType getType();
}
