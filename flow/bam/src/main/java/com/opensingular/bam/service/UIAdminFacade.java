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

import com.opensingular.bam.dto.FeedDTO;
import com.opensingular.bam.dto.MenuItemDTO;
import com.opensingular.bam.dto.StatusDTO;
import com.opensingular.bam.support.persistence.dto.DefinitionDTO;
import com.opensingular.bam.support.persistence.dto.MetaDataDTO;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import com.opensingular.bam.dto.InstanceDTO;

import org.opensingular.flow.core.service.IUIAdminService;
import org.opensingular.flow.persistence.entity.Dashboard;

@Service("uiAdminFacade")
public class UIAdminFacade implements IUIAdminService<DefinitionDTO, InstanceDTO, MetaDataDTO, StatusDTO,
        FeedDTO, MenuItemDTO> {

    @Inject
    private ProcessDefinitionService processDefinitionService;

    @Inject
    private PesquisaService pesquisaService;

    @Inject
    private FeedService feedService;

    @Inject
    private MenuService menuService;

    @Inject
    private DashboardService dashboardService;

    @Value("${user.avatar.url}")
    private String userAvatar;

    @Value("${springsecurity.logout}")
    private String logoutUrl;

    @Override
    public DefinitionDTO retrieveDefinitionById(Integer processDefinitionCod) {
        return processDefinitionService.retrieveById(processDefinitionCod);
    }

    @Override
    public DefinitionDTO retrieveDefinitionByKey(String processDefinitionKey) {
        return processDefinitionService.retrieveByKey(processDefinitionKey);
    }

    @Override
    public List<DefinitionDTO> retrieveAllDefinition(int first, int size, String orderByProperty, boolean asc, Set<String> processCodeWithAccess) {
        return processDefinitionService.retrieveAll(first, size, orderByProperty, asc, processCodeWithAccess);
    }

    @Override
    public int countAllDefinition(Set<String> processCodeWithAccess) {
        return processDefinitionService.countAll(processCodeWithAccess);
    }

    @Override
    public List<InstanceDTO> retrieveAllInstance(int first, int size, String orderByProperty, boolean asc, Integer processDefinitionCod) {
        return processDefinitionService.retrieveAll(first, size, orderByProperty, asc, processDefinitionCod);
    }

    @Override
    public int countAllInstance(Integer processDefinitionCod) {
        return processDefinitionService.countAll(processDefinitionCod);
    }

    @Override
    public List<MetaDataDTO> retrieveMetaData(Integer processDefinitionCod) {
        return processDefinitionService.retrieveMetaData(processDefinitionCod);
    }

    @Override
    public List<Map<String, String>> retrieveMeanTimeByProcess(Period period, String processCode, Set<String> processCodeWithAccess) {
        return pesquisaService.retrieveMeanTimeByProcess(period, processCode, processCodeWithAccess);
    }

    @Override
    public List<Map<String, String>> retrieveNewInstancesQuantityLastYear(String processCode, Set<String> processCodeWithAccess) {
        return pesquisaService.retrieveNewInstancesQuantityLastYear(processCode, processCodeWithAccess);
    }

    @Override
    public List<Map<String, String>> retrieveEndStatusQuantityByPeriod(Period period, String processCode) {
        return pesquisaService.retrieveEndStatusQuantityByPeriod(period, processCode);
    }

    @Override
    public List<Map<String, String>> retrieveMeanTimeByTask(Period period, String processCode) {
        return pesquisaService.retrieveMeanTimeByTask(period, processCode);
    }

    @Override
    public List<Map<String, String>> retrieveStatsByActiveTask(String processDefinitionCode) {
        return pesquisaService.retrieveStatsByActiveTask(processDefinitionCode);
    }

    @Override
    public StatusDTO retrieveActiveInstanceStatus(String processCode, Set<String> processCodeWithAccess) {
        return pesquisaService.retrieveActiveInstanceStatus(processCode, processCodeWithAccess);
    }

    @Override
    public List<Map<String, String>> retrieveMeanTimeActiveInstances(String processCode, Set<String> processCodeWithAccess) {
        return pesquisaService.retrieveMeanTimeActiveInstances(processCode, processCodeWithAccess);
    }

    @Override
    public List<Map<String, String>> retrieveAverageTimesActiveInstances(String processCode, Set<String> processCodeWithAccess) {
        return pesquisaService.retrieveAverageTimesActiveInstances(processCode, processCodeWithAccess);
    }

    @Override
    public List<Map<String, String>> retrieveMeanTimeFinishedInstances(String processCode, Set<String> processCodeWithAccess) {
        return pesquisaService.retrieveMeanTimeFinishedInstances(processCode, processCodeWithAccess);
    }

    @Override
    public List<Map<String, String>> retrieveCounterActiveInstances(String processCode, Set<String> processCodeWithAccess) {
        return pesquisaService.retrieveCounterActiveInstances(processCode, processCodeWithAccess);
    }

    @Override
    public String retrieveProcessDefinitionName(String processCode) {
        return pesquisaService.retrieveProcessDefinitionName(processCode);
    }

    @Override
    public String retrieveProcessDefinitionId(String processDefinitionCode) {
        return pesquisaService.retrieveProcessDefinitionId(processDefinitionCode);
    }

    @Override
    public List<FeedDTO> retrieveAllFeed(String processCode, Set<String> processCodeWithAccess) {
        return feedService.retrieveFeed(processCode, processCodeWithAccess);
    }

    @Override
    public List<MenuItemDTO> retrieveAllCategories() {
        return menuService.retrieveAllCategories();
    }

    @Override
    public List<MenuItemDTO> retrieveAllCategoriesWithAcces(String userId) {
        return menuService.retrieveAllCategoriesWithAcces(userId);
    }

    @Override
    public Pair<Long, Long> retrieveCategoryDefinitionIdsByCode(String code) {
        return menuService.retrieveCategoryDefinitionIdsByCode(code);
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public List<Dashboard> retrieveCustomDashboards() {
        return dashboardService.retrieveCustomDashboards();
    }

    public Dashboard retrieveDashboardById(String customDashboardCode) {
        return dashboardService.retrieveDashboardById(customDashboardCode);
    }
}
