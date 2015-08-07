package br.net.mirante.singular.flow.core.entity;

public interface IEntityCategory extends IEntityByCod {

    String getNome();

    void setNome(String nome);

    String getNomeAbsoluto();

    void setNomeAbsoluto(String nomeAbsoluto);

    IEntityCategory getPai();

}
