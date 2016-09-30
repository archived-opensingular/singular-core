package br.net.mirante.singular.server.commons.service;


import br.net.mirante.singular.commons.base.SingularException;
import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.SingularFlowException;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.flow.core.TaskType;
import br.net.mirante.singular.flow.core.service.IUserService;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.SInstances;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.persistence.SingularFormPersistenceException;
import br.net.mirante.singular.form.persistence.entity.FormAnnotationEntity;
import br.net.mirante.singular.form.persistence.entity.FormEntity;
import br.net.mirante.singular.form.persistence.entity.FormTypeEntity;
import br.net.mirante.singular.form.persistence.entity.FormVersionEntity;
import br.net.mirante.singular.form.service.IFormService;
import br.net.mirante.singular.form.type.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.type.core.annotation.SIAnnotation;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.persistence.entity.ProcessDefinitionEntity;
import br.net.mirante.singular.persistence.entity.ProcessGroupEntity;
import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;
import br.net.mirante.singular.persistence.entity.TaskDefinitionEntity;
import br.net.mirante.singular.persistence.entity.TaskInstanceEntity;
import br.net.mirante.singular.persistence.entity.TaskVersionEntity;
import br.net.mirante.singular.server.commons.exception.PetitionConcurrentModificationException;
import br.net.mirante.singular.server.commons.exception.SingularServerException;
import br.net.mirante.singular.server.commons.flow.rest.ActionConfig;
import br.net.mirante.singular.server.commons.form.FormActions;
import br.net.mirante.singular.server.commons.persistence.dao.flow.GrupoProcessoDAO;
import br.net.mirante.singular.server.commons.persistence.dao.flow.TaskInstanceDAO;
import br.net.mirante.singular.server.commons.persistence.dao.form.DraftDAO;
import br.net.mirante.singular.server.commons.persistence.dao.form.FormPetitionDAO;
import br.net.mirante.singular.server.commons.persistence.dao.form.PetitionContentHistoryDAO;
import br.net.mirante.singular.server.commons.persistence.dao.form.PetitionDAO;
import br.net.mirante.singular.server.commons.persistence.dao.form.PetitionerDAO;
import br.net.mirante.singular.server.commons.persistence.dto.PeticaoDTO;
import br.net.mirante.singular.server.commons.persistence.dto.TaskInstanceDTO;
import br.net.mirante.singular.server.commons.persistence.entity.form.DraftEntity;
import br.net.mirante.singular.server.commons.persistence.entity.form.FormPetitionEntity;
import br.net.mirante.singular.server.commons.persistence.entity.form.FormVersionHistoryEntity;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionContentHistoryEntity;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.server.commons.persistence.filter.QuickFilter;
import br.net.mirante.singular.server.commons.service.dto.BoxItemAction;
import br.net.mirante.singular.server.commons.spring.security.AuthorizationService;
import br.net.mirante.singular.server.commons.spring.security.SingularPermission;
import br.net.mirante.singular.server.commons.util.PetitionUtil;
import br.net.mirante.singular.server.commons.wicket.view.form.FormPageConfig;
import br.net.mirante.singular.server.commons.wicket.view.util.DispatcherPageUtil;
import br.net.mirante.singular.support.persistence.enums.SimNao;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static br.net.mirante.singular.server.commons.flow.action.DefaultActions.ACTION_ANALYSE;
import static br.net.mirante.singular.server.commons.flow.action.DefaultActions.ACTION_DELETE;
import static br.net.mirante.singular.server.commons.flow.action.DefaultActions.ACTION_EDIT;
import static br.net.mirante.singular.server.commons.flow.action.DefaultActions.ACTION_RELOCATE;
import static br.net.mirante.singular.server.commons.flow.action.DefaultActions.ACTION_VIEW;
import static br.net.mirante.singular.server.commons.flow.rest.DefaultServerREST.DELETE;
import static br.net.mirante.singular.server.commons.flow.rest.DefaultServerREST.PATH_BOX_ACTION;
import static br.net.mirante.singular.server.commons.util.Parameters.SIGLA_FORM_NAME;

@Transactional
public class PetitionService<T extends PetitionEntity> implements Loggable {

    @Inject
    protected PetitionDAO<T> petitionDAO;

