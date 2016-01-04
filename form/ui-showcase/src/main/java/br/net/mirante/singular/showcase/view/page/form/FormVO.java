package br.net.mirante.singular.showcase.view.page.form;

import java.io.Serializable;

import org.apache.wicket.model.IModel;

import br.net.mirante.singular.showcase.dao.form.TemplateRepository.TemplateEntry;
import br.net.mirante.singular.form.mform.MTipo;

@SuppressWarnings("serial")
public class FormVO implements Serializable, IModel<String> {
    private String key;
    private String typeName;
    private transient MTipo<?> value;

    public FormVO(String key, MTipo<?> value) {
        this.key = key;
        this.value = value;
        if(value != null) this.typeName = value.getNome();
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)    return true;
        if (obj == null)    return false;
        if (getClass() != obj.getClass())   return false;
        FormVO other = (FormVO) obj;
        if (key == null) {
            if (other.key != null)  return false;
        } else if (!key.equals(other.key))  return false;
        if (typeName == null) {
            if (other.typeName != null) return false;
        } else if (!typeName.equals(other.typeName))    return false;
        return true;
    }
    
    
}