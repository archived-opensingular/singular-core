package br.net.mirante.singular.server.commons.service;

import br.net.mirante.singular.server.commons.persistence.dao.flow.TaskInstanceDAO;
import br.net.mirante.singular.server.commons.persistence.dto.TaskInstanceDTO;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import org.apache.commons.lang3.math.NumberUtils;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
public class AnalisePeticaoService<T extends TaskInstanceDTO> {

    @Inject
    private TaskInstanceDAO<T> taskInstanceDAO;

    private List<Long> getIdsPerfis() {
        return SingularSession.get().getRoles().stream().map(s -> NumberUtils.toLong(s, 0)).filter(i -> i != 0).collect(Collectors.toList());
    }


    public List<T> listTasks(int first, int count, String sortProperty, boolean ascending, String siglaFluxo, String filtroRapido, boolean concluidas) {
        return taskInstanceDAO.findTasks(first, count, sortProperty, ascending, siglaFluxo, getIdsPerfis(), filtroRapido, concluidas);
    }


    public Integer countTasks(String siglaFluxo, String filtroRapido, boolean concluidas) {
        return taskInstanceDAO.countTasks(siglaFluxo, getIdsPerfis(), filtroRapido, concluidas);
    }


}