    @Inject
    protected GrupoProcessoDAO grupoProcessoDAO;

    @Inject
    protected IFormService formPersistenceService;

    @Inject
    protected TaskInstanceDAO taskInstanceDAO;

    @Inject
    protected DraftDAO draftDAO;

    @Inject
    protected PetitionerDAO petitionerDAO;

    @Inject
    protected FormPetitionDAO formPetitionDAO;

    @Inject
    protected PetitionContentHistoryDAO petitionContentHistoryDAO;

    @Inject
    protected AuthorizationService authorizationService;

    @Inject
    private IUserService userService;

    public T find(Long cod) {
        return petitionDAO.find(cod);
    }

    public T findByProcessCod(Integer cod) {
        return petitionDAO.findByProcessCod(cod);
    }

    public void delete(PeticaoDTO peticao) {
        petitionDAO.delete(petitionDAO.find(peticao.getCodPeticao()));
    }

    public void delete(Long idPeticao) {
        petitionDAO.delete(petitionDAO.find(idPeticao));
    }

    public long countQuickSearch(QuickFilter filter, String siglaProcesso, String formName) {
        return countQuickSearch(filter, Collections.singletonList(siglaProcesso), Collections.singletonList(formName));
    }

    public Long countQuickSearch(QuickFilter filter) {
        return countQuickSearch(filter, filter.getProcessesAbbreviation(), filter.getTypesNames());
    }

    public Long countQuickSearch(QuickFilter filter, List<String> siglasProcesso, List<String> formNames) {
        return petitionDAO.countQuickSearch(filter, siglasProcesso, formNames);
    }


    public List<PeticaoDTO> quickSearch(QuickFilter filter, String siglaProcesso, String formName) {
        return quickSearch(filter, Collections.singletonList(siglaProcesso), Collections.singletonList(formName));
    }


    public List<PeticaoDTO> quickSearch(QuickFilter filter, List<String> siglasProcesso, List<String> formNames) {
        return petitionDAO.quickSearch(filter, siglasProcesso, formNames);
    }

