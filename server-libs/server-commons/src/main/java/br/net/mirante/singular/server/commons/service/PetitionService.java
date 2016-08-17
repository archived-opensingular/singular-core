package br.net.mirante.singular.server.commons.service;


import br.net.mirante.singular.commons.util.Loggable;
import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskInstance;
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
import br.net.mirante.singular.persistence.entity.TaskInstanceEntity;
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
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionContentHistoryEntity;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.server.commons.persistence.filter.QuickFilter;
import br.net.mirante.singular.server.commons.service.dto.BoxItemAction;
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
import java.util.stream.Collectors;

import static br.net.mirante.singular.server.commons.flow.rest.DefaultServerREST.DELETE;
import static br.net.mirante.singular.server.commons.flow.rest.DefaultServerREST.PATH_BOX_ACTION;
import static br.net.mirante.singular.server.commons.util.Parameters.SIGLA_FORM_NAME;
import static br.net.mirante.singular.server.commons.util.ServerActionConstants.ACTION_DELETE;
import static br.net.mirante.singular.server.commons.util.ServerActionConstants.ACTION_EDIT;
import static br.net.mirante.singular.server.commons.util.ServerActionConstants.ACTION_RELOCATE;
import static br.net.mirante.singular.server.commons.util.ServerActionConstants.ACTION_VIEW;

@Transactional
public class PetitionService<T extends PetitionEntity> implements Loggable {

    @Inject
    private PetitionDAO<T> petitionDAO;

    @Inject
    private GrupoProcessoDAO grupoProcessoDAO;

    @Inject
    private IFormService formPersistenceService;

    @Inject
    private TaskInstanceDAO taskInstanceDAO;

    @Inject
    private DraftDAO draftDAO;

    @Inject
    private PetitionerDAO petitionerDAO;

    @Inject
    private FormPetitionDAO formPetitionDAO;

    @Inject
    private PetitionContentHistoryDAO petitionContentHistoryDAO;


    public T find(Long cod) {
        return petitionDAO.find(cod);
    }

    public T findByProcessCod(Integer cod) {
        return petitionDAO.findByProcessCod(cod);
    }

