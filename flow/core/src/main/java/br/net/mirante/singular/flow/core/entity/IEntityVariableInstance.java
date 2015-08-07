package br.net.mirante.singular.flow.core.entity;

public interface IEntityVariableInstance extends IEntityByCod {

    String getNome();

    void setNome(String nome);

    String getTextoValor();

    void setTextoValor(String textoValor);

    IEntityProcessInstance getDemanda();

}
