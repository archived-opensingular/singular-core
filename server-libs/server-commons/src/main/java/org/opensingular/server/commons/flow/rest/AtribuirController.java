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

package org.opensingular.server.commons.flow.rest;

import static org.opensingular.server.commons.flow.action.DefaultActions.ACTION_ASSIGN;

import javax.xml.ws.WebServiceException;

import org.springframework.stereotype.Controller;

import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.flow.core.Flow;
import org.opensingular.flow.core.MUser;
import org.opensingular.flow.core.ProcessInstance;
import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;

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
