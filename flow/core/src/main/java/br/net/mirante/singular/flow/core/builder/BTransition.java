package br.net.mirante.singular.flow.core.builder;

import br.net.mirante.singular.flow.core.MTransition;
import br.net.mirante.singular.flow.core.TaskInstance;
import br.net.mirante.singular.flow.core.TransitionAccessStrategy;

@SuppressWarnings({"rawtypes", "unchecked"})
public interface BTransition<SELF extends BTransition<SELF>> {

    public abstract MTransition getTransition();

    public FlowBuilder getFlowBuilder();

    public default SELF self() {
        return (SELF) this;
    }

    public default SELF thenGo(ITaskDefinition destination) {
        MTransition transition = getTransition().thenGo(destination);
        return (SELF) getFlowBuilder().newTransition(transition);
    }

    // depois de avaliar a assinatura com ITaskDefinition e refatorar Alocpro,
    // apagar esse método
    @Deprecated
    public default SELF thenGo(BTask destination) {
        MTransition transition = getTransition().thenGo(destination.getTask());
        return (SELF) getFlowBuilder().newTransition(transition);
    }

    // depois de avaliar a assinatura com ITaskDefinition e refatorar Alocpro,
    // apagar esse método
    @Deprecated
    public default SELF thenGo(String actionName, BTask destination) {
        MTransition transition = getTransition().thenGo(actionName, destination.getTask());
        return (SELF) getFlowBuilder().newTransition(transition);
    }

    public default SELF withAccessControl(TransitionAccessStrategy<? extends TaskInstance> accessStrategy) {
        getTransition().withAccessControl(accessStrategy);
        return self();
    }

    public default SELF defineUserRoleInTransition(BProcessRole<?> processRole) {
        getTransition().defineUserRoleInTransition(processRole.getProcessRole());
        return self();
    }
}