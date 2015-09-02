package br.net.mirante.singular.flow.core.entity;


public interface IEntityTaskTransition extends IEntityByCod {

    IEntityTask getOriginTask();

    IEntityTask getDestinationTask();
    
    String getName();

    String getAbbreviation();

    TransitionType getType();

}
