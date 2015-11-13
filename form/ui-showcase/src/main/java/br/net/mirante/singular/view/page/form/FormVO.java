package br.net.mirante.singular.view.page.form;

import java.io.Serializable;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.dao.form.TemplateRepository.TemplateEntry;
import br.net.mirante.singular.form.mform.MTipo;

@SuppressWarnings("serial")
public class FormVO implements Serializable, IModel<String> {
    private String key;
    private final String typeName;
    private transient MTipo<?> value;

    public FormVO(String key, MTipo<?> value) {
        this.key = key;
        this.value = value;
        this.typeName = value.getNome();
    }

    public FormVO(TemplateEntry t) {
        this(t.getDisplayName(), t.getType());
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTypeName() {
        return typeName;
    }

    public MTipo<?> getType() {
        return value;
    }

    public void setType(MTipo<?> value) {
        this.value = value;
    }

    @Override
    public void detach() {
    }

    @Override
    public String getObject() {
        return getKey();
    }

    @Override
    public void setObject(String o) {
        setKey(o);
    }
}