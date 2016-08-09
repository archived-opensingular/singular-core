/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.flow.rest;

import javax.inject.Inject;

import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.server.commons.service.PetitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.server.commons.config.SingularServerConfiguration;
import br.net.mirante.singular.support.spring.util.AutoScanDisabled;

@AutoScanDisabled
@RequestMapping("/rest/flow")
@RestController
public class DefaultServerREST {

    static final Logger LOGGER = LoggerFactory.getLogger(DefaultServerREST.class);

    public static final String PATH_BOX_ACTION = "/box/action";
    public static final String DELETE = "/delete";
    public static final String EXECUTE = "/execute";

    @Inject
    protected PetitionService<PetitionEntity> petitionService;

    @Inject
    private SingularServerConfiguration singularServerConfiguration;

    @RequestMapping(value = PATH_BOX_ACTION + DELETE, method = RequestMethod.POST)
    public ActionResponse excluir(@RequestBody Long id) {
        try {
            petitionService.delete(id);
        } catch (Exception e) {
            final String msg = "Erro ao excluir o item.";
            LOGGER.error(msg, e);
            return new ActionResponse(msg, false);
        }

        return new ActionResponse("Registro excluído com sucesso", true);
    }

    @RequestMapping(value = PATH_BOX_ACTION + EXECUTE, method = RequestMethod.POST)
    public ActionResponse execute(@RequestParam Long id, @RequestBody Action action) {
        try {
            IController controller = getActionController(id, action);
            return controller.execute(id, action);
        } catch (Exception e) {
            final String msg = String.format("Erro ao executar a ação %s para o id %d.", action.getName(), id);
            LOGGER.error(msg, e);
            return new ActionResponse(msg, false);
        }

    }

    private IController getActionController(Long id, Action action) {
        final PetitionEntity petition          = petitionService.find(id);
        final ProcessDefinition<?>      processDefinition = Flow.getProcessDefinitionWith(petition.getProcessType());
        final ActionConfig              actionConfig      = processDefinition.getMetaDataValue(ActionConfig.KEY);

        return actionConfig.getCustomAction(action.getName());
    }

}