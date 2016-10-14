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

package org.opensingular.server.commons.service;


import org.apache.commons.collections.CollectionUtils;
import org.opensingular.flow.core.*;
import org.opensingular.flow.persistence.entity.*;
import org.opensingular.form.SInstance;
import org.opensingular.form.context.SFormConfig;
import org.opensingular.form.persistence.FormKey;
import org.opensingular.form.persistence.entity.FormAnnotationEntity;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.util.Loggable;
import org.opensingular.server.commons.exception.PetitionConcurrentModificationException;
import org.opensingular.server.commons.exception.SingularServerException;
import org.opensingular.server.commons.flow.rest.ActionConfig;
import org.opensingular.server.commons.form.FormActions;
import org.opensingular.server.commons.persistence.dao.flow.ActorDAO;
import org.opensingular.server.commons.persistence.dao.flow.GrupoProcessoDAO;
import org.opensingular.server.commons.persistence.dao.flow.TaskInstanceDAO;
import org.opensingular.server.commons.persistence.dao.form.PetitionContentHistoryDAO;
import org.opensingular.server.commons.persistence.dao.form.PetitionDAO;
import org.opensingular.server.commons.persistence.dao.form.PetitionerDAO;
import org.opensingular.server.commons.persistence.dto.PeticaoDTO;
import org.opensingular.server.commons.persistence.dto.TaskInstanceDTO;
import org.opensingular.server.commons.persistence.entity.form.FormPetitionEntity;
import org.opensingular.server.commons.persistence.entity.form.PetitionContentHistoryEntity;
import org.opensingular.server.commons.persistence.entity.form.PetitionEntity;
import org.opensingular.server.commons.persistence.filter.QuickFilter;
import org.opensingular.server.commons.service.dto.BoxItemAction;
import org.opensingular.server.commons.spring.security.AuthorizationService;
import org.opensingular.server.commons.spring.security.SingularPermission;
import org.opensingular.server.commons.util.PetitionUtil;
import org.opensingular.server.commons.wicket.view.form.FormPageConfig;
import org.opensingular.server.commons.wicket.view.util.DispatcherPageUtil;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.opensingular.lib.support.persistence.enums.SimNao.SIM;
import static org.opensingular.server.commons.flow.action.DefaultActions.*;
import static org.opensingular.server.commons.flow.rest.DefaultServerREST.DELETE;
import static org.opensingular.server.commons.flow.rest.DefaultServerREST.PATH_BOX_ACTION;
import static org.opensingular.server.commons.util.Parameters.SIGLA_FORM_NAME;

@Transactional
public class PetitionService<P extends PetitionEntity> implements Loggable {

    @Inject
    protected PetitionDAO<P> petitionDAO;

    @Inject
    protected GrupoProcessoDAO grupoProcessoDAO;

    @Inject
    protected TaskInstanceDAO taskInstanceDAO;

    @Inject
    protected PetitionerDAO petitionerDAO;

    @Inject
    protected PetitionContentHistoryDAO petitionContentHistoryDAO;

    @Inject
    protected AuthorizationService authorizationService;

    @Inject
    protected FormPetitionService<P> formPetitionService;

    @Inject
    protected ActorDAO actorDAO;

    public P findPetitionByCod(Long cod) {
        return petitionDAO.find(cod);
    }

    public P findPetitionByProcessCod(Integer cod) {
        return petitionDAO.findByProcessCod(cod);
    }

    public void deletePetition(PeticaoDTO peticao) {
        petitionDAO.delete(petitionDAO.find(peticao.getCodPeticao()));
    }

    public void deletePetition(Long idPeticao) {
        petitionDAO.delete(petitionDAO.find(idPeticao));
    }

    public Long countQuickSearch(QuickFilter filter) {
        return countQuickSearch(filter, filter.getProcessesAbbreviation(), filter.getTypesNames());
    }

    public Long countQuickSearch(QuickFilter filter, List<String> siglasProcesso, List<String> formNames) {
        return petitionDAO.countQuickSearch(filter, siglasProcesso, formNames);
    }

    public List<PeticaoDTO> quickSearch(QuickFilter filter, List<String> siglasProcesso, List<String> formNames) {
        return petitionDAO.quickSearch(filter, siglasProcesso, formNames);
    }

    public List<Map<String, Object>> quickSearchMap(QuickFilter filter) {
        final List<Map<String, Object>> list = petitionDAO.quickSearchMap(filter, filter.getProcessesAbbreviation(), filter.getTypesNames());
        parseResultsPetition(list);
        list.forEach(this::checkItemActions);
        for (Map<String, Object> map : list) {
            authorizationService.filterActions((String) map.get("type"), (Long) map.get("codPeticao"), (List<BoxItemAction>) map.get("actions"), filter.getIdUsuarioLogado());
        }
        return list;
    }

