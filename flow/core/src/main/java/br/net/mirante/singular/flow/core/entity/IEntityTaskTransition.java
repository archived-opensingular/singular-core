package br.net.mirante.singular.flow.core.entity;


public interface IEntityTaskTransition extends IEntityByCod {

    IEntityTaskVersion getOriginTask();

    IEntityTaskVersion getDestinationTask();
    
    String getName();

    String getAbbreviation();

    TransitionType getType();

}
