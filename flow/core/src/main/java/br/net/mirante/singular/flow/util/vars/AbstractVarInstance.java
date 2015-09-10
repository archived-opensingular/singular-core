package br.net.mirante.singular.flow.util.vars;

import br.net.mirante.singular.flow.util.props.MetaData;

public abstract class AbstractVarInstance implements VarInstance {

    private MetaData metaData;

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
    public MetaData getMetaData() {
        if (metaData == null) {
            metaData = new MetaData();
        }
        return metaData;
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
