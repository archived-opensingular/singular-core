/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.server.commons.flow.rest;

import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.form.context.SFormConfig;
import br.net.mirante.singular.form.spring.SpringServiceRegistry;
import br.net.mirante.singular.server.commons.persistence.dto.TaskInstanceDTO;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.server.commons.persistence.filter.QuickFilter;
import br.net.mirante.singular.server.commons.service.PetitionService;
import br.net.mirante.singular.server.commons.spring.security.AuthorizationService;
import br.net.mirante.singular.server.commons.spring.security.PermissionResolverService;
import br.net.mirante.singular.server.commons.util.PetitionUtil;
import br.net.mirante.singular.support.spring.util.AutoScanDisabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static br.net.mirante.singular.server.commons.service.IServerMetadataREST.PATH_BOX_SEARCH;
import static br.net.mirante.singular.server.commons.flow.action.DefaultActions.ACTION_DELETE;

/**
 * Essa interface deve ser protegida de forma que apenas o próprio servidor possa
 * acessar seus métodos.
 */
@AutoScanDisabled
@RequestMapping("/rest/flow")
@RestController
public class DefaultServerREST {

    public static final String PATH_BOX_ACTION  = "/box/action";
    public static final String DELETE           = "/delete";
    public static final String EXECUTE          = "/execute";
    public static final String SEARCH_PETITIONS = "/searchPetitions";
    public static final String COUNT_PETITIONS  = "/countPetitions";
    public static final String SEARCH_TASKS     = "/searchTasks";
    public static final String COUNT_TASKS      = "/countTasks";
    static final        Logger LOGGER           = LoggerFactory.getLogger(DefaultServerREST.class);
    @Inject
    protected PetitionService<PetitionEntity> petitionService;

    @Inject
    protected PermissionResolverService permissionResolverService;

    @Inject
    protected AuthorizationService authorizationService;

    @Inject
    protected SpringServiceRegistry springServiceRegistry;

    @Inject
    @Named("formConfigWithDatabase")
    protected SFormConfig<String> singularFormConfig;

    @RequestMapping(value = PATH_BOX_ACTION + DELETE, method = RequestMethod.POST)
    public ActionResponse excluir(@RequestParam Long id, @RequestBody ActionRequest actionRequest) {
        try {
            boolean hasPermission = authorizationService.hasFormPermission(id, actionRequest.getIdUsuario(), ACTION_DELETE.getName());
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
    public ActionResponse execute(@RequestParam Long id, @RequestBody ActionRequest actionRequest) {
        try {
            final PetitionEntity petition = petitionService.find(id);
            final ProcessDefinition<?> processDefinition = PetitionUtil.getProcessDefinition(petition);

            IController controller = getActionController(processDefinition, actionRequest);
            return controller.run(petition, actionRequest);
        } catch (Exception e) {
            final String msg = String.format("Erro ao executar a ação %s para o id %d.", actionRequest.getName(), id);
            LOGGER.error(msg, e);
            return new ActionResponse(msg, false);
        }

    }

    private IController getActionController(ProcessDefinition<?> processDefinition, ActionRequest actionRequest) {
        final ActionConfig actionConfig = processDefinition.getMetaDataValue(ActionConfig.KEY);

        Class<? extends IController> controllerClass = actionConfig.getAction(actionRequest.getName());
        return springServiceRegistry.lookupService(controllerClass);
    }


    @RequestMapping(value = PATH_BOX_SEARCH + SEARCH_PETITIONS, method = RequestMethod.POST)
    public List<Map<String, Object>> searchPetitions(@RequestBody QuickFilter filter) {
        List<Map<String, Object>> result = petitionService.quickSearchMap(filter);
        authorizationService.filterActions(result, filter.getIdUsuarioLogado());
        return result;
    }

    @RequestMapping(value = PATH_BOX_SEARCH + COUNT_PETITIONS, method = RequestMethod.POST)
    public Long countPetitions(@RequestBody QuickFilter filter) {
        return petitionService.countQuickSearch(filter);
    }

    @RequestMapping(value = PATH_BOX_SEARCH + SEARCH_TASKS, method = RequestMethod.POST)
    public List<TaskInstanceDTO> searchTasks(@RequestBody QuickFilter filter) {
        List<Serializable> permissions = permissionResolverService.searchPermissionsInternal(filter.getIdUsuarioLogado());
        return petitionService.listTasks(filter, permissions);
    }

    @RequestMapping(value = PATH_BOX_SEARCH + COUNT_TASKS, method = RequestMethod.POST)
    public Long countTasks(@RequestBody QuickFilter filter) {
        List<Serializable> permissions = permissionResolverService.searchPermissionsInternal(filter.getIdUsuarioLogado());
        return petitionService.countTasks(filter, permissions);
    }

}