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

    List<DEFINITION> retrieveAllDefinition(int first, int size, String orderByProperty, boolean asc, Set<String> flowDefinitionCodesWithAccess);

    int countAllDefinition(Set<String> flowCodesWithAccess);

    List<INSTANCE> retrieveAllInstance(int first, int size, String orderByProperty, boolean asc, Integer flowDefinitionCod);

    int countAllInstance(Integer flowDefinitionCod);

    List<METADATA> retrieveMetaData(Integer flowDefinitionCod);

    List<Map<String, Object>> retrieveMeanTimeByFlowDefinition(Period period, String flowDefinitionCod, Set<String> flowDefinitionCodesWithAccess);

    List<Map<String, Object>> retrieveNewInstancesQuantityLastYear(String flowDefinitionCod, Set<String> flowDefinitionCodesWithAccess);

    List<Map<String, Object>> retrieveEndStatusQuantityByPeriod(Period period, String flowDefinitionCod);

    List<Map<String, Object>> retrieveMeanTimeByTask(Period period, String flowDefinitionCod);

    List<Map<String, Object>> retrieveStatsByActiveTask(String flowDefinitionCode);

    STATUS retrieveActiveInstanceStatus(String flowInstanceCod, Set<String> flowDefinitionCodesWithAccess);

    List<Map<String, Object>> retrieveMeanTimeActiveInstances(String flowDefinitionCod, Set<String> flowDefinitionCodesWithAccess);

    List<Map<String, Object>> retrieveAverageTimesActiveInstances(String flowDefinitionCod, Set<String> flowDefinitionCodesWithAccess);

    List<Map<String, Object>> retrieveMeanTimeFinishedInstances(String flowDefinitionCod, Set<String> flowDefinitionCodesWithAccess);

    List<Map<String, Object>> retrieveCounterActiveInstances(String flowDefinitionCod, Set<String> flowDefinitionCodesWithAccess);

    String retrieveFlowDefinitionName(String flowDefinitionCode);

    String retrieveFlowDefinitionId(String flowDefinitionCode);

    List<FEED> retrieveAllFeed(String flowDefinitionCod, Set<String> flowDefinitionCodesWithAccess);

    List<MENU> retrieveAllCategories();

    List<MENU> retrieveAllCategoriesWithAccess(String userId);
    
    Pair<Long, Long> retrieveCategoryDefinitionIdsByCode(String code);
    
    String getUserAvatar();

    String getLogoutUrl();
}
