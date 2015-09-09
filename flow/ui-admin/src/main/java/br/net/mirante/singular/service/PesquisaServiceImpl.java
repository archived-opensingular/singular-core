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

@Service("pesquisaService")
@Transactional(readOnly = true)
public class PesquisaServiceImpl implements PesquisaService {

    @Inject
    private PesquisaDAO pesquisaDAO;

    @Inject
    private InstanceDAO instanceDAO;

    @Override
    @Cacheable(value = "retrieveMeanTimeByProcess", key = "#period", cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveMeanTimeByProcess(Period period) {
        return pesquisaDAO.retrieveMeanTimeByProcess(period);
    }

    @Override
    @Cacheable(value = "retrieveNewInstancesQuantityLastYear", cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveNewInstancesQuantityLastYear() {
        return instanceDAO.retrieveNewQuantityLastYear();
    }

    @Override
    @Cacheable(value = "retrieveStatusQuantityByPeriod", key = "#period", cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveStatusQuantityByPeriod(Period period) {
        return instanceDAO.retrieveStatusQuantityByPeriod(period, 26L, new ArrayList<Long>() {{
            add(436L);
        }});
    }

    @Override
    @Cacheable(value = "retrieveMeanTimeByTask", key = "#processCode", cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveMeanTimeByTask(String processCode) {
        return pesquisaDAO.retrieveMeanTimeByTask(processCode);
    }
}
