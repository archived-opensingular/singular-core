/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.server.commons.flow.rest;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.opensingular.server.commons.flow.action.DefaultActions.ACTION_DELETE;
import static org.opensingular.server.commons.service.IServerMetadataREST.PATH_BOX_SEARCH;

import org.opensingular.flow.core.ProcessDefinition;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.spring.SpringServiceRegistry;
import org.opensingular.flow.persistence.entity.Actor;
import org.opensingular.server.commons.persistence.dto.TaskInstanceDTO;
import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.server.commons.persistence.filter.QuickFilter;
import org.opensingular.server.commons.service.PetitionService;
import org.opensingular.server.commons.spring.security.AuthorizationService;
import org.opensingular.server.commons.spring.security.PermissionResolverService;
import org.opensingular.server.commons.spring.security.SingularPermission;
import org.opensingular.server.commons.util.PetitionUtil;
import org.opensingular.lib.support.spring.util.AutoScanDisabled;

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
    public static final String USERS            = "/listarUsuarios";

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
            boolean hasPermission = authorizationService.hasPermission(id, null, actionRequest.getIdUsuario(), ACTION_DELETE.getName());
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
        return petitionService.quickSearchMap(filter);
    }

    @RequestMapping(value = PATH_BOX_SEARCH + COUNT_PETITIONS, method = RequestMethod.POST)
    public Long countPetitions(@RequestBody QuickFilter filter) {
        return petitionService.countQuickSearch(filter);
    }

    @RequestMapping(value = PATH_BOX_SEARCH + SEARCH_TASKS, method = RequestMethod.POST)
    public List<TaskInstanceDTO> searchTasks(@RequestBody QuickFilter filter) {
        List<SingularPermission> permissions = permissionResolverService.searchPermissions(filter.getIdUsuarioLogado());
        return petitionService.listTasks(filter, permissions);
    }

    @RequestMapping(value = PATH_BOX_SEARCH + COUNT_TASKS, method = RequestMethod.POST)
    public Long countTasks(@RequestBody QuickFilter filter) {
        List<SingularPermission> permissions = permissionResolverService.searchPermissions(filter.getIdUsuarioLogado());
        return petitionService.countTasks(filter, permissions);
    }

    @RequestMapping(value = PATH_BOX_SEARCH + USERS, method = RequestMethod.POST)
    public List<Actor> listarUsuarios(@RequestBody Map<String, Object> selectedTask) {
        return petitionService.listAllocableUsers(selectedTask);
    }
}