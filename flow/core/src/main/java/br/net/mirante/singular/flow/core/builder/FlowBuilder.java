package br.net.mirante.singular.flow.core.builder;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.IExecutionDateStrategy;
import br.net.mirante.singular.flow.core.IRoleChangeListener;
import br.net.mirante.singular.flow.core.MProcessRole;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTaskEnd;
import br.net.mirante.singular.flow.core.MTaskJava;
import br.net.mirante.singular.flow.core.MTaskPeople;
import br.net.mirante.singular.flow.core.MTaskWait;
import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.ProcessScheduledJob;
import br.net.mirante.singular.flow.core.RoleAccessStrategy;
import br.net.mirante.singular.flow.core.TaskAccessStrategy;
import br.net.mirante.singular.flow.core.TaskPredicate;
import br.net.mirante.singular.flow.core.UserRoleSettingStrategy;
import br.net.mirante.singular.flow.core.MTaskEnd;
import br.net.mirante.singular.flow.core.MTaskJava;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskPredicate;

public abstract class FlowBuilder<DEF extends ProcessDefinition<?>, MAPA extends FlowMap, BUILDER_JAVA extends BJava<?>,
        BUILDER_PEOPLE extends BPeople<?>, BUILDER_WAIT extends BWait<?>, BUILDER_END extends BEnd<?>, BUILDER_TRANSITION extends BTransition<?>, BUILDER_PAPEL extends BProcessRole<?>> {

    private final MAPA flowMap;

    public FlowBuilder(DEF definicaoProcesso) {
        flowMap = newFlowMap(definicaoProcesso);
    }

    protected abstract MAPA newFlowMap(DEF processDefinition);

    protected abstract BUILDER_JAVA newJavaTask(MTaskJava task);

    protected abstract BUILDER_PEOPLE newPeopleTask(MTaskPeople task);

    protected abstract BUILDER_WAIT newWaitTask(MTaskWait task);

    protected abstract BUILDER_END newEndTask(MTaskEnd task);

    protected abstract BUILDER_TRANSITION newTransition(MTransition transition);

    protected abstract BUILDER_PAPEL newProcessRole(MProcessRole transicao);

    protected final MAPA getFlowMap() {
        return flowMap;
    }

    public MAPA build() {
        return flowMap;
    }

    public void setInicio(BTask inicio) {
        getFlowMap().setInicio(inicio.getTask());
    }

    public <T extends ProcessInstance> void setRoleChangeListener(IRoleChangeListener<T> roleChangeListener) {
        getFlowMap().setRoleChangeListener(roleChangeListener);
    }

    private BTask toBuilder(MTask<?> task) {
        if (task instanceof MTaskPeople) {
            return newPeopleTask((MTaskPeople) task);
        } else if (task instanceof MTaskJava) {
            return newJavaTask((MTaskJava) task);
        } else if (task instanceof MTaskWait) {
            return newWaitTask((MTaskWait) task);
        } else if (task instanceof MTaskEnd) {
            return newEndTask((MTaskEnd) task);
        }
        throw new RuntimeException("Task type " + task.getClass().getName() + " not supported");
    }

    public void forEach(Consumer<BTask> consumer) {
        getFlowMap().getTasks().stream().map(t -> toBuilder(t)).forEach(consumer);
    }

    public BUILDER_PAPEL addRoleDefinition(String description,
            UserRoleSettingStrategy<? extends ProcessInstance> estrategiaDefinicaoPapelPessoa,
            boolean alocarPessoaAutomaticamente) {
        return newProcessRole(getFlowMap().addRoleDefinition(description, estrategiaDefinicaoPapelPessoa, alocarPessoaAutomaticamente));
    }

    public BUILDER_JAVA addJavaTask(String nome) {
        return newJavaTask(getFlowMap().addJavaTask(nome));
    }

    public BUILDER_PEOPLE addPeopleTask(String nome) {
        return newPeopleTask(getFlowMap().addPeopleTask(nome));
    }

    public BUILDER_PEOPLE addPeopleTask(String nome, TaskAccessStrategy<?> estrategiaAcessoTask) {
        BUILDER_PEOPLE task = newPeopleTask(getFlowMap().addPeopleTask(nome));
        if (estrategiaAcessoTask != null) {
            task.addAccessStrategy(estrategiaAcessoTask);
        }
        return task;
    }

    public BUILDER_PEOPLE addPeopleTask(String nome, BProcessRole<?> mPapelExecucao) {
        return addPeopleTask(nome, RoleAccessStrategy.of(mPapelExecucao.getProcessRole()));
    }

    public BUILDER_PEOPLE addPeopleTask(String nome, BProcessRole<?> mPapelExecucao, BProcessRole<?> mPapelVisualizacao) {
        return addPeopleTask(nome, RoleAccessStrategy.of(mPapelExecucao.getProcessRole(), mPapelVisualizacao.getProcessRole()));
    }

    public BUILDER_WAIT addWaitTask(String nome) {
        return newWaitTask(getFlowMap().addWaitTask(nome));
    }

    public <T extends ProcessInstance> BUILDER_WAIT addWaitTask(String nome, IExecutionDateStrategy<T> estrategiaAgendamento) {
        return newWaitTask(getFlowMap().addWaitTask(nome, estrategiaAgendamento));
    }

    public <T extends ProcessInstance> BUILDER_WAIT addWaitTask(String nome, IExecutionDateStrategy<T> estrategiaAgendamento,
            TaskAccessStrategy<?> estrategiaAcessoTask) {
        BUILDER_WAIT wait = addWaitTask(nome, estrategiaAgendamento);
        wait.addAccessStrategy(estrategiaAcessoTask);
        return wait;
    }

    public BUILDER_END addEnd() {
        return addEnd("End");
    }

    public BUILDER_END addEnd(String name) {
        return newEndTask(getFlowMap().addFim(name));
    }

    public BUILDER_TRANSITION addTransition(BTask origin, String actionName, BTask destination, boolean showTransitionInExecution) {
        return newTransition(origin.getTask().addTransition(actionName, destination.getTask(), showTransitionInExecution));
    }

    public BUILDER_TRANSITION addTransition(BTask origin, String actionName, BTask destination) {
        return newTransition(origin.getTask().addTransition(actionName, destination.getTask()));
    }

    public BUILDER_TRANSITION addTransition(BTask origin, BTask destination) {
        return newTransition(origin.getTask().addTransition(destination.getTask()));
    }

    public BUILDER_TRANSITION addAutomaticTransition(BTask origin, TaskPredicate condition, BTask destination) {
        return newTransition(origin.getTask().addAutomaticTransition(condition, destination.getTask()));
    }

    public ProcessScheduledJob addScheduledJob(Supplier<Object> impl, String name) {
        return getFlowMap().addScheduledJob(name).call(impl);
    }

    public ProcessScheduledJob addScheduledJob(Runnable impl, String name) {
        return getFlowMap().addScheduledJob(name).call(impl);
    }

    public void deleteInstancesFinalizedMoreThan(int time, TimeUnit timeUnit) {
        getFlowMap().deleteInstancesFinalizedMoreThan(time, timeUnit);
    }

    public void addTasksVisualizeStrategy(TaskAccessStrategy<?> estrategiaVisualizacao) {
        getFlowMap().getAllTasks().stream().forEach(t -> t.addVisualizeStrategy(estrategiaVisualizacao));
    }

    public void addTasksVisualizeStrategy(TaskAccessStrategy<?> estrategiaVisualizacao, Predicate<MTask<?>> filtroTipoTask) {
        getFlowMap().getAllTasks().stream().filter(filtroTipoTask).forEach(t -> t.addVisualizeStrategy(estrategiaVisualizacao));
    }
}