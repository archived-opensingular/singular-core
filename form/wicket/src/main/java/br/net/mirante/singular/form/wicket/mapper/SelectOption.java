package br.net.mirante.singular.form.wicket.mapper;

import org.apache.wicket.model.IModel;

//TODO: This must be abstracted
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
}