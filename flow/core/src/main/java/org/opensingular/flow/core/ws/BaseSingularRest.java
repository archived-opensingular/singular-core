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

package org.opensingular.flow.core.ws;

import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.FlowDefinition;
import org.opensingular.flow.core.FlowInstance;
import org.opensingular.flow.core.SUser;

public class BaseSingularRest {

    public static final String START_INSTANCE = "/startInstance";
    public static final String EXECUTE_DEFAULT_TRANSITION = "/executeDefaultTransition";
    public static final String EXECUTE_TRANSITION = "/executeTransition";
    public static final String RELOCATE_TASK = "/relocateTask";

    public static final String FLOW_DEFINITION_ABBREVIATION = "flowDefinitionAbbreviation";
    public static final String COD_FLOW_INSTANCE = "codFlowInstance";
    public static final String USERNAME = "username";
    public static final String LAST_VERSION = "lastVersion";

    public String ping() {
        return "pong";
    }

    public Long startInstance(String flowDefinitionAbbreviation) {
        FlowDefinition definition = Flow.getFlowDefinition(flowDefinitionAbbreviation);
        FlowInstance flowInstance = definition.prepareStartCall().createAndStart();
        return flowInstance.getEntityCod().longValue();
    }

    public void executeDefaultTransition(String flowDefinitionAbbreviation,
                                         Long codFlowInstance,
                                          String username) {
        FlowInstance flowInstance = getFlowInstance(flowDefinitionAbbreviation, codFlowInstance);
        flowInstance.prepareTransition().go();
    }

    public void executeTransition(String flowDefinitionAbbreviation,
                                  Long codFlowInstance,
                                  String transitionName,
                                  String username) {
        FlowInstance flowInstance = getFlowInstance(flowDefinitionAbbreviation, codFlowInstance);
        flowInstance.prepareTransition(transitionName).go();
    }

    public void relocateTask(String flowDefinitionAbbreviation,
                             Long codFlowInstance,
                             String username,
                             Integer lastVersion) {
        FlowInstance flowInstance = getFlowInstance(flowDefinitionAbbreviation, codFlowInstance);
        SUser user = Flow.getConfigBean().getUserService().saveUserIfNeededOrException(username);
        Integer lastVersion2 = (lastVersion == null) ? Integer.valueOf(0) : lastVersion;
        flowInstance.getCurrentTaskOrException().relocateTask(user, user, false, "", lastVersion2);
    }

    private FlowInstance getFlowInstance(String flowDefinitionAbbreviation, Long codFlowInstance) {
        FlowDefinition<FlowInstance> flowDefinition = Flow.getFlowDefinition(flowDefinitionAbbreviation);
        return Flow.getFlowInstance(flowDefinition, (Integer) codFlowInstance.intValue());
    }
    
}