    protected void parseResultsPetition(List<Map<String, Object>> results) {

    }

    private void checkItemActions(Map<String, Object> item) {
        List<BoxItemAction> actions = new ArrayList<>();
        actions.add(createPopupBoxItemAction(item, FormActions.FORM_FILL, ACTION_EDIT.getName()));
        actions.add(createPopupBoxItemAction(item, FormActions.FORM_VIEW, ACTION_VIEW.getName()));
        actions.add(createDeleteAction(item));
        actions.add(BoxItemAction.newExecuteInstante(item.get("codPeticao"), ACTION_ASSIGN.getName()));

        appendItemActions(item, actions);

        String                     processKey        = (String) item.get("processType");
        final ProcessDefinition<?> processDefinition = Flow.getProcessDefinitionWith(processKey);
        final ActionConfig         actionConfig      = processDefinition.getMetaDataValue(ActionConfig.KEY);
        if (actionConfig != null) {
            actions = actions.stream()
                    .filter(itemAction -> actionConfig.containsAction(itemAction.getName()))
                    .collect(Collectors.toList());
        }

        item.put("actions", actions);
    }

    protected void appendItemActions(Map<String, Object> item, List<BoxItemAction> actions) {
    }

    private BoxItemAction createDeleteAction(Map<String, Object> item) {
        String endpointUrl = PATH_BOX_ACTION + DELETE + "?id=" + item.get("codPeticao");

        final BoxItemAction boxItemAction = new BoxItemAction();
        boxItemAction.setName(ACTION_DELETE.getName());
        boxItemAction.setEndpoint(endpointUrl);
        return boxItemAction;
    }

    protected BoxItemAction createPopupBoxItemAction(Map<String, Object> item, FormActions formAction, String actionName) {
        Object cod  = item.get("codPeticao");
        Object type = item.get("type");
        return createPopupBoxItemAction(cod, type, formAction, actionName);
    }

    private BoxItemAction createPopupBoxItemAction(Object cod, Object type, FormActions formAction, String actionName) {
        String endpoint = DispatcherPageUtil
                .baseURL("")
                .formAction(formAction.getId())
                .formId(cod)
                .param(SIGLA_FORM_NAME, type)
                .build();

        final BoxItemAction boxItemAction = new BoxItemAction();
        boxItemAction.setName(actionName);
        boxItemAction.setEndpoint(endpoint);
        boxItemAction.setFormAction(formAction);
        return boxItemAction;
    }

    public FormKey saveOrUpdate(P peticao, SInstance instance, boolean createNewDraftIfDoesntExists, boolean mainForm, SFormConfig config) {
        return saveOrUpdate(peticao, instance, createNewDraftIfDoesntExists, mainForm, config, null);
    }

    public FormKey saveOrUpdate(P petition,
                                SInstance instance,
                                boolean createNewDraftIfDoesntExists,
                                boolean mainForm,
                                SFormConfig config,
                                Consumer<P> onSave) {

        if (instance == null) {
            return null;
        }

        petitionDAO.saveOrUpdate(petition);

        if (petition.getPetitioner() != null) {
            petitionerDAO.saveOrUpdate(petition.getPetitioner());
        }

        final FormKey key = formPetitionService.saveFormPetition(petition, instance, createNewDraftIfDoesntExists, mainForm, config);

        if (onSave != null) {
            onSave.accept(petition);
        }

        return key;
    }

    public void send(P peticao, SInstance instance, String codResponsavel, SFormConfig config) {

        formPetitionService.consolidateDrafts(peticao, config);

        final ProcessDefinition<?> processDefinition = PetitionUtil.getProcessDefinition(peticao);
        final ProcessInstance      processInstance   = processDefinition.newInstance();
        final ProcessInstanceEntity processEntity = processInstance.saveEntity();

        processInstance.setDescription(peticao.getDescription());
        peticao.setProcessInstanceEntity(processEntity);

        processInstance.start();

        onSend(peticao, instance, processEntity, codResponsavel);

        savePetitionHistory(peticao);
    }

    protected void onSend(P peticao, SInstance instance, ProcessInstanceEntity processEntity, String codResponsavel) {
    }