    public List<Map<String, Object>> quickSearchMap(QuickFilter filter) {
        final List<Map<String, Object>> list = petitionDAO.quickSearchMap(filter, filter.getProcessesAbbreviation(), filter.getTypesNames());
        parseResultsPetition(list);
        list.forEach(this::checkItemActions);
        for (Map<String, Object> map : list) {
            authorizationService.filterActions((String) map.get("type"), null, (List<BoxItemAction>) map.get("actions"), filter.getIdUsuarioLogado());
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
        actions.add(BoxItemAction.newExecuteInstante(item.get("codPeticao"), ACTION_RELOCATE.getName()));

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

    public FormKey saveOrUpdate(T peticao, SInstance instance, boolean createNewDraftIfDoesntExists, boolean mainForm) {
        return saveOrUpdate(peticao, instance, createNewDraftIfDoesntExists, mainForm, null);
    }

    public FormKey saveOrUpdate(T peticao, SInstance instance, boolean createNewDraftIfDoesntExists, boolean mainForm, Consumer<T> onSave) {

        if (instance == null) {
            return null;
        }

        final FormKey key;

        Integer codActor = userService.getUserCodIfAvailable();
        if (peticao.getCurrentDraftEntity() != null
                && findFormType(peticao.getCurrentDraftEntity().getForm().getCod()).getAbbreviation().equals(instance.getType().getName())) {
            key = formPersistenceService.insertOrUpdate(instance, codActor);
            saveOrUpdateDraft(key, peticao.getCurrentDraftEntity());
        } else if (createNewDraftIfDoesntExists) {
            key = formPersistenceService.insert(instance, codActor);
            peticao.setCurrentDraftEntity(saveOrUpdateDraft(key, createNewDraftWithoutSave()));
        } else {
            key = formPersistenceService.insertOrUpdate(instance, codActor);
            loadAndSetFormEntityFromKey(key, formSetterByName(mainForm, instance.getType().getName(), peticao));
        }

        if (peticao.getPetitioner() != null) {
            petitionerDAO.saveOrUpdate(peticao.getPetitioner());
        }

        petitionDAO.saveOrUpdate(peticao);

        if (onSave != null) {
            onSave.accept(peticao);
        }

        return key;
    }

    public FormTypeEntity findFormType(Long formEntityPK) {
        return formPersistenceService.loadFormEntity(formPersistenceService.keyFromObject(formEntityPK)).getFormType();
    }

    public FormTypeEntity findFormTypeFromVersion(Long formVersionPK) {
        final FormVersionEntity formVersionEntity = formPersistenceService.loadFormVersionEntity(formVersionPK);
        return formVersionEntity.getFormEntity().getFormType();
    }

    private Consumer<FormEntity> formSetterByName(boolean mainForm, String typeName, T petition) {
        return formEntity -> {

            final Optional<FormPetitionEntity> optionalOfFormPetitionEntity;

            if (mainForm) {
                optionalOfFormPetitionEntity = findFormPetitionEntityByTypeName(petition.getCod(), typeName);
            } else {
                optionalOfFormPetitionEntity = findFormPetitionEntityByTypeNameAndTask(petition.getCod(), typeName, getCurrentTaskDefinition(petition).map(TaskDefinitionEntity::getCod).orElse(null));
            }

            final FormPetitionEntity formPetitionEntity = optionalOfFormPetitionEntity.orElse(new FormPetitionEntity());

            formPetitionEntity.setForm(formEntity);
            formPetitionEntity.setPetition(petition);

            if (mainForm) {
                formPetitionEntity.setMainForm(SimNao.SIM);
            } else {
                formPetitionEntity.setMainForm(SimNao.NAO);
                formPetitionEntity.setTaskDefinitionEntity(petition.getProcessInstanceEntity().getCurrentTask().getTask().getTaskDefinition());
            }

            formPetitionDAO.saveOrUpdate(formPetitionEntity);

            if (!optionalOfFormPetitionEntity.isPresent()) {
                if (petition.getFormPetitionEntities() == null) {
                    petition.setFormPetitionEntities(new TreeSet<>());
                }
                petition.getFormPetitionEntities().add(formPetitionEntity);
            }

        };
    }

    private DraftEntity saveOrUpdateDraft(FormKey key, DraftEntity draftEntity) {
        loadAndSetFormEntityFromKey(key, draftEntity::setForm);
        draftEntity.setEditionDate(new Date());
        draftDAO.saveOrUpdate(draftEntity);
        return draftEntity;
    }

    private void loadAndSetFormEntityFromKey(FormKey key, Consumer<FormEntity> consumer) {
        consumer.accept(formPersistenceService.loadFormEntity(key));
    }

    private FormKey preparePetitionForTransition(T petition, SInstance instance, boolean mainForm) {
        if (petition.getCurrentDraftEntity() != null) {
            return consolidateDraft(petition, instance, mainForm);
        } else {
            return saveOrUpdate(petition, instance, false, mainForm);
        }
    }

    public FormKey send(T peticao, SInstance instance, boolean mainForm, String codResponsavel) {

        final FormKey              key               = preparePetitionForTransition(peticao, instance, mainForm);
        final ProcessDefinition<?> processDefinition = PetitionUtil.getProcessDefinition(peticao);
        final ProcessInstance      processInstance   = processDefinition.newInstance();

        savePetitionHistory(peticao.getCod(), key);
        processInstance.setDescription(peticao.getDescription());
        final ProcessInstanceEntity processEntity = processInstance.saveEntity();
        peticao.setProcessInstanceEntity(processEntity);
        processInstance.start();
        onSend(peticao, instance, processEntity, codResponsavel);

        return key;
    }

    protected void onSend(T peticao, SInstance instance, ProcessInstanceEntity processEntity, String codResponsavel) {
    }

    private void savePetitionHistory(Long petitionId, FormKey formKey) {

        final PetitionEntity     petitionEntity = petitionDAO.find(petitionId);
        final TaskInstanceEntity taskInstance   = findCurrentTaskByPetitionId(petitionId);
        final FormEntity         formEntity     = formPersistenceService.loadFormEntity(formKey);

        getLogger().info("Atualizando histórico da petição.");

        final PetitionContentHistoryEntity contentHistoryEntity = new PetitionContentHistoryEntity();

        contentHistoryEntity.setPetitionEntity(petitionEntity);

        if (taskInstance != null) {
            contentHistoryEntity.setActor(taskInstance.getAllocatedUser());
            contentHistoryEntity.setTaskInstanceEntity(taskInstance);
        }

        if (CollectionUtils.isNotEmpty(formEntity.getCurrentFormVersionEntity().getFormAnnotations())) {
            contentHistoryEntity.setFormAnnotationsVersions(formEntity.getCurrentFormVersionEntity().getFormAnnotations().stream().map(FormAnnotationEntity::getAnnotationCurrentVersion).collect(Collectors.toList()));
        }

        contentHistoryEntity.setPetitionerEntity(petitionEntity.getPetitioner());
        contentHistoryEntity.setHistoryDate(new Date());

        petitionContentHistoryDAO.saveOrUpdate(contentHistoryEntity);

        contentHistoryEntity.setFormVersionHistoryEntities(
                petitionEntity
                        .getFormPetitionEntities()
                        .stream()
                        .map(f -> formPetitionDAO.find(f.getCod()))
                        .filter(isMainFormOrIsForCurrentTaskDefinition(petitionEntity))
                        .map(f -> {
                            final FormVersionHistoryEntity formVersionHistoryEntity = new FormVersionHistoryEntity();
                            formVersionHistoryEntity.setMainForm(f.getMainForm());
                            formVersionHistoryEntity.setCodFormVersion(f.getForm().getCurrentFormVersionEntity().getCod());
                            formVersionHistoryEntity.setCodPetitionContentHistory(contentHistoryEntity.getCod());
                            formVersionHistoryEntity.setFormVersion(f.getForm().getCurrentFormVersionEntity());
                            formVersionHistoryEntity.setPetitionContentHistory(contentHistoryEntity);
                            return formVersionHistoryEntity;
                        })
                        .collect(Collectors.toList())
        );


    }

    private Predicate<FormPetitionEntity> isMainFormOrIsForCurrentTaskDefinition(PetitionEntity petitionEntity) {
        return f ->
                Optional.ofNullable(f)
                        .map(FormPetitionEntity::getMainForm).map(SimNao.SIM::equals).orElse(false)
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

    private FormKey consolidateDraft(T petition, SInstance draftInstance, boolean mainForm) {

        final SDocumentFactory documentFactory = draftInstance.getDocument().getDocumentFactoryRef().get();
        final RefType          refType         = draftInstance.getDocument().getRootRefType().orElse(null);

        if (documentFactory == null || refType == null) {
            throw new SingularFormPersistenceException("Não foi possivel resolver as dependencias para consolidar o rascunho.");
        }

        FormKey                            petitionFormKey;
        final String                       typeName = draftInstance.getType().getName();
        final Optional<FormPetitionEntity> optionalOfForm;

        if (mainForm) {
            optionalOfForm = findFormPetitionEntityByTypeName(petition.getCod(), typeName);
        } else {
            optionalOfForm = findFormPetitionEntityByTypeNameAndTask(petition.getCod(), typeName, getCurrentTaskDefinition(petition).map(TaskDefinitionEntity::getCod).orElse(null));
        }

        Integer codActor = userService.getUserCodIfAvailable();
        if (optionalOfForm.isPresent()) {
            petitionFormKey = formPersistenceService.keyFromObject(optionalOfForm.get().getForm().getCod());
            final SInstance petitionFormInstance = formPersistenceService.loadSInstance(petitionFormKey, refType, documentFactory);
            copyValuesAndAnnotations(draftInstance, petitionFormInstance);
            petitionFormKey = formPersistenceService.newVersion(petitionFormInstance, codActor);
            loadAndSetFormEntityFromKey(petitionFormKey, formSetterByName(mainForm, typeName, petition));
        } else {
            petitionFormKey = formPersistenceService.insert(draftInstance, codActor);
            loadAndSetFormEntityFromKey(petitionFormKey, formSetterByName(mainForm, typeName, petition));
        }

        final DraftEntity currentDraftEntity = petition.getCurrentDraftEntity();

        petition.setCurrentDraftEntity(null);
        draftDAO.delete(currentDraftEntity);
        petitionDAO.saveOrUpdate(petition);

        return petitionFormKey;
    }

    private void copyValuesAndAnnotations(SInstance source, SInstance target) {
        Value.copyValues(source, target);
        SIList<SIAnnotation> annotations = source.as(AtrAnnotation::new).persistentAnnotations();
        if (annotations != null) {
            Iterator<SIAnnotation> it = annotations.iterator();
            while (it.hasNext()) {
                SIAnnotation sourceAnnotation = it.next();
                //obtem o caminho completo da instancia anotada no formulario raiz
                SInstances.findDescendantById(source, sourceAnnotation.getTargetId()).ifPresent(si -> {
                    String pathFromRoot = si.getPathFromRoot();
                    //localiza a instancia correspondente no formulario destino
                    SInstance targetInstance = ((SIComposite) target).getField(pathFromRoot);
                    //Copiando todos os valores da anotação (inclusive o id na sinstance antiga)
                    SIAnnotation targetAnnotation = targetInstance.as(AtrAnnotation::new).annotation();
                    Value.copyValues(sourceAnnotation, targetAnnotation);
                    //Corrigindo o ID
                    targetAnnotation.setTargetId(targetInstance.getId());
                });

            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public FormKey saveAndExecuteTransition(String transitionName, T peticao, SInstance instance, boolean mainForm, BiConsumer<T, String> onTransition) {
        try {
            if (onTransition != null) {
                onTransition.accept(peticao, transitionName);
            }
            final FormKey key = preparePetitionForTransition(peticao, instance, mainForm);
            savePetitionHistory(peticao.getCod(), key);
            final Class<? extends ProcessDefinition> clazz = PetitionUtil.getProcessDefinition(peticao).getClass();
            final ProcessInstance pi = Flow.getProcessInstance(clazz, peticao.getProcessInstanceEntity().getCod());
            checkTaskIsEqual(peticao.getProcessInstanceEntity(), pi);
            pi.executeTransition(transitionName);
            return key;
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


    public T createNewPetitionWithoutSave(Class<T> petitionClass, FormPageConfig config, BiConsumer<T, FormPageConfig> creationListener) {

        final T petition;

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

    private DraftEntity createNewDraftWithoutSave() {
        final DraftEntity draftEntity = new DraftEntity();
        draftEntity.setStartDate(new Date());
        draftEntity.setEditionDate(new Date());
        return draftEntity;
    }

    public Optional<FormPetitionEntity> findFormPetitionEntityByTypeName(Long petitionPK, String typeName) {
        return Optional.ofNullable(formPetitionDAO.findFormPetitionEntityByTypeName(petitionPK, typeName));
    }

    public Optional<FormPetitionEntity> findFormPetitionEntityByTypeNameAndTask(Long petitionPK, String typeName, Integer taskDefinitionEntityPK) {
        return Optional.ofNullable(formPetitionDAO.findFormPetitionEntityByTypeNameAndTask(petitionPK, typeName, taskDefinitionEntityPK));
    }

    public Optional<FormPetitionEntity> findLastFormPetitionEntityByTypeName(Long petitionPK, String typeName) {
        return Optional.ofNullable(formPetitionDAO.findLastFormPetitionEntityByTypeName(petitionPK, typeName));
    }

    private Optional<TaskDefinitionEntity> getCurrentTaskDefinition(T petition) {
        ProcessInstanceEntity processInstanceEntity = petition.getProcessInstanceEntity();
        if (processInstanceEntity != null) {
            return Optional.of(processInstanceEntity.getCurrentTask().getTask().getTaskDefinition());
        }
        return Optional.empty();
    }

    public ProcessDefinitionEntity findEntityProcessDefinitionByClass(Class<? extends ProcessDefinition> clazz) {
        return (ProcessDefinitionEntity) Optional
                .ofNullable(Flow.getProcessDefinition(clazz))
                .map(ProcessDefinition::getEntityProcessDefinition)
                .orElseThrow(() -> new SingularFlowException("Não foi possivel recuperar a definição do processo"));
    }

    public List<PetitionContentHistoryEntity> listPetitionContentHistoryByCodInstancePK(int instancePK) {
        return petitionContentHistoryDAO.listPetitionContentHistoryByCodInstancePK(instancePK);
    }
}
