/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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
