package br.net.mirante.singular.flow.util.vars;

import br.net.mirante.singular.flow.util.props.Props;

public abstract class AbstractVarInstance implements VarInstance {

    private Props props;

    private final VarDefinition definition;

    private VarInstanceMap<?> changeListener;

    public AbstractVarInstance(VarDefinition definition) {
        this.definition = definition;
    }

    @Override
    public VarDefinition getDefinicao() {
        return definition;
    }

    @Override
    public String getStringDisplay() {
        return getDefinicao().toDisplayString(this);
    }

    @Override
    public String getStringPersistencia() {
        return getDefinicao().toPersistenceString(this);
    }

    @Override
    public Props props() {
        if (props == null) {
            props = new Props();
        }
        return props;
    }

    @Override
    public void setChangeListner(VarInstanceMap<?> changeListener) {
        this.changeListener = changeListener;
    }

    protected final boolean needToNotifyAboutValueChanged() {
        return changeListener != null;
    }

    protected void notifyValueChanged() {
        changeListener.onValueChanged(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " [definicao=" + getRef() + ", codigo=" + getValor() + "]";
    }
}
