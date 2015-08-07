package br.net.mirante.singular.flow.core.entity;

import java.util.Date;

import br.net.mirante.singular.flow.core.MUser;

public interface IEntityRole extends IEntityByCod {

    MUser getPessoa();

    void setPessoa(MUser pessoa);

    Date getDataCriacao();

    void setDataCriacao(Date dataCriacao);

    MUser getPessoaAtribuidora();

    void setPessoaAtribuidora(MUser pessoaAtribuidora);

    IEntityProcessRole getPapel();

    IEntityProcessInstance getDemanda();

}
