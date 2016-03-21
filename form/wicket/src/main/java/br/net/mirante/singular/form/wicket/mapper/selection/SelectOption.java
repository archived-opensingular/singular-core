/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.options.SOptionsConfig;
import br.net.mirante.singular.form.mform.options.SSelectionableInstance;
import br.net.mirante.singular.form.mform.util.transformer.Value;
import org.apache.wicket.model.IModel;

/**
 * This class represents a SelectOption used on DropDowns, Checklists, Radios and etc.
 */
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
        return String.format("SelectOption('%s','%s')", selectLabel, value);
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

    public void copyValueToInstance(SSelectionableInstance target) {
        SInstance source = target.getOptionsConfig().getValueFromKey(String.valueOf(value));
        Value.hydrate((SInstance) target, Value.dehydrate(source));
    }

    public void copyValueToInstance(SSelectionableInstance target, SOptionsConfig provider) {
        SInstance source = provider.getValueFromKey(String.valueOf(value));
        Value.hydrate((SInstance) target, Value.dehydrate(source));
    }
}