package br.net.mirante.singular.flow.core.entity;

import java.util.Date;
import java.util.List;

import br.net.mirante.singular.flow.core.IEntityTaskType;
import br.net.mirante.singular.flow.core.TaskType;

public interface IEntityTaskVersion extends IEntityByCod {

    IEntityProcessVersion getProcess();
    
    String getName();

    IEntityTaskType getType();

    IEntityTaskDefinition getTaskDefinition();

    List<? extends IEntityTaskTransition> getTransitions();
    
    default Date getVersionDate(){
        return getProcess().getVersionDate();
    }
    
    default IEntityTaskTransition getTransition(String abbreviation) {
        for (IEntityTaskTransition entityTaskTransition : getTransitions()) {
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
        return TaskType.End.equals(getType());
    }

    default boolean isPeople() {
        return TaskType.People.equals(getType());
    }

    default boolean isWait() {
        return TaskType.Wait.equals(getType());
    }

    default boolean isJava() {
        return TaskType.Java.equals(getType());
    }

    default String getDetail() {
        return "(" + getType().getAbbreviation() + ") " + getName();
    }
}
