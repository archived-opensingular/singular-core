/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.server.commons.flow.rest.actions;

import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.MUser;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.lib.support.spring.util.AutoScanDisabled;
import org.opensingular.server.commons.flow.rest.ActionRequest;
import org.opensingular.server.commons.flow.rest.ActionResponse;
import org.opensingular.server.commons.flow.rest.IController;
import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;
import org.springframework.stereotype.Controller;

import javax.xml.ws.WebServiceException;

import static org.opensingular.server.commons.flow.action.DefaultActions.ACTION_ASSIGN;

@AutoScanDisabled
@Controller
public class DefaultAssignController extends IController implements Loggable {

    @Override
    public ActionResponse execute(PetitionEntity petition, ActionRequest action) {
        try {
            ProcessInstance processInstance = Flow.getProcessInstance(petition.getProcessInstanceEntity());
            MUser user = Flow.getConfigBean().getUserService().saveUserIfNeeded(action.getIdUsuario());
            if (user == null) {
                throw new WebServiceException("Usuário não encontrado");
            }

            processInstance.getCurrentTask().relocateTask(user, user, false, "", action.getLastVersion());
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
