package br.net.mirante.singular.form.wicket.mapper.selection;

import org.apache.wicket.model.IModel;

@SuppressWarnings({"serial", "rawtypes"})
public class SelectOption<T> implements IModel {
    private String key;
    private T value;

    public SelectOption(String key, T value) {
        this.key = key;
        this.value = value;
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
        SelectOption s = (SelectOption) o;
        if(o != null){
            this.setKey(s.getKey());
            this.setValue((T) s.getValue());
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