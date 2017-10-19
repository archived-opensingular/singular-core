/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.flow.core.service;

import org.apache.commons.lang3.tuple.Pair;
import org.opensingular.flow.core.dto.IDefinitionDTO;
import org.opensingular.flow.core.dto.IFeedDTO;
import org.opensingular.flow.core.dto.IInstanceDTO;
import org.opensingular.flow.core.dto.IMenuItemDTO;
import org.opensingular.flow.core.dto.IMetaDataDTO;
import org.opensingular.flow.core.dto.IStatusDTO;

import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IUIAdminService<DEFINITION extends IDefinitionDTO, INSTANCE extends IInstanceDTO,
        METADATA extends IMetaDataDTO, STATUS extends IStatusDTO, FEED extends IFeedDTO, MENU extends IMenuItemDTO> {

    DEFINITION retrieveDefinitionById(Integer flowDefinitionCod);
    
    DEFINITION retrieveDefinitionByKey(String flowDefinitionKey);

    List<DEFINITION> retrieveAllDefinition(int first, int size, String orderByProperty, boolean asc, Set<String> processCodeWithAccess);

    int countAllDefinition(Set<String> processCodeWithAccess);

    List<INSTANCE> retrieveAllInstance(int first, int size, String orderByProperty, boolean asc, Integer flowDefinitionCod);

    int countAllInstance(Integer flowDefinitionCod);

    List<METADATA> retrieveMetaData(Integer flowDefinitionCod);

    List<Map<String, String>> retrieveMeanTimeByProcess(Period period, String processCode, Set<String> processCodeWithAccess);

    List<Map<String, String>> retrieveNewInstancesQuantityLastYear(String processCode, Set<String> processCodeWithAccess);

    List<Map<String, String>> retrieveEndStatusQuantityByPeriod(Period period, String processCode);

    List<Map<String, String>> retrieveMeanTimeByTask(Period period, String processCode);

    List<Map<String, String>> retrieveStatsByActiveTask(String flowDefinitionCode);

    STATUS retrieveActiveInstanceStatus(String processCode, Set<String> processCodeWithAccess);

    List<Map<String, String>> retrieveMeanTimeActiveInstances(String processCode, Set<String> processCodeWithAccess);

    List<Map<String, String>> retrieveAverageTimesActiveInstances(String processCode, Set<String> processCodeWithAccess);

    List<Map<String, String>> retrieveMeanTimeFinishedInstances(String processCode, Set<String> processCodeWithAccess);

    List<Map<String, String>> retrieveCounterActiveInstances(String processCode, Set<String> processCodeWithAccess);

    String retrieveFlowDefinitionName(String flowDefinitionCode);

    String retrieveFlowDefinitionId(String flowDefinitionCode);

    List<FEED> retrieveAllFeed(String processCode, Set<String> processCodeWithAccess);

    List<MENU> retrieveAllCategories();

    List<MENU> retrieveAllCategoriesWithAcces(String userId);
    
    Pair<Long, Long> retrieveCategoryDefinitionIdsByCode(String code);
    
    String getUserAvatar();

    String getLogoutUrl();
}
