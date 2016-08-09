package br.net.mirante.singular.server.commons.service;


import static br.net.mirante.singular.server.commons.flow.rest.DefaultServerREST.DELETE;
import static br.net.mirante.singular.server.commons.flow.rest.DefaultServerREST.PATH_BOX_ACTION;
import static br.net.mirante.singular.server.commons.util.Parameters.SIGLA_FORM_NAME;
import static br.net.mirante.singular.server.commons.util.ServerActionConstants.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import br.net.mirante.singular.form.persistence.entity.FormEntity;
import br.net.mirante.singular.form.persistence.entity.FormTypeEntity;
import br.net.mirante.singular.form.persistence.entity.FormVersionEntity;
import br.net.mirante.singular.persistence.entity.ProcessDefinitionEntity;
import br.net.mirante.singular.server.commons.persistence.dao.flow.FormTypeDAO;
import br.net.mirante.singular.server.commons.persistence.entity.form.DraftEntity;
import br.net.mirante.singular.server.commons.persistence.entity.form.PetitionEntity;
import br.net.mirante.singular.server.commons.util.PetitionUtil;
import br.net.mirante.singular.server.commons.wicket.view.form.FormPageConfig;
import org.springframework.transaction.annotation.Transactional;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.persistence.FormKeyNumber;
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
    public FormKey saveOrUpdate(T peticao, SInstance instance) {
        FormKey key;
        if (instance != null) {
            key = formPersistenceService.insertOrUpdate(instance);
            Long keyAsLong = ((FormKeyNumber) key).longValue();
            if (!keyAsLong.equals(Optional
                    .ofNullable(peticao)
                    .map(PetitionEntity::getCurrentDraftEntity)
                    .map(DraftEntity::getFormVersionEntity)
                    .map(FormVersionEntity::getFormEntity)
                    .map(FormEntity::getCod)
                    .orElse(keyAsLong))) { //chave diferente de null e diferente da chave retornada.
                throw new SingularServerException("Tentado criar novo formulário mas a petição já possui um");
            }
            //TODO BUD: VINCULAR VERSÃO DO FORM E DO RASCUNHO, criar rascunho se não existir
//            peticao.setCodForm(keyAsLong);
            peticao.setCurrentFormVersionEntity(formPersistenceService.loadFormEntity(key).getCurrentFormVersionEntity());
        } else {
//            peticao.setCodForm(null);
            key = null;
        }

        petitionDAO.saveOrUpdate(peticao);

        return key;
    }

    public FormKey send(T peticao, SInstance instance) {

        final ProcessDefinition<?> processDefinition = PetitionUtil.getProcessDefinition(peticao);
        final ProcessInstance      processInstance   = processDefinition.newInstance();

        //TODO falta a descrição da petição
        //processInstance.setDescription(peticao.getDescription());
        processInstance.setDescription("Descrição temporaria");

        final ProcessInstanceEntity processEntity = processInstance.saveEntity();

        peticao.setProcessInstanceEntity(processEntity);

        final FormKey key = saveOrUpdate(peticao, instance);

        processInstance.start();

        return key;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public FormKey saveAndExecuteTransition(String transitionName, T peticao, SInstance instance) {
        try {

            final FormKey                            key   = saveOrUpdate(peticao, instance);
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

    public T createNewPetitionWithoutSave(Class<T> petitionClass, FormPageConfig config) {

        T petition;

        try {
            petition = petitionClass.newInstance();
        } catch (Exception e) {
            throw new SingularServerException("Error creating new petition instance", e);
        }

        final FormVersionEntity formVersionEntity = new FormVersionEntity();
        final FormEntity        formEntity        = new FormEntity();

        formEntity.setFormType(getOrCreateNewFormTypeEntity(config.getFormType()));
        formEntity.setCurrentFormVersionEntity(formVersionEntity);

        formVersionEntity.setFormEntity(formEntity);

        if (config.containsProcessDefinition()) {
            petition.setProcessDefinitionEntity((ProcessDefinitionEntity) Flow.getProcessDefinition(config.getProcessDefinition()).getEntityProcessDefinition());
        }

//        petition.setProcessName(getTypeLabel(config.getFormType()));

        return petition;
    }

}
