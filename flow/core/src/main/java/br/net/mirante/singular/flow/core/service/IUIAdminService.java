package br.net.mirante.singular.flow.core.service;

import java.time.Period;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import br.net.mirante.singular.flow.core.dto.IDefinitionDTO;
import br.net.mirante.singular.flow.core.dto.IFeedDTO;
import br.net.mirante.singular.flow.core.dto.IInstanceDTO;
import br.net.mirante.singular.flow.core.dto.IMenuItemDTO;
import br.net.mirante.singular.flow.core.dto.IMetaDataDTO;
import br.net.mirante.singular.flow.core.dto.IStatusDTO;

public interface IUIAdminService<DEFINITION extends IDefinitionDTO, INSTANCE extends IInstanceDTO,
        METADATA extends IMetaDataDTO, STATUS extends IStatusDTO, FEED extends IFeedDTO, MENU extends IMenuItemDTO> {

    DEFINITION retrieveDefinitionById(Long id);

    List<DEFINITION> retrieveAllDefinition(int first, int size, String orderByProperty, boolean asc);

    int countAllDefinition();

    List<INSTANCE> retrieveAllInstance(int first, int size, String orderByProperty, boolean asc, Long id);

    int countAllInstance(Long id);

    byte[] retrieveProcessDiagram(String sigla);

    List<METADATA> retrieveMetaData(Long id);

    List<Map<String, String>> retrieveMeanTimeByProcess(Period period, String processCode);

    List<Map<String, String>> retrieveNewInstancesQuantityLastYear(String processCode);

    List<Map<String, String>> retrieveEndStatusQuantityByPeriod(Period period, String processCode);

    List<Map<String, String>> retrieveMeanTimeByTask(Period period, String processCode);

    List<Map<String, String>> retrieveStatsByActiveTask(String processDefinitionCode);

    STATUS retrieveActiveInstanceStatus(String processCode);

    List<Map<String, String>> retrieveMeanTimeActiveInstances(String processCode);

    List<Map<String, String>> retrieveAverageTimesActiveInstances(String processCode);

    List<Map<String, String>> retrieveMeanTimeFinishedInstances(String processCode);

    List<Map<String, String>> retrieveCounterActiveInstances(String processCode);

    String retrieveProcessDefinitionName(String processCode);

    String retrieveProcessDefinitionId(String processDefinitionCode);

    List<FEED> retrieveAllFeed(String processCode);

    List<MENU> retrieveAllCategories();

    Pair<Long, Long> retrieveCategoryDefinitionIdsByCode(String code);
}
