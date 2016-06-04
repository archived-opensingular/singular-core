package br.net.mirante.singular.server.commons.service;


import br.net.mirante.singular.flow.core.*;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.persistence.FormKey;
import br.net.mirante.singular.form.persistence.FormKeyNumber;
import br.net.mirante.singular.form.service.IFormService;
import br.net.mirante.singular.persistence.entity.ProcessGroupEntity;
import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;
import br.net.mirante.singular.persistence.entity.TaskInstanceEntity;
import br.net.mirante.singular.server.commons.exception.SingularServerException;
import br.net.mirante.singular.server.commons.persistence.dao.flow.GrupoProcessoDAO;
import br.net.mirante.singular.server.commons.persistence.dao.flow.TaskInstanceDAO;
import br.net.mirante.singular.server.commons.persistence.dao.form.PetitionDAO;
import br.net.mirante.singular.server.commons.persistence.dto.PeticaoDTO;
import br.net.mirante.singular.server.commons.persistence.dto.TaskInstanceDTO;
import br.net.mirante.singular.server.commons.persistence.entity.form.AbstractPetitionEntity;
import br.net.mirante.singular.server.commons.persistence.filter.QuickFilter;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Transactional
public class PetitionService<T extends AbstractPetitionEntity> {

    @Inject
    private PetitionDAO<T> petitionDAO;

    @Inject
    private GrupoProcessoDAO grupoProcessoDAO;

    @Inject
    private IFormService formPersistenceService;

    @Inject
    private TaskInstanceDAO taskInstanceDAO;
    
    public T find(Long cod) {
        return petitionDAO.find(cod);
    }

    public T findByProcessCod(Integer cod) {
        return petitionDAO.findByProcessCod(cod);
    }
    
    public void delete(PeticaoDTO peticao) {
        petitionDAO.delete(petitionDAO.find(peticao.getCod()));
    }


    public long countQuickSearch(QuickFilter filtro, String siglaProcesso) {
        return countQuickSearch(filtro, Collections.singletonList(siglaProcesso));
    }


    public Long countQuickSearch(QuickFilter filtro, List<String> siglasProcesso) {
        return petitionDAO.countQuickSearch(filtro, siglasProcesso);
    }


    public List<? extends PeticaoDTO> quickSearch(QuickFilter filtro, String siglaProcesso) {
        return quickSearch(filtro, Collections.singletonList(siglaProcesso));
    }


    public List<? extends PeticaoDTO> quickSearch(QuickFilter filtro, List<String> siglasProcesso) {
        return petitionDAO.quickSearch(filtro, siglasProcesso);
    }

    public FormKey saveOrUpdate(T peticao, SInstance instance) {
        FormKey key;
        if(instance != null){
            key = formPersistenceService.insertOrUpdate(instance);
            Long keyAsLong = ((FormKeyNumber) key).longValue();
            if (peticao.getCodForm() != null && ! peticao.getCodForm().equals(keyAsLong)) {
                throw new SingularServerException("Tentado criar novo formulário mas a petição já possui um");
            }
            peticao.setCodForm(keyAsLong);
        } else {
            peticao.setCodForm(null);
            key = null;
        }
        
        petitionDAO.saveOrUpdate(peticao);
        return key;
    }

    public FormKey send(T peticao, SInstance instance) {
        ProcessDefinition<?> processDefinition = Flow.getProcessDefinitionWith(peticao.getProcessType());
        ProcessInstance processInstance = processDefinition.newInstance();
        processInstance.setDescription(peticao.getDescription());
        
        ProcessInstanceEntity processEntity = processInstance.saveEntity();
        peticao.setProcessInstanceEntity(processEntity);
        FormKey key = saveOrUpdate(peticao, instance);
        
        processInstance.start();

        return key;
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public FormKey saveAndExecuteTransition(String transitionName, T peticao, SInstance instance) {
        try {
            
            FormKey key = saveOrUpdate(peticao, instance);
            
            final Class<? extends ProcessDefinition> clazz = Flow.getProcessDefinitionWith(peticao.getProcessType()).getClass();
            ProcessInstance pi = Flow.getProcessInstance(clazz, peticao.getProcessInstanceEntity().getCod());
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
    
}