    private void savePetitionHistory(PetitionEntity petition) {

        final TaskInstanceEntity taskInstance = findCurrentTaskByPetitionId(petition.getCod());
        final FormEntity         formEntity   = petition.getMainForm();

        getLogger().info("Atualizando histórico da petição.");

        final PetitionContentHistoryEntity contentHistoryEntity = new PetitionContentHistoryEntity();

        contentHistoryEntity.setPetitionEntity(petition);

        if (taskInstance != null) {
            contentHistoryEntity.setActor(taskInstance.getAllocatedUser());
            contentHistoryEntity.setTaskInstanceEntity(taskInstance);
        }

        if (CollectionUtils.isNotEmpty(formEntity.getCurrentFormVersionEntity().getFormAnnotations())) {
            contentHistoryEntity.setFormAnnotationsVersions(formEntity.getCurrentFormVersionEntity().getFormAnnotations().stream().map(FormAnnotationEntity::getAnnotationCurrentVersion).collect(Collectors.toList()));
        }

        contentHistoryEntity.setPetitionerEntity(petition.getPetitioner());
        contentHistoryEntity.setHistoryDate(new Date());

        petitionContentHistoryDAO.saveOrUpdate(contentHistoryEntity);

        contentHistoryEntity.setFormVersionHistoryEntities(
                petition
                        .getFormPetitionEntities()
                        .stream()
                        .filter(isMainFormOrIsForCurrentTaskDefinition(petition))
                        .map(f -> formPetitionService.createFormVersionHistory(contentHistoryEntity, f))
                        .collect(Collectors.toList())
        );
    }

    private Predicate<FormPetitionEntity> isMainFormOrIsForCurrentTaskDefinition(PetitionEntity petitionEntity) {
        return f ->
                Optional.ofNullable(f)
                        .map(FormPetitionEntity::getMainForm)
                        .map(SIM::equals)
                        .orElse(false)
                        ||
                        Optional.ofNullable(petitionEntity)
                                .map(PetitionEntity::getProcessInstanceEntity)
                                .map(ProcessInstanceEntity::getCurrentTask)
                                .map(TaskInstanceEntity::getTask)
                                .map(TaskVersionEntity::getTaskDefinition)
                                .map(definition -> Optional.ofNullable(f)
                                        .map(FormPetitionEntity::getTaskDefinitionEntity)
                                        .map(definition::equals)
                                        .orElse(false)
                                )
                                .orElse(false);
    }

