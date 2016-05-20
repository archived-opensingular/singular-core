package br.net.mirante.singular.server.commons.service;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.transaction.annotation.Transactional;

import br.net.mirante.singular.flow.core.Flow;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.service.FormDTO;
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

    public void saveOrUpdate(T peticao, FormDTO form, SInstance instance) {
        if(instance != null){
            formPersistenceService.saveOrUpdateForm(form, instance);
            peticao.setCodForm(form.getCod());
        } else {
            peticao.setCodForm(null);
        }
        
        petitionDAO.saveOrUpdate(peticao);
    }

    public void send(T peticao, FormDTO form, SInstance instance) {
        ProcessDefinition<?> processDefinition = Flow.getProcessDefinitionWith(peticao.getProcessType());
        ProcessInstance processInstance = processDefinition.newInstance();
        processInstance.setDescription(peticao.getDescription());
        
        ProcessInstanceEntity processEntity = processInstance.saveEntity();
        peticao.setProcessInstanceEntity(processEntity);
        saveOrUpdate(peticao, form, instance);
        
        processInstance.start();
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void saveAndExecuteTransition(String transitionName, T peticao, FormDTO form, SInstance instance) {
        try {
            
            saveOrUpdate(peticao, form, instance);
            
            final Class<? extends ProcessDefinition> clazz = Flow.getProcessDefinitionWith(peticao.getProcessType()).getClass();
            ProcessInstance pi = Flow.getProcessInstance(clazz, peticao.getProcessInstanceEntity().getCod());
            pi.executeTransition(transitionName);
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
