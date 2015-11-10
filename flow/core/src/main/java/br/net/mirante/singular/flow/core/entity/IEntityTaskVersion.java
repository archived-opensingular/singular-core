package br.net.mirante.singular.flow.core.entity;

import java.util.Date;
import java.util.List;

import br.net.mirante.singular.flow.core.IEntityTaskType;

public interface IEntityTaskVersion extends IEntityByCod<Integer> {

    IEntityProcessVersion getProcessVersion();

    String getName();

    void setName(String name);

    IEntityTaskType getType();

    IEntityTaskDefinition getTaskDefinition();

    List<? extends IEntityTaskTransitionVersion> getTransitions();

    default Date getVersionDate(){
        return getProcessVersion().getVersionDate();
    }

    default IEntityTaskTransitionVersion getTransition(String abbreviation) {
        for (IEntityTaskTransitionVersion entityTaskTransition : getTransitions()) {
            if (entityTaskTransition.getAbbreviation().equalsIgnoreCase(abbreviation)) {
                return entityTaskTransition;
            }
        }
        return null;
    }

    default String getAbbreviation(){
        return getTaskDefinition().getAbbreviation();
    }

    default boolean isEnd() {
        return getType().isEnd();
    }

    default boolean isPeople() {
        return getType().isPeople();
    }

    default boolean isWait() {
        return getType().isWait();
    }

    default boolean isJava() {
        return getType().isJava();
    }

    default String getDetail() {
        return "(" + getType().getAbbreviation() + ") " + getName();
    }
}
