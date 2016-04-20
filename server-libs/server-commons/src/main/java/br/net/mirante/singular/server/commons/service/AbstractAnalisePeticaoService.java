package br.net.mirante.singular.server.commons.service;

import br.net.mirante.singular.server.commons.dao.ITaskInstanceDAO;
import br.net.mirante.singular.server.commons.dto.ITaskInstanceDTO;
import br.net.mirante.singular.server.commons.wicket.SingularSession;
import org.apache.commons.lang3.math.NumberUtils;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
public abstract class AbstractAnalisePeticaoService<T extends ITaskInstanceDTO> implements IAnalisePeticaoService<T> {

    @Inject
    private ITaskInstanceDAO<T> taskInstanceDAO;

    private List<Long> getIdsPerfis() {
        return SingularSession.get().getRoles().stream().map(s -> NumberUtils.toLong(s, 0)).filter(i -> i != 0).collect(Collectors.toList());
    }

    @Override
    public List<T> listTasks(int first, int count, String sortProperty, boolean ascending, String siglaFluxo, String filtroRapido, boolean concluidas) {
        return taskInstanceDAO.findTasks(first, count, sortProperty, ascending, siglaFluxo, getIdsPerfis(), filtroRapido, concluidas);
    }

    @Override
    public Integer countTasks(String siglaFluxo, String filtroRapido, boolean concluidas) {
        return taskInstanceDAO.countTasks(siglaFluxo, getIdsPerfis(), filtroRapido, concluidas);
    }

}