    public void delete(PeticaoDTO peticao) {
        petitionDAO.delete(petitionDAO.find(peticao.getCod()));
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


    public List<? extends PeticaoDTO> quickSearch(QuickFilter filter, String siglaProcesso, String formName) {
        return quickSearch(filter, Collections.singletonList(siglaProcesso), Collections.singletonList(formName));
    }


    public List<? extends PeticaoDTO> quickSearch(QuickFilter filter, List<String> siglasProcesso, List<String> formNames) {
        return petitionDAO.quickSearch(filter, siglasProcesso, formNames);
    }

    public List<Map<String, Object>> quickSearchMap(QuickFilter filter) {
        final List<Map<String, Object>> list = petitionDAO.quickSearchMap(filter, filter.getProcessesAbbreviation(), filter.getTypesNames());
        list.forEach(this::checkItemActions);
        return list;
    }

    private void checkItemActions(Map<String, Object> item) {
        List<BoxItemAction> actions = new ArrayList<>();
        actions.add(createPopupBoxItemAction(item, FormActions.FORM_FILL, ACTION_EDIT));
        actions.add(createPopupBoxItemAction(item, FormActions.FORM_VIEW, ACTION_VIEW));
        actions.add(createDeleteAction(item));
        actions.add(BoxItemAction.newExecuteInstante(item.get("cod"), ACTION_RELOCATE));

        appendItemActions(item, actions);

        String processKey = (String) item.get("processType");
        final ProcessDefinition<?> processDefinition = Flow.getProcessDefinitionWith(processKey);
        final ActionConfig actionConfig = processDefinition.getMetaDataValue(ActionConfig.KEY);
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
        String endpointUrl = PATH_BOX_ACTION + DELETE + "?id=" + item.get("cod");

        final BoxItemAction boxItemAction = new BoxItemAction();
        boxItemAction.setName(ACTION_DELETE);
        boxItemAction.setEndpoint(endpointUrl);
        return boxItemAction;
    }

    protected BoxItemAction createPopupBoxItemAction(Map<String, Object> item, FormActions formAction, String actionName) {
        String endpoint = DispatcherPageUtil
                .baseURL("")
                .formAction(formAction.getId())
                .formId(item.get("cod"))
                .param(SIGLA_FORM_NAME, item.get("type"))
                .build();

        final BoxItemAction boxItemAction = new BoxItemAction();
        boxItemAction.setName(actionName);
        boxItemAction.setEndpoint(endpoint);
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

        if (peticao.getCurrentDraftEntity() != null
                && getFormType(peticao.getCurrentDraftEntity().getForm().getCod()).getAbbreviation().equals(instance.getType().getName())) {
            key = formPersistenceService.insertOrUpdate(instance);
            saveOrUpdateDraft(key, peticao.getCurrentDraftEntity());
        } else if (createNewDraftIfDoesntExists) {
            key = formPersistenceService.insert(instance);
            peticao.setCurrentDraftEntity(saveOrUpdateDraft(key, createNewDraftWithoutSave()));
        } else {
            key = formPersistenceService.insertOrUpdate(instance);
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

    public FormTypeEntity getFormType(Long formEntityPK) {
        return formPersistenceService.loadFormEntity(formPersistenceService.keyFromObject(formEntityPK)).getFormType();
    }

    private Consumer<FormEntity> formSetterByName(boolean mainForm, String typeName, T petition) {
        return formEntity -> {

            final Optional<FormPetitionEntity> optionalOfFormPetitionEntity = findFormPetitionEntityByTypeName(petition.getCod(), typeName);
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

    public FormKey send(T peticao, SInstance instance, boolean mainForm) {
        final FormKey key = preparePetitionForTransition(peticao, instance, mainForm);
        final ProcessDefinition<?> processDefinition = PetitionUtil.getProcessDefinition(peticao);
        final ProcessInstance processInstance = processDefinition.newInstance();

        savePetitionHistory(peticao.getCod(), key);

        processInstance.setDescription(peticao.getDescription());

        final ProcessInstanceEntity processEntity = processInstance.saveEntity();

        peticao.setProcessInstanceEntity(processEntity);

        processInstance.start();
        return key;
    }

    private void savePetitionHistory(Long petitionId, FormKey formKey) {
        PetitionEntity petitionEntity =  petitionDAO.find(petitionId);
        TaskInstanceEntity taskInstance = findCurrentTaskByPetitionId(petitionId);
        FormEntity formEntity = formPersistenceService.loadFormEntity(formKey);
        getLogger().info("Atualizando histórico da petição.");
        PetitionContentHistoryEntity contentHistoryEntity = new PetitionContentHistoryEntity();
        contentHistoryEntity.setPetitionEntity(petitionEntity);
        contentHistoryEntity.setFormVersionEntity(formEntity.getCurrentFormVersionEntity());
        if (taskInstance != null){
            contentHistoryEntity.setActor(taskInstance.getAllocatedUser());
            contentHistoryEntity.setTaskInstanceEntity(taskInstance);
        }
        if (CollectionUtils.isNotEmpty(formEntity.getCurrentFormVersionEntity().getFormAnnotations())) {
            contentHistoryEntity.setFormAnnotationsVersions(formEntity.getCurrentFormVersionEntity().getFormAnnotations().stream().map(FormAnnotationEntity::getAnnotationCurrentVersion).collect(Collectors.toList()));
        }
        contentHistoryEntity.setPetitionerEntity(petitionEntity.getPetitioner());
        contentHistoryEntity.setHistoryDate(new Date());
        petitionContentHistoryDAO.saveOrUpdate(contentHistoryEntity);
    }

    private FormKey consolidateDraft(T petition, SInstance draftInstance, boolean mainForm) {

        final SDocumentFactory documentFactory = draftInstance.getDocument().getDocumentFactoryRef().get();
        final RefType refType = draftInstance.getDocument().getRootRefType().orElse(null);

        if (documentFactory == null || refType == null) {
            throw new SingularFormPersistenceException("Não foi possivel resolver as dependencias para consolidar o rascunho.");
        }

        FormKey petitionFormKey;
        final String typeName = draftInstance.getType().getName();
        final Optional<FormPetitionEntity> optionalOfForm = findFormPetitionEntityByTypeName(petition.getCod(), typeName);

        if (optionalOfForm.isPresent()) {
            petitionFormKey = formPersistenceService.keyFromObject(optionalOfForm.get().getForm().getCod());
            final SInstance petitionFormInstance = formPersistenceService.loadSInstance(petitionFormKey, refType, documentFactory);
            copyValuesAndAnnotations(draftInstance, petitionFormInstance);
            petitionFormKey = formPersistenceService.newVersion(petitionFormInstance);
            loadAndSetFormEntityFromKey(petitionFormKey, formSetterByName(mainForm, typeName, petition));
        } else {
            petitionFormKey = formPersistenceService.insert(draftInstance);
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
    public FormKey saveAndExecuteTransition(String transitionName, T peticao, SInstance instance, boolean mainForm) {
        try {
            final FormKey key = preparePetitionForTransition(peticao, instance, mainForm);
            savePetitionHistory(peticao.getCod(), key);
            final Class<? extends ProcessDefinition> clazz = PetitionUtil.getProcessDefinition(peticao).getClass();
            final ProcessInstance pi = Flow.getProcessInstance(clazz, peticao.getProcessInstanceEntity().getCod());
            pi.executeTransition(transitionName);
            return key;
        } catch (Exception e) {
            throw new SingularServerException(e.getMessage(), e);
        }
    }

    public List<? extends TaskInstanceDTO> listTasks(int first, int count, String sortProperty, boolean ascending, String siglaFluxo, List<String> idsPerfis, String filtroRapido, boolean concluidas) {
        return taskInstanceDAO.findTasks(first, count, sortProperty, ascending, siglaFluxo, idsPerfis, filtroRapido, concluidas);
    }

    public Integer countTasks(String siglaFluxo, List<String> idsPerfis, String filtroRapido, boolean concluidas) {
        return taskInstanceDAO.countTasks(siglaFluxo, idsPerfis, filtroRapido, concluidas);
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

}
