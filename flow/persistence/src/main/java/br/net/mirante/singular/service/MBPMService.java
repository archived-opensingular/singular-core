package br.net.mirante.singular.service;

import br.net.mirante.singular.entity.Categoria;
import br.net.mirante.singular.entity.DefinicaoProcesso;
import br.net.mirante.singular.entity.InstanciaPapel;
import br.net.mirante.singular.entity.InstanciaProcesso;
import br.net.mirante.singular.entity.InstanciaTarefa;
import br.net.mirante.singular.entity.Papel;
import br.net.mirante.singular.entity.Tarefa;
import br.net.mirante.singular.entity.Variavel;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskHistoricLog;
import br.net.mirante.singular.flow.core.entity.IEntityByCod;
import br.net.mirante.singular.flow.core.service.IPersistenceService;
import br.net.mirante.singular.flow.util.vars.VarInstance;
import br.net.mirante.singular.flow.util.vars.VarInstanceMap;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class MBPMService /*extends BeanSupport*/ implements IPersistenceService<Categoria, DefinicaoProcesso, InstanciaProcesso, InstanciaTarefa, Tarefa, Variavel, Papel, InstanciaPapel> {

//    @Autowired
//    private DmdDefinicaoDAO definicaoDAO;
//
//    @Autowired
//    private DmdDemandaDAO demandaDAO;

    @Override
    public InstanciaProcesso createProcessInstance(DefinicaoProcesso definicaoProcesso, Tarefa initialState) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InstanciaProcesso saveProcessInstance(InstanciaProcesso instance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InstanciaTarefa addTask(InstanciaProcesso instance, Tarefa tarefa) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void endTask(InstanciaTarefa instanciaTarefa, String transitionName, MUser responsibleUser) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProcessInstanceParent(InstanciaProcesso instance, InstanciaProcesso instanceFather) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InstanciaPapel setInstanceUserRole(InstanciaProcesso instance, Papel role, MUser user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeInstanceUserRole(InstanciaProcesso instance, InstanciaPapel instanciaPapel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Integer updateVariableValue(ProcessInstance instance, VarInstance varInstance, Integer dbVariableCod) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setParentTask(InstanciaProcesso childrenInstance, InstanciaTarefa parentTask) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateTask(InstanciaTarefa instanciaTarefa) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Categoria retrieveOrCreateCategoryWith(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DefinicaoProcesso retrieveProcessDefinitionByCod(Serializable cod) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DefinicaoProcesso retrieveProcessDefinitionByAbbreviation(String abbreviation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DefinicaoProcesso retrieveOrCreateProcessDefinitionFor(ProcessDefinition<?> processDefinition) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InstanciaProcesso retrieveProcessInstanceByCod(Serializable cod) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Tarefa retrieveTaskStateByCod(Serializable cod) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Tarefa retrieveOrCreateStateFor(DefinicaoProcesso definicaoProcesso, MTask<?> mTask) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateProcessDefinition(DefinicaoProcesso definicaoProcesso) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TaskHistoricLog saveTaskHistoricLog(InstanciaTarefa instanciaTarefa, String typeDescription, String detail, MUser allocatedUser, MUser responsibleUser, Date dateHour, InstanciaProcesso generatedProcessInstance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void saveVariableHistoric(Date dateHour, InstanciaProcesso instance, InstanciaTarefa originTask, InstanciaTarefa destinationTask, VarInstanceMap<?> instanceMap) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<? extends MUser> retrieveUsersByCod(Collection<Integer> cods) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void refreshModel(IEntityByCod model) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flushSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void commitTransaction() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<InstanciaProcesso> retrieveProcessInstancesWith(Collection<? extends Tarefa> tarefas) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<InstanciaProcesso> retrieveProcessInstancesWith(DefinicaoProcesso definicaoProcesso, Date minDataInicio, Date maxDataInicio, Collection<? extends Tarefa> tarefas) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<InstanciaProcesso> retrieveProcessInstancesWith(DefinicaoProcesso definicaoProcesso, MUser creatingUser, Boolean active) {
        throw new UnsupportedOperationException();
    }
}