    /**
     * Executa a transição informada, consolidando todos os rascunhos, este metodo não salva a petição
     *
     * @param tn           nome tra transicao
     * @param petition     peticao
     * @param cfg          formConfig
     * @param onTransition listener
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void executeTransition(String tn, P petition, SFormConfig cfg, BiConsumer<P, String> onTransition) {
        try {

            if (onTransition != null) {
                onTransition.accept(petition, tn);
            }

            formPetitionService.consolidateDrafts(petition, cfg);

            savePetitionHistory(petition);

            final Class<? extends ProcessDefinition> clazz = PetitionUtil.getProcessDefinition(petition).getClass();
            final ProcessInstance                    pi    = Flow.getProcessInstance(clazz, petition.getProcessInstanceEntity().getCod());

            checkTaskIsEqual(petition.getProcessInstanceEntity(), pi);
            pi.executeTransition(tn);

        } catch (SingularException e) {
            throw e;
        } catch (Exception e) {
            throw new SingularServerException(e.getMessage(), e);
        }
    }

    private void checkTaskIsEqual(ProcessInstanceEntity processInstanceEntity, ProcessInstance piAtual) {
        if (!processInstanceEntity.getCurrentTask().getTask().getAbbreviation().equalsIgnoreCase(piAtual.getCurrentTask().getAbbreviation())) {
            throw new PetitionConcurrentModificationException("A instância está em uma tarefa diferente da esperada.");
        }
    }

    public List<TaskInstanceDTO> listTasks(QuickFilter filter, List<SingularPermission> permissions) {
        List<TaskInstanceDTO> tasks = taskInstanceDAO.findTasks(filter, permissions);
        parseResultsTask(tasks);
        for (TaskInstanceDTO task : tasks) {
            checkTaskActions(task, filter);
            authorizationService.filterActions(task.getType(), task.getCodPeticao(), task.getActions(), filter.getIdUsuarioLogado(), permissions);
        }
        return tasks;
    }

    protected void parseResultsTask(List<TaskInstanceDTO> tasks) {

    }

    protected void checkTaskActions(TaskInstanceDTO task, QuickFilter filter) {
        List<BoxItemAction> actions = new ArrayList<>();
        if (task.getCodUsuarioAlocado() == null
                && task.getTaskType() == TaskType.People) {
            actions.add(BoxItemAction.newExecuteInstante(task.getCodPeticao(), ACTION_ASSIGN.getName()));
        }

        if (task.getTaskType() == TaskType.People) {
            actions.add(BoxItemAction.newExecuteInstante(task.getCodPeticao(), ACTION_RELOCATE.getName()));
        }

        if (filter.getIdUsuarioLogado().equalsIgnoreCase(task.getCodUsuarioAlocado())) {
            actions.add(createPopupBoxItemAction(task.getCodPeticao(), task.getType(), FormActions.FORM_ANALYSIS, ACTION_ANALYSE.getName()));
        }

        actions.add(createPopupBoxItemAction(task.getCodPeticao(), task.getType(), FormActions.FORM_VIEW, ACTION_VIEW.getName()));

        appendTaskActions(task, actions);

        String                     processKey        = task.getProcessType();
        final ProcessDefinition<?> processDefinition = Flow.getProcessDefinitionWith(processKey);
        final ActionConfig         actionConfig      = processDefinition.getMetaDataValue(ActionConfig.KEY);
        if (actionConfig != null) {
            actions = actions.stream()
                    .filter(itemAction -> actionConfig.containsAction(itemAction.getName()))
                    .collect(Collectors.toList());
        }

        task.setActions(actions);
    }

    protected void appendTaskActions(TaskInstanceDTO task, List<BoxItemAction> actions) {

    }

    public Long countTasks(QuickFilter filter, List<SingularPermission> permissions) {
        return taskInstanceDAO.countTasks(filter.getProcessesAbbreviation(), permissions, filter.getFilter(), filter.getEndedTasks());
    }

    public List<? extends TaskInstanceDTO> listTasks(int first, int count, String sortProperty, boolean ascending, String siglaFluxo, List<SingularPermission> permissions, String filtroRapido, boolean concluidas) {
        return taskInstanceDAO.findTasks(first, count, sortProperty, ascending, siglaFluxo, permissions, filtroRapido, concluidas);
    }


    public Long countTasks(String siglaFluxo, List<SingularPermission> permissions, String filtroRapido, boolean concluidas) {
        return taskInstanceDAO.countTasks(Collections.singletonList(siglaFluxo), permissions, filtroRapido, concluidas);
    }

    public List<MTransition> listCurrentTaskTransitions(Long petitionId) {
        return Optional
                .ofNullable(Flow.getTaskInstance(findCurrentTaskByPetitionId(petitionId)))
                .map(TaskInstance::getFlowTask)
                .map(MTask::getTransitions)
                .orElse(Collections.emptyList());
    }

    public TaskInstanceEntity findCurrentTaskByPetitionId(Long petitionId) {
        List<TaskInstanceEntity> taskInstances = taskInstanceDAO.findCurrentTasksByPetitionId(petitionId);
        if (taskInstances.isEmpty()) {
            return null;
        } else {
            return taskInstances.get(0);
        }
    }

    public List<ProcessGroupEntity> listAllProcessGroups() {
        return grupoProcessoDAO.listarTodosGruposProcesso();
    }

    public ProcessGroupEntity findByProcessGroupName(String name) {
        return grupoProcessoDAO.findByName(name);
    }

    public ProcessGroupEntity findByProcessGroupCod(String cod) {
        return grupoProcessoDAO.get(cod);
    }

    public P createNewPetitionWithoutSave(Class<P> petitionClass, FormPageConfig config, BiConsumer<P, FormPageConfig> creationListener) {

        final P petition;

        try {
            petition = petitionClass.newInstance();
        } catch (Exception e) {
            throw new SingularServerException("Error creating new petition instance", e);
        }

        if (config.containsProcessDefinition()) {
            petition.setProcessDefinitionEntity((ProcessDefinitionEntity) Flow.getProcessDefinition(config.getProcessDefinition()).getEntityProcessDefinition());
        }

        if (creationListener != null) {
            creationListener.accept(petition, config);
        }

        return petition;
    }

    public ProcessDefinitionEntity findEntityProcessDefinitionByClass(Class<? extends ProcessDefinition> clazz) {
        return (ProcessDefinitionEntity) Optional
                .ofNullable(Flow.getProcessDefinition(clazz))
                .map(ProcessDefinition::getEntityProcessDefinition)
                .orElseThrow(() -> new SingularFlowException("Não foi possivel recuperar a definição do processo"));
    }

    public List<PetitionContentHistoryEntity> listPetitionContentHistoryByPetitionCod(long petitionCod) {
        return petitionContentHistoryDAO.listPetitionContentHistoryByPetitionCod(petitionCod);
    }

    public List<Actor> listAllocableUsers(Map<String, Object> selectedTask) {
        Integer taskInstanceId = (Integer) selectedTask.get("taskInstanceId");
        return actorDAO.listAllocableUsers(taskInstanceId);
    }

}