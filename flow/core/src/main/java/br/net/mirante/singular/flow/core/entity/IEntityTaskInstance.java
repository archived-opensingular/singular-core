package br.net.mirante.singular.flow.core.entity;

import java.util.Date;
import java.util.List;

import br.net.mirante.singular.flow.core.MUser;

public interface IEntityTaskInstance extends IEntityByCod {

    boolean isSuspensa();

    Date getDataAlvoSuspensao();

    void setDataAlvoSuspensao(Date dataAlvoSuspensao);

    Date getDataInicio();

    Date getDataAlvoFim();

    void setDataAlvoFim(Date dataAlvoFim);

    void setDataInicio(Date dataInicio);

    Date getDataFim();

    void setDataFim(Date dataFim);

    MUser getPessoaAlocada();

    void setPessoaAlocada(MUser pessoaAlocada);

    MUser getAutorFim();

    void setAutorFim(MUser autorFim);

    String getSiglaTransicaoResultado();

    void setSiglaTransicaoResultado(String siglaTransicaoResultado);

    List<? extends IEntityTaskHistoric> getHistoricoAlocacao();

    List<? extends IEntityVariable> getVariaveisGeradas();

    List<? extends IEntityVariable> getVariaveisEntrada();

    IEntityProcessInstance getDemanda();

    IEntityTaskDefinition getSituacao();
}
