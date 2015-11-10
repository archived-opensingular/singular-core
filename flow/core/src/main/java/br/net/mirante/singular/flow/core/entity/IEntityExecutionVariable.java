package br.net.mirante.singular.flow.core.entity;

import java.util.Date;

public interface IEntityExecutionVariable extends IEntityByCod<Integer> {

    IEntityProcessInstance getProcessInstance();

    String getName();

    void setName(String name);

    IEntityTaskInstance getOriginTask();

    IEntityTaskInstance getDestinationTask();

    String getValue();

    void setValue(String value);

    Date getDate();

    void setDate(Date date);
}