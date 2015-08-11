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

    public default SELF thenGoTo(BTask destination) {
        MTransition transition = getTransition().thenGoTo(destination.getTask());
        return (SELF) getFlowBuilder().newTransition(transition);
    }

    public default SELF thenGoTo(String actionName, BTask destination) {
        MTransition transition = getTransition().thenGoTo(actionName, destination.getTask());
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