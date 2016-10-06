/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.service;

import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import com.opensingular.bam.dao.PesquisaDAO;
import com.opensingular.bam.dto.StatusDTO;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.opensingular.bam.dao.InstanceDAO;

@Service("pesquisaService")
@Transactional(readOnly = true)
public class PesquisaServiceImpl implements PesquisaService {

    @Inject
    private PesquisaDAO pesquisaDAO;

    @Inject
    private InstanceDAO instanceDAO;

    @Override
    @Cacheable(value = "retrieveMeanTimeByProcess", cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveMeanTimeByProcess(Period period, String processCode, Set<String> processCodeWithAccess) {
        return pesquisaDAO.retrieveMeanTimeByProcess(period, processCode, processCodeWithAccess);
    }

    @Override
    @Cacheable(value = "retrieveNewInstancesQuantityLastYear", cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveNewInstancesQuantityLastYear(String processCode, Set<String> processCodeWithAccess) {
        return instanceDAO.retrieveTransactionQuantityLastYear(processCode, processCodeWithAccess);
    }

    @Override
    @Cacheable(value = "retrieveEndStatusQuantityByPeriod", cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveEndStatusQuantityByPeriod(Period period, String processCode) {
        return instanceDAO.retrieveEndStatusQuantityByPeriod(period, processCode);
    }

    @Override
    @Cacheable(value = "retrieveMeanTimeByTask", cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveMeanTimeByTask(Period period, String processCode) {
        return pesquisaDAO.retrieveMeanTimeByTask(period, processCode);
    }

    @Override
    @Cacheable(value = "retrieveStatsByActiveTask", key = "#processCode", cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveStatsByActiveTask(String processCode) {
        return pesquisaDAO.retrieveStatsByActiveTask(processCode);
    }

    @Override
    @Cacheable(value = "retrieveActiveInstanceStatus", cacheManager = "cacheManager")
    public StatusDTO retrieveActiveInstanceStatus(String processCode, Set<String> processCodeWithAccess) {
        return instanceDAO.retrieveActiveInstanceStatus(processCode);
    }

    @Override
    @Cacheable(value = "retrieveMeanTimeActiveInstances", key = "#processCode", cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveMeanTimeActiveInstances(String processCode, Set<String> processCodeWithAccess) {
        return instanceDAO.retrieveMeanTimeActiveInstances(processCode, processCodeWithAccess);
    }

    @Override
    @Cacheable(value = "retrieveAverageTimesActiveInstances", key = "#processCode", cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveAverageTimesActiveInstances(String processCode, Set<String> processCodeWithAccess) {
        return instanceDAO.retrieveAverageTimesActiveInstances(processCode, processCodeWithAccess);
    }

    @Override
    @Cacheable(value = "retrieveMeanTimeFinishedInstances", key = "#processCode", cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveMeanTimeFinishedInstances(String processCode, Set<String> processCodeWithAccess) {
        return instanceDAO.retrieveMeanTimeFinishedInstances(processCode, processCodeWithAccess);
    }

    @Override
    @Cacheable(value = "retrieveCounterActiveInstances", cacheManager = "cacheManager")
    public List<Map<String, String>> retrieveCounterActiveInstances(String processCode, Set<String> processCodeWithAccess) {
        return instanceDAO.retrieveCounterActiveInstances(processCode, processCodeWithAccess);
    }

    @Override
    @Cacheable(value = "retrieveProcessDefinitionName", key = "#processCode", cacheManager = "cacheManager")
    public String retrieveProcessDefinitionName(String processCode) {
        return pesquisaDAO.retrieveProcessDefinitionName(processCode);
    }

    @Override
    @Cacheable(value = "retrieveProcessDefinitionId", key = "#processCode", cacheManager = "cacheManager")
    public String retrieveProcessDefinitionId(String processCode) {
        return pesquisaDAO.retrieveProcessDefinitionId(processCode).toString();
    }
}
