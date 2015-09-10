package br.net.mirante.singular.flow.core.entity;

import java.util.Date;

public interface IEntityExecutionVariable extends IEntityByCod {
    
    IEntityProcessInstance getProcessInstance();
    
    String getName();

    IEntityVariableInstance getVariable();
    
    IEntityTaskInstance getOriginTask();
    
    IEntityTaskInstance getDestinationTask();

    String getValue();
    
    Date getDate();

}