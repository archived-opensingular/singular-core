package br.net.mirante.singular.flow.core.service;

import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import br.net.mirante.singular.flow.core.dto.IDefinitionDTO;
import br.net.mirante.singular.flow.core.dto.IFeedDTO;
import br.net.mirante.singular.flow.core.dto.IInstanceDTO;
import br.net.mirante.singular.flow.core.dto.IMenuItemDTO;
import br.net.mirante.singular.flow.core.dto.IMetaDataDTO;
import br.net.mirante.singular.flow.core.dto.IStatusDTO;

public interface IUIAdminService<DEFINITION extends IDefinitionDTO, INSTANCE extends IInstanceDTO,
        METADATA extends IMetaDataDTO, STATUS extends IStatusDTO, FEED extends IFeedDTO, MENU extends IMenuItemDTO> {

    DEFINITION retrieveDefinitionById(Integer processDefinitionCod);
    
    DEFINITION retrieveDefinitionByKey(String processDefinitionKey);

    List<DEFINITION> retrieveAllDefinition(int first, int size, String orderByProperty, boolean asc, Set<String> processCodeWithAccess);

    int countAllDefinition(Set<String> processCodeWithAccess);

    List<INSTANCE> retrieveAllInstance(int first, int size, String orderByProperty, boolean asc, Integer processDefinitionCod);

    int countAllInstance(Integer processDefinitionCod);

    List<METADATA> retrieveMetaData(Integer processDefinitionCod);

    List<Map<String, String>> retrieveMeanTimeByProcess(Period period, String processCode, Set<String> processCodeWithAccess);

    List<Map<String, String>> retrieveNewInstancesQuantityLastYear(String processCode, Set<String> processCodeWithAccess);

    List<Map<String, String>> retrieveEndStatusQuantityByPeriod(Period period, String processCode);

    List<Map<String, String>> retrieveMeanTimeByTask(Period period, String processCode);

    List<Map<String, String>> retrieveStatsByActiveTask(String processDefinitionCode);

    STATUS retrieveActiveInstanceStatus(String processCode, Set<String> processCodeWithAccess);

    List<Map<String, String>> retrieveMeanTimeActiveInstances(String processCode, Set<String> processCodeWithAccess);

    List<Map<String, String>> retrieveAverageTimesActiveInstances(String processCode, Set<String> processCodeWithAccess);

    List<Map<String, String>> retrieveMeanTimeFinishedInstances(String processCode, Set<String> processCodeWithAccess);

    List<Map<String, String>> retrieveCounterActiveInstances(String processCode, Set<String> processCodeWithAccess);

    String retrieveProcessDefinitionName(String processCode);

    String retrieveProcessDefinitionId(String processDefinitionCode);

    List<FEED> retrieveAllFeed(String processCode, Set<String> processCodeWithAccess);

    List<MENU> retrieveAllCategories();

    List<MENU> retrieveAllCategoriesWithAcces(String userId);
    
    Pair<Long, Long> retrieveCategoryDefinitionIdsByCode(String code);
    
    String getUserAvatar();

    String getLogoutUrl();
}
