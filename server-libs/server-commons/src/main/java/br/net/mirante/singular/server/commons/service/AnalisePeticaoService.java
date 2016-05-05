package br.net.mirante.singular.server.commons.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.server.commons.persistence.dao.flow.TaskInstanceDAO;
import br.net.mirante.singular.server.commons.persistence.dto.TaskInstanceDTO;
import br.net.mirante.singular.server.commons.wicket.SingularSession;

@Transactional
public class AnalisePeticaoService<T extends TaskInstanceDTO> {

    @Inject
    private TaskInstanceDAO taskInstanceDAO;

    private List<String> getIdsPerfis() {
        return SingularSession.get().getRoles().stream().collect(Collectors.toList());
    }


    public List<? extends TaskInstanceDTO> listTasks(int first, int count, String sortProperty, boolean ascending, String siglaFluxo, String filtroRapido, boolean concluidas) {
        return taskInstanceDAO.findTasks(first, count, sortProperty, ascending, siglaFluxo, getIdsPerfis(), filtroRapido, concluidas);
    }


    public Integer countTasks(String siglaFluxo, String filtroRapido, boolean concluidas) {
        return taskInstanceDAO.countTasks(siglaFluxo, getIdsPerfis(), filtroRapido, concluidas);
    }


    public TaskInstance findCurrentTaskByPetitionId(String petitionId) {
        List<TaskInstance> taskInstances = taskInstanceDAO.findCurrentTasksByPetitionId(petitionId);
        return taskInstances.get(0);
    }
}