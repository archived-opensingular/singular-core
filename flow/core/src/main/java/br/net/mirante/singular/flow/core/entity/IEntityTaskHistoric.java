package br.net.mirante.singular.flow.core.entity;

import java.util.Date;

import br.net.mirante.singular.flow.core.MUser;

public interface IEntityTaskHistoric extends IEntityByCod {

    Date getData();

    void setData(Date data);

    MUser getPessoaAlocada();

    void setPessoaAlocada(MUser pessoaAlocada);

    MUser getPessoaAlocadora();

    void setPessoaAlocadora(MUser pessoaAlocadora);

    String getTextoDetalhamento();

    void setTextoDetalhamento(String textoDetalhamento);

    IEntityTaskInstance getTarefa();

    IEntityProcessInstance getDemanda();

    String getDescricaoTipo();
}
