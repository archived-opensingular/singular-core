package br.net.mirante.singular.server.commons.dao;

import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.server.commons.dto.ITaskInstanceDTO;

import java.util.List;

public interface ITaskInstanceDAO<T extends ITaskInstanceDTO> extends Loggable {

    List<T> findTasks(int first, int count, String sortProperty, boolean ascending, String siglaFluxo, List<Long> idsPerfis, String filtroRapido, boolean concluidas);

    Integer countTasks(String siglaFluxo, List<Long> idsPerfis, String filtroRapido, boolean concluidas);
}
