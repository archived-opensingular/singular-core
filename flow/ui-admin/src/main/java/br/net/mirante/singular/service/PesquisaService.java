package br.net.mirante.singular.service;

import java.time.Period;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.net.mirante.singular.dao.InstanceDAO;
import br.net.mirante.singular.dao.PesquisaDAO;

@Service
@Transactional(readOnly = true)
public class PesquisaService {

    @Inject
    private PesquisaDAO pesquisaDAO;

    @Inject
    private InstanceDAO instanceDAO;

    public List<Map<String, String>> retrieveMeanTimeByProcess(Period period) {
        return pesquisaDAO.retrieveMeanTimeByProcess(period);
    }

    public List<Map<String, String>> retrieveNewInstancesQuantityLastYear() {
        return instanceDAO.retrieveNewQuantityLastYear();
    }
    public List<Map<String, String>> retrieveMeanTimeByTask(Long processId) {
        return pesquisaDAO.retrieveMeanTimeByTask(processId);
    }
}
