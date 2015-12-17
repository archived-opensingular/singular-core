package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.MDicionarioResolver;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.io.FormSerializationUtil;
import br.net.mirante.singular.form.mform.io.MDicionarioResolverSerializable;
import org.apache.wicket.model.IModel;

@SuppressWarnings({"serial", "rawtypes"})
public class SelectOption<T> implements IModel {
    private String key;
    private T value;
//    private FormSerializationUtil.FormSerialized target;
    private MInstancia target;

    public SelectOption(String key, T value) {
        this(key, value, null);
    }

    public SelectOption(String key, T value, MInstancia target) {
        this.key = key;
        this.value = value;
//        this.target = null;
//        if(target != null) {this.target = FormSerializationUtil.toSerializedObject(target);}
        this.target = target;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public MInstancia getTarget(){
//        if(target != null) return FormSerializationUtil.toInstance(target);
        return target;
    }

    @Override
    public void detach() {
//        target = null;
    }

    @Override
    public Object getObject() {
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setObject(Object o) {
        SelectOption s = (SelectOption) o;
        if(o != null){
            this.setKey(s.getKey());
            this.setValue((T) s.getValue());
            this.target = s.target;
        }
    }
    
    @Override
    public String toString() {
        return String.format("SelectOption('%s','%s')", key,value);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null || ! (obj instanceof SelectOption)) return false;
        
        boolean eq = true;
        SelectOption op = (SelectOption) obj;
        eq &= key != null && key.equals(op.key);
        
        return eq;
    }
    
    @Override
    public int hashCode() {
        int hash = 1;
        if(key != null) hash += key.hashCode();
        return hash;
    }
}