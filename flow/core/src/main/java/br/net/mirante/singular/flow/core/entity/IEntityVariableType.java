package br.net.mirante.singular.flow.core.entity;

public interface IEntityVariableType extends IEntityByCod<Integer> {

    String getTypeClassName();

    void setTypeClassName(String typeClassName);

    String getDescription();

    void setDescription(String description);

}
