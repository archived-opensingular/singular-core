package br.net.mirante.singular.flow.core.entity;

public interface IEntityProcessRole extends IEntityByCod {

    String getNome();

    void setNome(String nome);

    String getSigla();

    void setSigla(String sigla);

    IEntityProcess getDefinicao();

}
