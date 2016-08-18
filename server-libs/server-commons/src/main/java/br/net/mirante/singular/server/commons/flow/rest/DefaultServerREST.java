/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.flow.rest;

import static br.net.mirante.singular.server.commons.util.ServerActionConstants.ACTION_DELETE;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.spring.SpringServiceRegistry;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.spring.security.PermissionResolverService;
import br.net.mirante.singular.server.commons.util.PetitionUtil;
import br.net.mirante.singular.support.spring.util.AutoScanDisabled;

/**
 * Essa interface deve ser protegida de forma que apenas o próprio servidor possa
 * acessar seus métodos.
 */
@AutoScanDisabled
@RequestMapping("/rest/flow")
@RestController
public class DefaultServerREST {

    static final Logger LOGGER = LoggerFactory.getLogger(DefaultServerREST.class);

    public static final String PATH_BOX_ACTION = "/box/action";
    public static final String DELETE          = "/delete";
    public static final String EXECUTE         = "/execute";

    @Inject
    protected PetitionService<PetitionEntity> petitionService;

    @Inject
    private PermissionResolverService permissionResolverService;

    @Inject
    private SpringServiceRegistry springServiceRegistry;

    @Inject
    @Named("formConfigWithDatabase")
    protected SFormConfig<String> singularFormConfig;

    @RequestMapping(value = PATH_BOX_ACTION + DELETE, method = RequestMethod.POST)
    public ActionResponse excluir(@RequestParam Long id, @RequestBody Action action) {
        try {
            boolean hasPermission = permissionResolverService.hasPermission(id, action.getIdUsuario(), ACTION_DELETE);
            if (hasPermission) {
                petitionService.delete(id);
                return new ActionResponse("Registro excluído com sucesso", true);
            } else {
                return new ActionResponse("Você não tem permissão para executar esta ação.", false);
            }
        } catch (Exception e) {
            final String msg = "Erro ao excluir o item.";
            LOGGER.error(msg, e);
            return new ActionResponse(msg, false);
        }

    }

    @RequestMapping(value = PATH_BOX_ACTION + EXECUTE, method = RequestMethod.POST)
    public ActionResponse execute(@RequestParam Long id, @RequestBody Action action) {
        try {
            final PetitionEntity petition = petitionService.find(id);
            final ProcessDefinition<?> processDefinition = PetitionUtil.getProcessDefinition(petition);

            IController controller = getActionController(processDefinition, action);
            return controller.run(petition, action);
        } catch (Exception e) {
            final String msg = String.format("Erro ao executar a ação %s para o id %d.", action.getName(), id);
            LOGGER.error(msg, e);
            return new ActionResponse(msg, false);
        }

    }

    private IController getActionController(ProcessDefinition<?> processDefinition, Action action) {
        final ActionConfig actionConfig = processDefinition.getMetaDataValue(ActionConfig.KEY);

        Class<? extends IController> controllerClass = actionConfig.getAction(action.getName());
        return springServiceRegistry.lookupService(controllerClass);
    }

}