package br.net.mirante.singular.view.page.form.crud;

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
}