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
import br.net.mirante.singular.dao.StatusDTO;

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
    @Cacheable(value = "retrieveNewInstancesQuantityLastYear",
            key = "#processCode?:'NULL'",
            cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveNewInstancesQuantityLastYear(String processCode) {
        return instanceDAO.retrieveNewQuantityLastYear(processCode);
    }

    @Override
    @Cacheable(value = "retrieveEndStatusQuantityByPeriod",
            key = "#period.toString().concat(#processCode)",
            cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveEndStatusQuantityByPeriod(Period period, String processCode) {
        return instanceDAO.retrieveEndStatusQuantityByPeriod(period, processCode);
    }

    @Override
    @Cacheable(value = "retrieveMeanTimeByTask",
            key = "#period.toString().concat(#processCode)",
            cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveMeanTimeByTask(Period period, String processCode) {
        return pesquisaDAO.retrieveMeanTimeByTask(period, processCode);
    }

    @Override
    @Cacheable(value = "retrieveActiveInstanceStatus", key = "#processCode?:'NULL'", cacheManager = "cacheManager")
    public StatusDTO retrieveActiveInstanceStatus(String processCode) {
        return instanceDAO.retrieveActiveInstanceStatus(processCode);
    }

    @Override
    @Cacheable(value = "retrieveMeanTimeActiveInstances", key = "#processCode", cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveMeanTimeActiveInstances(String processCode) {
        return instanceDAO.retrieveMeanTimeActiveInstances(processCode);
    }

    @Override
    @Cacheable(value = "retrieveMeanTimeFinishedInstances", key = "#processCode", cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveMeanTimeFinishedInstances(String processCode) {
        return instanceDAO.retrieveMeanTimeFinishedInstances(processCode);
    }
}
