package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.options.MOptionsConfig;
import br.net.mirante.singular.form.mform.options.MSelectionableInstance;
import br.net.mirante.singular.form.mform.util.transformer.Val;
import org.apache.wicket.model.IModel;

/**
 * This class represents a SelectOption used on DropDowns, Checklists, Radios and etc.
 */
@SuppressWarnings({"serial", "rawtypes"})
public class SelectOption implements IModel {

    private String selectLabel;
    private Object value;

    public SelectOption(String selectLabel, Object value) {
        this.selectLabel = selectLabel;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public String getSelectLabel() {
        return selectLabel;
    }

    @Override
    public void detach() {
    }

    @Override
    public Object getObject() {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setObject(Object o) {
        if (o instanceof SelectOption) {
            this.value = ((SelectOption) o).getValue();
            this.selectLabel = ((SelectOption) o).getSelectLabel();
        } else {
            throw new SingularFormException("Não implementado");
        }
    }

    @Override
    public String toString() {
        return String.format("SelectOption('%s','%s')", value, selectLabel);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof SelectOption)) return false;

        boolean eq = true;
        SelectOption op = (SelectOption) obj;
        eq &= value != null && value.equals(op.value);

        return eq;
    }

    @Override
    public int hashCode() {
        int hash = 1;
        if (value != null) hash += value.hashCode();
        return hash;
    }

    public void copyValueToInstance(MSelectionableInstance target) {
        MInstancia source = target.getOptionsConfig().getValueFromKey(String.valueOf(value));
        Val.hydrate((MInstancia) target, Val.dehydrate(source));
    }

    public void copyValueToInstance(MSelectionableInstance target, MOptionsConfig provider) {
        MInstancia source = provider.getValueFromKey(String.valueOf(value));
        Val.hydrate((MInstancia) target, Val.dehydrate(source));
    }
}