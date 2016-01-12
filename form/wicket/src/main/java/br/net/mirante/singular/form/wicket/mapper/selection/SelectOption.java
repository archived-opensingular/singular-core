package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import org.apache.wicket.model.IModel;

import java.util.Map;

import static org.apache.wicket.util.lang.Generics.newHashMap;

/**
 * This class represents a SelectOption used on DropDowns, Chacklists, Radios and etc.
 *
 * @param <T> Type of value used buy this option
 *
 */
@SuppressWarnings({"serial", "rawtypes"})
public class SelectOption<T> implements IModel {

    private T value;
    private String selectLabel;

    private Map<String, Object> otherFields = newHashMap();

    public SelectOption(String selectLabel, T value) {
        this(selectLabel, value, null);
    }

    public SelectOption(String selectLabel, T value, MInstancia target) {
        this.selectLabel = selectLabel;
        this.value = value;
        if(target instanceof MIComposto){
            MIComposto item = (MIComposto) target;
            for(MInstancia i :item.getAllChildren()){
                otherFields.put(i.getNome(), i.getValor());
            }
        }
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getSelectLabel() {
        return selectLabel;
    }

    public void setSelectLabel(String selectLabel) {
        this.selectLabel = selectLabel;
    }

    public Object getOtherField(String key){
        return otherFields.get(key);
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
        SelectOption<T> s = (SelectOption) o;
        if(o != null){
            this.setValue(s.getValue());
            this.setSelectLabel(s.getSelectLabel());
            this.otherFields.putAll(s.otherFields);
        }
    }
    
    @Override
    public String toString() {
        return String.format("SelectOption('%s','%s')", value, selectLabel);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null || ! (obj instanceof SelectOption)) return false;
        
        boolean eq = true;
        SelectOption op = (SelectOption) obj;
        eq &= value != null && value.equals(op.value);
        
        return eq;
    }
    
    @Override
    public int hashCode() {
        int hash = 1;
        if(value != null) hash += value.hashCode();
        return hash;
    }
}