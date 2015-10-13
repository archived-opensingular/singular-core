package br.net.mirante.singular.flow.core.entity;


public interface IEntityTaskTransition extends IEntityByCod {

    IEntityTaskVersion getOriginTask();

    IEntityTaskVersion getDestinationTask();

    String getName();

    void setName(String name);

    String getAbbreviation();

    void setAbbreviation(String abbreviation);

    TransitionType getType();

    void setType(TransitionType type);

}
