package br.net.mirante.singular.service;

import java.time.Period;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import br.net.mirante.singular.dao.DefinitionDTO;
import br.net.mirante.singular.dao.FeedDTO;
import br.net.mirante.singular.dao.InstanceDTO;
import br.net.mirante.singular.dao.MenuItemDTO;
import br.net.mirante.singular.dao.MetaDataDTO;
import br.net.mirante.singular.dao.StatusDTO;
import br.net.mirante.singular.flow.core.service.IUIAdminService;

@Service("uiAdminFacade")
public class UIAdminFacade implements IUIAdminService<DefinitionDTO, InstanceDTO, MetaDataDTO, StatusDTO,
        FeedDTO, MenuItemDTO> {

    @Inject
    private FeedService feedService;

    @Inject
    private MenuService menuService;

    @Override
    public DefinitionDTO retrieveDefinitionById(Long id) {
        return null;
    }

    @Override
    public List<DefinitionDTO> retrieveAllDefinition(int first, int size, String orderByProperty, boolean asc) {
        return null;
    }

    @Override
    public int countAllDefinition() {
        return 0;
    }

    @Override
    public List<InstanceDTO> retrieveAllInstance(int first, int size, String orderByProperty, boolean asc, Long id) {
        return null;
    }

    @Override
    public int countAllInstance(Long id) {
        return 0;
    }

    @Override
    public byte[] retrieveProcessDiagram(String sigla) {
        return new byte[0];
    }

    @Override
    public List<MetaDataDTO> retrieveMetaData(Long id) {
        return null;
    }

    @Override
    public List<Map<String, String>> retrieveMeanTimeByProcess(Period period) {
        return null;
    }

    @Override
    public List<Map<String, String>> retrieveNewInstancesQuantityLastYear(String processCode) {
        return null;
    }

    @Override
    public List<Map<String, String>> retrieveEndStatusQuantityByPeriod(Period period, String processCode) {
        return null;
    }

    @Override
    public List<Map<String, String>> retrieveMeanTimeByTask(Period period, String processCode) {
        return null;
    }

    @Override
    public List<Map<String, String>> retrieveCountByTask(String processDefinitionCode) {
        return null;
    }

    @Override
    public StatusDTO retrieveActiveInstanceStatus(String processCode) {
        return null;
    }

    @Override
    public List<Map<String, String>> retrieveMeanTimeActiveInstances(String processCode) {
        return null;
    }

    @Override
    public List<Map<String, String>> retrieveMeanTimeFinishedInstances(String processCode) {
        return null;
    }

    @Override
    public List<Map<String, String>> retrieveCounterActiveInstances(String processCode) {
        return null;
    }

    @Override
    public String retrieveProcessDefinitionName(String processCode) {
        return null;
    }

    @Override
    public String retrieveProcessDefinitionId(String processDefinitionCode) {
        return null;
    }

    @Override
    public List<FeedDTO> retrieveAllFeed() {
        return feedService.retrieveFeed();
    }

    @Override
    public List<MenuItemDTO> retrieveAllCategories() {
        return menuService.retrieveAllCategories();
    }

    @Override
    public Pair<Long, Long> retrieveCategoryDefinitionIdsByCode(String code) {
        return null;
    }
}
