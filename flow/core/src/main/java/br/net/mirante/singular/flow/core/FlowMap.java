package br.net.mirante.singular.flow.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import br.net.mirante.singular.flow.util.vars.VarService;

@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class FlowMap implements Serializable {

    private final ProcessDefinition<?> processDefinition;

    private final Map<String, MTask<?>> tasksByName = new HashMap<>();

    private final Map<String, MTask<?>> tasksByAbbreviation = new HashMap<>();

    private final Map<String, MTaskEnd> endTasks = new HashMap<>();

    private final Map<String, MProcessRole> rolesByAbbreviation = new HashMap<>();

    private final Map<String, ProcessScheduledJob> scheduledJobsByName = new HashMap<>();

    private MTask<?> initialTask;

    private InstanceCleanupStrategy instanceCleanupStrategy;

    private IRoleChangeListener roleChangeListener;

    private int qtdTaskVisiveis;

    public FlowMap(ProcessDefinition<?> processDefinition) {
        this.processDefinition = processDefinition;
    }

    /**
     * Ponto de extensão para customizações.
     */
    protected MTransition newTransition(MTask<?> origin, String name, MTask<?> destinarion, boolean userOption) {
        return new MTransition(origin, name, destinarion, userOption);
    }

    public ProcessScheduledJob addScheduledJob(String name) {
        name = StringUtils.trimToNull(name);

        final ProcessScheduledJob scheduledJob = new ProcessScheduledJob(this, name);

        Preconditions.checkArgument(!scheduledJobsByName.containsKey(name), "A Job with name '%s' is already defined.", name);
        scheduledJobsByName.put(name, scheduledJob);
        return scheduledJob;
    }

    public Collection<ProcessScheduledJob> getScheduledJobs() {
        return scheduledJobsByName.values();
    }

    public Collection<MTask<?>> getTasks() {
        return tasksByName.values();
    }

    public Collection<MTask<?>> getAllTasks() {
        return CollectionUtils.union(getTasks(), getEndTasks());
    }

    public Collection<MTaskPeople> getPeopleTasks() {
        return (Collection<MTaskPeople>) getTasks(TaskType.People);
    }

    public Collection<MTaskJava> getJavaTasks() {
        return (Collection<MTaskJava>) getTasks(TaskType.Java);
    }

    public Collection<MTaskWait> getWaitTasks() {
        return (Collection<MTaskWait>) getTasks(TaskType.Wait);
    }

    public Collection<? extends MTask<?>> getTasks(TaskType taskType) {
        final Builder<MTask<?>> builder = ImmutableList.builder();
        for (final MTask mTask : getTasks()) {
            if (mTask.getTaskType() == taskType) {
                builder.add(mTask);
            }
        }
        return builder.build();
    }

    public Collection<MTaskEnd> getEndTasks() {
        return endTasks.values();
    }

    public boolean hasRoleWithAbbreviation(String sigla) {
        return rolesByAbbreviation.containsKey(sigla);
    }

    public MProcessRole getRoleWithAbbreviation(String abbreviation) {
        return rolesByAbbreviation.get(abbreviation);
    }

    public Collection<MProcessRole> getRoles() {
        return ImmutableSet.copyOf(rolesByAbbreviation.values());
    }

    public MProcessRole addRoleDefinition(String name, UserRoleSettingStrategy<? extends ProcessInstance> userRoleSettingStrategy,
            boolean automaticUserAllocation) {
        final MProcessRole processRole = new MProcessRole(name, userRoleSettingStrategy, automaticUserAllocation);
        if (hasRoleWithAbbreviation(processRole.getAbbreviation())) {
            throw createError("Role with abbreviation '" + processRole.getAbbreviation() + "' already defined");
        }
        rolesByAbbreviation.put(processRole.getAbbreviation(), processRole);
        return processRole;
    }

    public <T extends ProcessInstance> FlowMap setRoleChangeListener(IRoleChangeListener<T> roleChangeListener) {
        this.roleChangeListener = roleChangeListener;
        return this;
    }

    public void notifyRoleChange(final ProcessInstance instance, final MProcessRole role, MUser previousUser, MUser newUser) {
        if (roleChangeListener != null) {
            roleChangeListener.execute(instance, role, previousUser, newUser);
        }
    }

    private <T extends MTask> T addTask(T task) {
        if (tasksByName.containsKey(task.getName())) {
            throw createError("Task with name '" + task.getName() + "' already defined");
        }
        tasksByName.put(task.getName(), task);
        if (tasksByAbbreviation.containsKey(task.getAbbreviation())) {
            throw createError("Task with abbreviation '" + task.getAbbreviation() + "' already defined");
        }
        tasksByAbbreviation.put(task.getAbbreviation(), task);
        return task;
    }

    public MTaskPeople addPeopleTask(String nome) {
        MTaskPeople task = addTask(new MTaskPeople(this, nome));
        qtdTaskVisiveis++;
        return task;
    }

    public MTaskJava addJavaTask(String nome) {
        return addTask(new MTaskJava(this, nome));
    }

    public MTaskWait addWaitTask(String nome) {
        return addWaitTask(nome, (IExecutionDateStrategy<ProcessInstance>) null);
    }

    public <T extends ProcessInstance> MTaskWait addWaitTask(String nome, IExecutionDateStrategy<T> dateExecutionStrategy) {
        return addTask(new MTaskWait(this, nome, dateExecutionStrategy));
    }

    public MTask<?> setInicio(MTask<?> task) {
        Preconditions.checkNotNull(task);
        if (task.getFlowMap() != this) {
            throw createError("Essa task não pertence ao mapa");
        }
        initialTask = task;
        return task;
    }

    public boolean possuiMaisDeUmaTarefaVisivel() {
        return (qtdTaskVisiveis > 1);
    }

    public MTask<?> getTaskInicial() {
        Preconditions.checkNotNull(initialTask);
        return initialTask;
    }

    public ProcessDefinition<?> getDefinicaoProcesso() {
        return processDefinition;
    }

    final MTaskEnd getOrCreateTaskFim() {
        switch (endTasks.size()) {
            case 0:
                return addFim();
            case 1:
                return endTasks.values().iterator().next();
            default:
                throw createError("Existe mais de uma task (nó) de fim. Selecione um explicitamten usando outro método.");
        }
    }

    public MTaskEnd addFim() {
        return addFim("Fim");
    }

    public MTaskEnd addFim(String nome) {
        Preconditions.checkNotNull(nome);
        if (endTasks.containsKey(nome)) {
            throw createError("Já existe um ponto de fim com o nome '" + nome + "'");
        }
        final MTaskEnd fim = new MTaskEnd(this, nome);
        endTasks.put(nome, fim);
        tasksByAbbreviation.put(fim.getAbbreviation(), fim);
        return fim;
    }

    public MTask<?> getTaskWithSigla(String sigla) {
        return tasksByAbbreviation.get(MBPMUtil.convertToJavaIdentity(sigla));
    }

    public MTaskPeople getTaskPeopleWithSigla(String sigla) {
        return MTaskPeople.class.cast(getTaskWithSigla(sigla));
    }

    public MTask<?> getTaskWithNome(String nome) {
        if (tasksByName.containsKey(nome)) {
            return tasksByName.get(nome);
        }
        return endTasks.get(nome);
    }

    public void validarConsistencia() {
        verificarConsitenciaTasks();
        if (initialTask == null) {
            throw new RuntimeException("Não foi definida a task de inicio");
        }
        // verificarSeTodosAsTaskSaoAtingidas();
        verificarSetTodasAsTaskPossuemCaminhoParaFim();

    }

    private void verificarConsitenciaTasks() {
        for (final MTask task : tasksByAbbreviation.values()) {
            task.verifyConsistency();
        }
    }

    private void verificarSetTodasAsTaskPossuemCaminhoParaFim() {
        final Set<MTask<?>> tasks = new HashSet<>(tasksByName.values());
        while (removerTasksQueAtingemFim(tasks)) {
        }
        if (!tasks.isEmpty()) {
            throw createError("As seguintes task não possuem caminho para atingir fim: " + listarNomes(tasks));
        }
    }

    private static boolean removerTasksQueAtingemFim(Set<MTask<?>> tasks) {
        boolean removeuPeloMenosUm = false;
        for (final MTask<?> task : new ArrayList<>(tasks)) {
            for (final MTransition trans : task.getTransicoes()) {
                if (trans.getDestination().isFim() || !tasks.contains(trans.getDestination())) {
                    tasks.remove(task);
                    removeuPeloMenosUm = true;
                    break;
                }
            }
        }
        return removeuPeloMenosUm;
    }

    private static String listarNomes(Set<MTask<?>> tasks) {
        return tasks.stream().map(MTask::getName).collect(Collectors.joining(", "));
    }

    //    private void verificarSeTodosAsTaskSaoAtingidas() {
    //        final Set<MTask> tasks = new HashSet<>(tasksPorNome.values());
    //        // tasks.addAll(tasksFim.values());
    //        percorrerRemovendoDaLista(inicio, tasks);
    //        if (!tasks.isEmpty()) {
    //            throw createErro("As seguintes task nunca são atingidas no fluxo: " + listarNomes(tasks));
    //        }
    //    }
    //
    //    private void percorrerRemovendoDaLista(MTask task, Set<MTask> tasks) {
    //        if (!tasks.remove(task)) {
    //            return; // Já foi percorrido
    //        }
    //        for (final MTransicao trans : task.getTransicoes()) {
    //            percorrerRemovendoDaLista(trans.getDestino(), tasks);
    //        }
    //    }

    final RuntimeException createError(String msg) {
        return new RuntimeException(getDefinicaoProcesso() + " -> " + msg);
    }

    public InstanceCleanupStrategy deleteInstancesFinalizedMoreThan(int time, TimeUnit timeUnit) {
        Preconditions.checkArgument(instanceCleanupStrategy == null, "Instance cleanup strategy already set");
        instanceCleanupStrategy = new InstanceCleanupStrategy(this, time, timeUnit);
        return instanceCleanupStrategy;
    }

    public InstanceCleanupStrategy getCleanupStrategy() {
        return instanceCleanupStrategy;
    }

    protected VarService getVarService() {
        return processDefinition.getVarService();
    }
}
