/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.flow.core.ws;

import java.util.Objects;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;

@WebService
public class SingularWS {

    @WebMethod(action = "ping")
    public String ping() {
        return "pong";
    }

    @WebMethod(action = "startInstance")
    public Long startInstance(@WebParam(name = "processAbbreviation") String processAbbreviation) {
        ProcessDefinition processo = Flow.getProcessDefinitionWith(processAbbreviation);
        ProcessInstance processInstance = processo.newInstance();
        processInstance.start();
        return processInstance.getEntityCod().longValue();
    }

    @WebMethod(action = "executeDefaultTransition")
    public void executeDefaultTransition(@WebParam(name = "processAbbreviation") String processAbbreviation,
                                         @WebParam(name = "codProcessInstance") Long codProcessInstance,
                                         @WebParam(name = "username") String username) {
        ProcessInstance processInstance = getProcessInstance(processAbbreviation, codProcessInstance);
        processInstance.executeTransition();
    }

    @WebMethod(action = "executeTransition")
    public void executeTransition(@WebParam(name = "processAbbreviation") String processAbbreviation,
                                  @WebParam(name = "codProcessInstance") Long codProcessInstance,
                                  @WebParam(name = "transitionName") String transitionName,
                                  @WebParam(name = "username") String username) {
        ProcessInstance processInstance = getProcessInstance(processAbbreviation, codProcessInstance);
        processInstance.executeTransition(transitionName);
    }

    @WebMethod(action = "relocateTask")
    public void relocateTask(@WebParam(name = "processAbbreviation") String processAbbreviation,
                                  @WebParam(name = "codProcessInstance") Long codProcessInstance,
                                  @WebParam(name = "username") String username,
                                  @WebParam(name = "lastVersion") Integer version) {
        ProcessInstance processInstance = getProcessInstance(processAbbreviation, codProcessInstance);
        MUser user = Flow.getConfigBean().getUserService().saveUserIfNeeded(username);
        if (user == null) {
            throw new WebServiceException("Usuário não encontrado");
        }
        if(version == null) version = 0;
        processInstance.getCurrentTask().relocateTask(user, user, false, "", version);
    }

    private ProcessInstance getProcessInstance(String processAbbreviation, Long codProcessInstance) {
        ProcessInstance processInstance = Flow.getProcessDefinitionWith(processAbbreviation).getDataService().retrieveInstance(codProcessInstance.intValue());
        return Objects.requireNonNull(processInstance);
    }
}
