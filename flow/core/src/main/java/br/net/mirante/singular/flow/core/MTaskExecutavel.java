package br.net.mirante.singular.flow.core;

@SuppressWarnings("unchecked")
public abstract class MTaskExecutavel<K extends MTaskExecutavel<?>> extends MTask<K> {

    private ITaskPageStrategy executionPage;
    private ITaskPageStrategy backPage;
    private ITaskPageStrategy pageAfterTask;
    private IExecutionDateStrategy<? extends ProcessInstance> targetDateExecutionStrategy;

    public MTaskExecutavel(FlowMap flowMap, String name) {
        super(flowMap, name);
    }

    @Override
    public final K addAccessStrategy(TaskAccessStrategy<?> estrategiaAcesso) {
        return (K) super.addAccessStrategy(estrategiaAcesso);
    }

    @Override
    public K addVisualizeStrategy(TaskAccessStrategy<?> estrategiaAcesso) {
        return (K) super.addVisualizeStrategy(estrategiaAcesso);
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    public <T extends ProcessInstance> K withTargetDate(IExecutionDateStrategy<T> targetDateExecutionStrategy) {
        this.targetDateExecutionStrategy = targetDateExecutionStrategy;
        return (K) this;
    }

    public final IExecutionDateStrategy<ProcessInstance> getTargetDateExecutionStrategy() {
        return (IExecutionDateStrategy<ProcessInstance>) targetDateExecutionStrategy;
    }

    @Override
    public K setApareceNoPainelAtividades(Boolean valor) {
        super.setApareceNoPainelAtividades(valor);
        return (K) this;
    }

    public ITaskPageStrategy getBackPage() {
        return backPage;
    }

    public void setBackPage(ITaskPageStrategy backPage) {
        this.backPage = backPage;
    }

    public ITaskPageStrategy getPageAfterTask() {
        return pageAfterTask;
    }

    public void setPageAfterTask(ITaskPageStrategy pageAfterTask) {
        this.pageAfterTask = pageAfterTask;
    }

    public ITaskPageStrategy getExecutionPage() {
        return executionPage;
    }

    public K setExecutionPage(ITaskPageStrategy executionPage) {
        this.executionPage = executionPage;
        return (K) this;
    }

}
