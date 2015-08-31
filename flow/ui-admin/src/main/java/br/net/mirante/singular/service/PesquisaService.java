package br.net.mirante.singular.service;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value="retrieveMeanTimeByProcess", key = "#period")
    public List<Map<String, String>> retrieveMeanTimeByProcess(Period period) {
        return pesquisaDAO.retrieveMeanTimeByProcess(period);
    }

    @Cacheable(value="retrieveNewInstancesQuantityLastYear")
    public List<Map<String, String>> retrieveNewInstancesQuantityLastYear() {
        return instanceDAO.retrieveNewQuantityLastYear();
    }

    @Cacheable(value="retrieveStatusQuantityByPeriod", key = "#period")
    public List<Map<String, String>> retrieveStatusQuantityByPeriod(Period period) {
        return instanceDAO.retrieveStatusQuantityByPeriod(period, 26L, new ArrayList<Long>() {{
            add(436L);
        }});
    }
    @Cacheable(value="retrieveMeanTimeByTask", key = "#processId")
    public List<Map<String, String>> retrieveMeanTimeByTask(Long processId) {
        return pesquisaDAO.retrieveMeanTimeByTask(processId);
    }
}
