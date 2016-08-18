/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.flow.rest;

import static br.net.mirante.singular.server.commons.util.ServerActionConstants.ACTION_RELOCATE;

import javax.xml.ws.WebServiceException;

import org.springframework.stereotype.Controller;

import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;

@Controller
public class AtribuirController extends IController implements Loggable {

    @Override
    public ActionResponse execute(PetitionEntity petition, Action action) {
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
        return ACTION_RELOCATE;
    }


}
