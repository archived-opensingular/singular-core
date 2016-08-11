package br.net.mirante.singular.server.commons.service;


import static br.net.mirante.singular.form.persistence.SPackageFormPersistence.ATR_FORM_KEY;
import static br.net.mirante.singular.server.commons.flow.rest.DefaultServerREST.DELETE;
import static br.net.mirante.singular.server.commons.flow.rest.DefaultServerREST.PATH_BOX_ACTION;
import static br.net.mirante.singular.server.commons.util.Parameters.SIGLA_FORM_NAME;
import static br.net.mirante.singular.server.commons.util.ServerActionConstants.*;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;

import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.persistence.SingularFormPersistenceException;
import br.net.mirante.singular.form.persistence.entity.FormEntity;
import br.net.mirante.singular.form.persistence.entity.FormTypeEntity;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.persistence.entity.ProcessDefinitionEntity;
import br.net.mirante.singular.server.commons.persistence.dao.flow.FormTypeDAO;
import br.net.mirante.singular.server.commons.persistence.dao.form.DraftDAO;
import br.net.mirante.singular.server.commons.persistence.entity.form.DraftEntity;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.server.commons.util.PetitionUtil;
import br.net.mirante.singular.server.commons.wicket.view.form.FormPageConfig;
import br.net.mirante.singular.support.persistence.GenericDAO;
import br.net.mirante.singular.support.persistence.enums.SimNao;
import org.springframework.transaction.annotation.Transactional;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.service.IFormService;
import br.net.mirante.singular.persistence.entity.ProcessGroupEntity;
import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;
import br.net.mirante.singular.persistence.entity.TaskInstanceEntity;
import br.net.mirante.singular.server.commons.exception.SingularServerException;
import br.net.mirante.singular.server.commons.flow.rest.ActionConfig;
import br.net.mirante.singular.server.commons.form.FormActions;
import br.net.mirante.singular.server.commons.persistence.dao.flow.GrupoProcessoDAO;
import br.net.mirante.singular.server.commons.persistence.dao.flow.TaskInstanceDAO;
import br.net.mirante.singular.server.commons.persistence.dao.form.PetitionDAO;
import br.net.mirante.singular.server.commons.persistence.dto.PeticaoDTO;
import br.net.mirante.singular.server.commons.persistence.dto.TaskInstanceDTO;
import br.net.mirante.singular.server.commons.persistence.filter.QuickFilter;
import br.net.mirante.singular.server.commons.service.dto.BoxItemAction;
import br.net.mirante.singular.server.commons.wicket.view.util.DispatcherPageUtil;

@Transactional
public class PetitionService<T extends PetitionEntity> {

    @Inject
    private PetitionDAO<T> petitionDAO;

    @Inject
    private GrupoProcessoDAO grupoProcessoDAO;

    @Inject
    private IFormService formPersistenceService;

    @Inject
    private TaskInstanceDAO taskInstanceDAO;

    @Inject
    private FormTypeDAO formTypeDAO;

    @Inject
    private DraftDAO draftDAO;

    @Inject
    private GenericDAO genericDAO;

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

