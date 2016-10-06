/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.server.commons.flow.rest;

import static org.opensingular.singular.server.commons.flow.action.DefaultActions.ACTION_ASSIGN;

import javax.xml.ws.WebServiceException;

import org.springframework.stereotype.Controller;

import org.opensingular.singular.commons.util.Loggable;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.MUser;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.singular.server.commons.persistence.entity.form.PetitionEntity;

@Controller
public class AtribuirController extends IController implements Loggable {

    @Override
    public ActionResponse execute(PetitionEntity petition, ActionRequest actionRequest) {
        try {
            ProcessInstance processInstance = Flow.getProcessInstance(petition.getProcessInstanceEntity());
            MUser user = Flow.getConfigBean().getUserService().saveUserIfNeeded(actionRequest.getIdUsuario());
            if (user == null) {
                throw new WebServiceException("Usuário não encontrado");
            }

            processInstance.getCurrentTask().relocateTask(user, user, false, "", actionRequest.getLastVersion());
            return new ActionResponse("Tarefa atribuída com sucesso.", true);
        } catch (Exception e) {
            String resultMessage = "Erro ao atribuir tarefa.";
            getLogger().error(resultMessage, e);
            return new ActionResponse(resultMessage, false);
        }
    }

    @Override
    public String getActionName() {
        return ACTION_ASSIGN.getName();
    }


}