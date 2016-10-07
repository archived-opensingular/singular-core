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

import java.time.LocalDate;
import java.util.Set;

import org.opensingular.lib.commons.base.SingularUtil;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.SingularFlowException;
import org.opensingular.flow.core.authorization.AccessLevel;

public interface IFlowMetadataREST {

    static final String PATH_PROCESS_INSTANCE_HAS_ACCESS = "/process/instance/has-access";
    static final String PATH_PROCESS_DEFINITION_HAS_ACCESS = "/process/definition/has-access";
    static final String PATH_PROCESS_DEFINITION_WITH_ACCESS = "/process/definition/with-access";
    static final String PATH_PROCESS_DEFINITION_DIAGRAM = "/process/definition/diagram";
    static final String PATH_PROCESS_DETAIL_DASHBOARD = "/process/dashboard/detail";
    static final String PATH_PROCESS_CUSTOM_DASHBOARD = "/process/dashboard/custom";
    static final String PATH_PROCESS_DASHBOARD_DATA = "/process/dashboard/data";

    /**
     * REST path: {@link IFlowMetadataREST#PATH_PROCESS_DEFINITION_WITH_ACCESS}
     * 
     * @param groupToken - request
     * @param userCod - request
     * @param accessLevel - request
     * @return set of process definition key with accessLevel required
     */
    Set<String> listProcessDefinitionsWithAccess(String groupToken, String userCod, AccessLevel accessLevel);

    /**
     * REST path: {@link IFlowMetadataREST#PATH_PROCESS_DEFINITION_HAS_ACCESS}
     * 
     * @param groupToken - request
     * @param processDefinitionKey - request
     * @param userCod - request
     * @param accessLevel - request
     * @return true if has the accessLevel required
     */
    boolean hasAccessToProcessDefinition(String groupToken, String processDefinitionKey, String userCod, AccessLevel accessLevel);

    /**
     * REST path: {@link IFlowMetadataREST#PATH_PROCESS_INSTANCE_HAS_ACCESS}
     * 
     * @param groupToken - request
     * @param processInstanceFullId - request
     * @param userCod - request
     * @param accessLevel - request
     * @return true if has the accessLevel required
     */
    boolean hasAccessToProcessInstance(String groupToken, String processInstanceFullId, String userCod, AccessLevel accessLevel);

    /**
     * REST path {@link IFlowMetadataREST#PATH_PROCESS_DEFINITION_DIAGRAM}
     * 
     * @param groupToken - request
     * @param processDefinitionKey - request
     * @return
     */
    byte[] processDefinitionDiagram(String groupToken, String processDefinitionKey);
    
    static String generateGroupToken(String groupCod){
        return SingularUtil.toSHA1(groupCod+LocalDate.now().toEpochDay());
    }
    
    static void checkGroupToken(String groupToken) {
        String localGroupToken = generateGroupToken(Flow.getConfigBean().getProcessGroupCod());
        if (!localGroupToken.equals(groupToken)) {
            throw new SingularFlowException("Group token inválido");
        }
    }
}