    //TODO: FORM_ANNOTATION_VERSION
    //TODO CONSIDERAR QUE AS ANOTAÇÕES PODEM OU NÃO SER SALVAS
    public FormKey saveOrUpdate(T peticao, SInstance instance, boolean createNewDraftIfDoesntExists) {

        if (instance == null) {
            return null;
        }

        final FormKey key;

        if (peticao.getCurrentDraftEntity() != null) {
            key = formPersistenceService.insertOrUpdate(instance);
            saveOrUpdateDraft(key, peticao.getCurrentDraftEntity());
        } else if (createNewDraftIfDoesntExists) {
            key = formPersistenceService.insert(instance);
            peticao.setCurrentDraftEntity(saveOrUpdateDraft(key, createNewDraftWithoutSave()));
        } else {
            key = formPersistenceService.insertOrUpdate(instance);
            loadAndSetFormEntityFromKey(key, peticao::setForm);
        }

        if (peticao.getPetitioner() != null) {
            genericDAO.saveOrUpdate(peticao.getPetitioner());
        }

        petitionDAO.saveOrUpdate(peticao);

        return key;
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

    private FormKey preparePetitionForTransition(T petition, SInstance instance) {
        if (petition.getCurrentDraftEntity() != null) {
            return consolidateDraft(petition, instance);
        } else {
            return saveOrUpdate(petition, instance, false);
        }
    }

    public FormKey send(T peticao, SInstance instance) {

        final FormKey              key               = preparePetitionForTransition(peticao, instance);
        final ProcessDefinition<?> processDefinition = PetitionUtil.getProcessDefinition(peticao);
        final ProcessInstance      processInstance   = processDefinition.newInstance();

        processInstance.setDescription(peticao.getDescription());

        final ProcessInstanceEntity processEntity = processInstance.saveEntity();

        peticao.setProcessInstanceEntity(processEntity);

        processInstance.start();

        return key;
    }

    public FormKey consolidateDraft(T petition, SInstance draftInstance) {

        final SDocumentFactory documentFactory = draftInstance.getDocument().getDocumentFactoryRef().get();
        final RefType          refType         = draftInstance.getDocument().getRootRefType().orElse(null);

        if (documentFactory == null || refType == null) {
            throw new SingularFormPersistenceException("Não foi possivel resolver as dependencias para consolidar o rascunho.");
        }

        FormKey petitionFormKey;

        if (petition.getForm() != null) {
            petitionFormKey = formPersistenceService.keyFromObject(petition.getForm().getCod());
            final SInstance petitionFormInstance = formPersistenceService.loadSInstance(petitionFormKey, refType, documentFactory);
            Value.copyValues(draftInstance, petitionFormInstance);
            petitionFormKey = formPersistenceService.newVersion(petitionFormInstance);
            loadAndSetFormEntityFromKey(petitionFormKey, petition::setForm);
        } else {
            petitionFormKey = formPersistenceService.insert(draftInstance);
            loadAndSetFormEntityFromKey(petitionFormKey, petition::setForm);
        }

        final DraftEntity currentDraftEntity = petition.getCurrentDraftEntity();

        petition.setCurrentDraftEntity(null);
        genericDAO.delete(currentDraftEntity);
        genericDAO.saveOrUpdate(petition);

        return petitionFormKey;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public FormKey saveAndExecuteTransition(String transitionName, T peticao, SInstance instance) {
        try {
            final FormKey                            key   = preparePetitionForTransition(peticao, instance);
            final Class<? extends ProcessDefinition> clazz = PetitionUtil.getProcessDefinition(peticao).getClass();
            final ProcessInstance                    pi    = Flow.getProcessInstance(clazz, peticao.getProcessInstanceEntity().getCod());
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

    public List<MTransition> listCurrentTaskTransitions(String petitionId) {
        return Optional
                .ofNullable(Flow.getTaskInstance(findCurrentTaskByPetitionId(petitionId)))
                .map(TaskInstance::getFlowTask)
                .map(MTask::getTransitions)
                .orElse(Collections.emptyList());
    }

    public TaskInstanceEntity findCurrentTaskByPetitionId(String petitionId) {
        List<TaskInstanceEntity> taskInstances = taskInstanceDAO.findCurrentTasksByPetitionId(petitionId);
        if (taskInstances.isEmpty()) {
            return null;
        } else {
            return taskInstances.get(0);
        }
    }

    public List<ProcessGroupEntity> listarTodosGruposProcesso() {
        return grupoProcessoDAO.listarTodosGruposProcesso();
    }

    public ProcessGroupEntity findByProcessGroupName(String name) {
        return grupoProcessoDAO.findByName(name);
    }

    public ProcessGroupEntity findByProcessGroupCod(String cod) {
        return grupoProcessoDAO.get(cod);
    }

    public FormTypeEntity getOrCreateNewFormTypeEntity(String abbreviation) {
        FormTypeEntity formType = formTypeDAO.findFormTypeByAbbreviation(abbreviation);
        if (formType == null) {
            formType = createNewFormTypeEntity(abbreviation);
        }
        return formType;
    }

    private FormTypeEntity createNewFormTypeEntity(String abbreviation) {
        final FormTypeEntity formType = new FormTypeEntity();
        formType.setAbbreviation(abbreviation);
        formType.setCacheVersionNumber(1L);//?????????????????????
        formTypeDAO.saveOrUpdate(formType);
        return formType;
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

}
