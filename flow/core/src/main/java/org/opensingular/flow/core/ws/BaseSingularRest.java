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

import java.util.Objects;

import javax.xml.ws.WebServiceException;

import org.opensingular.flow.core.MUser;
import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.ProcessInstance;

public class BaseSingularRest {

    public static final String START_INSTANCE = "/startInstance";
    public static final String EXECUTE_DEFAULT_TRANSITION = "/executeDefaultTransition";
    public static final String EXECUTE_TRANSITION = "/executeTransition";
    public static final String RELOCATE_TASK = "/relocateTask";

    public static final String PROCESS_ABBREVIATION = "processAbbreviation";
    public static final String COD_PROCESS_INSTANCE = "codProcessInstance";
    public static final String USERNAME = "username";
    public static final String LAST_VERSION = "lastVersion";

    public String ping() {
        return "pong";
    }

    public Long startInstance(String processAbbreviation) {
        ProcessDefinition processo = Flow.getProcessDefinitionWith(processAbbreviation);
        ProcessInstance processInstance = processo.newInstance();
        processInstance.start();
        return processInstance.getEntityCod().longValue();
    }

    public void executeDefaultTransition(String processAbbreviation,
                                         Long codProcessInstance,
                                          String username) {
        ProcessInstance processInstance = getProcessInstance(processAbbreviation, codProcessInstance);
        processInstance.executeTransition();
    }

    public void executeTransition(String processAbbreviation,
                                  Long codProcessInstance,
                                  String transitionName,
                                  String username) {
        ProcessInstance processInstance = getProcessInstance(processAbbreviation, codProcessInstance);
        processInstance.executeTransition(transitionName);
    }

    public void relocateTask(String processAbbreviation,
                             Long codProcessInstance,
                             String username,
                             Integer lastVersion) {
        ProcessInstance processInstance = getProcessInstance(processAbbreviation, codProcessInstance);
        MUser user = Flow.getConfigBean().getUserService().saveUserIfNeeded(username);
        if (user == null) {
            throw new WebServiceException("Usuário não encontrado");
        }
        if(lastVersion == null) lastVersion = 0;
        processInstance.getCurrentTask().relocateTask(user, user, false, "", lastVersion);
    }

    private ProcessInstance getProcessInstance(String processAbbreviation, Long codProcessInstance) {
        ProcessInstance processInstance = Flow.getProcessDefinitionWith(processAbbreviation).getDataService().retrieveInstance(codProcessInstance.intValue());
        return Objects.requireNonNull(processInstance);
    }
    
}
