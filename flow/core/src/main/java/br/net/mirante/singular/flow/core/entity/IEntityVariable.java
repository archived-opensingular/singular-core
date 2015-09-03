package br.net.mirante.singular.flow.core.entity;

import java.util.Date;

public interface IEntityVariable extends IEntityByCod {

    Date getData();

    void setData(Date data);

    String getName();

    void setName(String nome);

    String getValue();

    void setValue(String textoValor);

    IEntityTaskInstance getTarefaOrigem();

    IEntityTaskInstance getTarefaDestino();

    IEntityProcessInstance getProcessInstance();

}