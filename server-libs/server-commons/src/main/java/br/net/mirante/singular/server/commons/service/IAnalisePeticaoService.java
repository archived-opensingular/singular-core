package br.net.mirante.singular.server.commons.service;

import br.net.mirante.singular.server.commons.dto.ITaskInstanceDTO;

import java.util.List;

public interface IAnalisePeticaoService<T extends ITaskInstanceDTO> {

    List<T> listTasks(int first, int count, String sortProperty, boolean ascending, String siglaFluxo, String filtroRapido, boolean concluidas);

    Integer countTasks(String siglaFluxo, String filtroRapido, boolean concluidas);
}
