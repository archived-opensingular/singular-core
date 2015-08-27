package br.net.mirante.singular.flow.worklist;

import java.util.Date;
import java.util.List;

import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.util.view.Lnk;

/**
 * @deprecated não é referenciado no core, deve ser removido
 */
@Deprecated
//TODO remover
public interface Tarefa {

    String getFullId();

    String getNomeProcesso();

    String getNomeTarefa();

    String getDescricao();

    Date getDataInicio();

    Date getDataFim();

    Date getDataAlvoFim();

    List<MUser> getResponsaveisDiretos();

    Lnk getHrefPadrao();
}
