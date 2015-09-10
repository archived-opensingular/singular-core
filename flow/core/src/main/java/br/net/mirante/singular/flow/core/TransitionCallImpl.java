package br.net.mirante.singular.flow.core;

import br.net.mirante.singular.flow.util.vars.VarInstanceMap;

class TransitionCallImpl implements TransitionCall {

    private final TransitionRef transition;
    private VarInstanceMap<?> vars;

    public TransitionCallImpl(TransitionRef transition) {
        this.transition = transition;
    }

    @Override
    public VarInstanceMap<?> vars() {
        if (vars == null) {
            vars = transition.newTransationParameters();
        }
        return vars;
    }

    @Override
    public void go() {
        transition.getTaskInstance().executeTransition(transition.getTransition().getName(), vars);
    }

}
