package br.net.mirante.singular.flow.core.entity;

import java.util.Date;

public interface IEntityVariable extends IEntityByCod {

    Date getData();

    void setData(Date data);

    String getNome();

    void setNome(String nome);

    String getTextoValor();

    void setTextoValor(String textoValor);

    IEntityTaskInstance getTarefaOrigem();

    IEntityTaskInstance getTarefaDestino();

    IEntityProcessInstance getDemanda();

}