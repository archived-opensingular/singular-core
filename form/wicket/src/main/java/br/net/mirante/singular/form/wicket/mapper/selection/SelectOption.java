package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.util.transformer.Val;
import org.apache.wicket.model.IModel;

/**
 * This class represents a SelectOption used on DropDowns, Chacklists, Radios and etc.
 */
@SuppressWarnings({"serial", "rawtypes"})
public class SelectOption implements IModel {

    private String selectLabel;
    private Object value;

    public SelectOption(String selectLabel, Object value) {
        this.selectLabel = selectLabel;
        this.value = Val.dehydratate(value);
    }

    public Object getValue() {
        return value;
    }

    public Object getValue(MTipo<?> tipo) {
        MInstancia instancia = tipo.novaInstancia();
        Val.hydratate(instancia, value);
        return instancia;
    }


    public String getSelectLabel() {
        if (selectLabel == null) {
            return String.valueOf(value);
        }
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

    public void copyValueToInstance(MInstancia instance) {
        Val.hydratate(instance, value);
    }
}