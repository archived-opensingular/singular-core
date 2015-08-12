package br.net.mirante.singular.flow.core.entity;

import br.net.mirante.singular.flow.core.TaskType;

public interface IEntityTaskDefinition extends IEntityByCod {

    String getNome();

    String getSigla();

    TaskType getTipoTarefa();

    IEntityProcess getDefinicao();

    default boolean isFim() {
        return TaskType.End.equals(getTipoTarefa());
    }

    default boolean isPessoa() {
        return TaskType.People.equals(getTipoTarefa());
    }

    default boolean isWait() {
        return TaskType.Wait.equals(getTipoTarefa());
    }

    default boolean isJava() {
        return TaskType.Java.equals(getTipoTarefa());
    }

    default String getDescricao() {
        return "(" + getTipoTarefa().getAbbreviation() + ") " + getNome();
    }
}
