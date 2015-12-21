package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoString;

import java.util.List;

public class MISelectItem extends MIComposto {
    public MISelectItem() {}
    
    public MISelectItem(Object id, Object value) {
        setFieldId(id);
        setFieldValue(value);
    }

    public void setFieldId(Object value) {
        setValor(key(), value);
    }

    private String key() {
        return getAndCheckKeyValueAttributes(MTipoSelectItem.ID_FIELD);
    }

    private String getAndCheckKeyValueAttributes(AtrRef<MTipoString, MIString, String> attr) {
        String value = getValorAtributo(attr);
        if(value == null){
            ((MTipoSelectItem)getMTipo()).configureKeyValueFields();
            value = getValorAtributo(attr);
        }
        return value;
    }

    public void setFieldValue(Object value) {
        setValor(value(), value);
    }
    
    private String value() {
        return getAndCheckKeyValueAttributes(MTipoSelectItem.VALUE_FIELD);
    }

    public String getFieldId() {
        return getValorString(key());
    }

    public String getFieldValue() {
        return getValorString(value());
    }

    public void setValorItem(Object key, Object value) {
        setFieldId(key);
        setFieldValue(value);
    }

    @Override
    public void setValor(Object value) {
        if(value instanceof MISelectItem) { setValue((MISelectItem) value);
        }else if(value instanceof List){    setValue((List) value);
        }else{                              super.setValor(value);
        }
    }

    private void setValue(List<MInstancia> values) {
        this.setValorItem(valueOf(values.get(0)), valueOf(values.get(1)));
    }

    private void setValue(MISelectItem item) {
        this.setValorItem(item.getFieldId(),item.getFieldValue());
    }

    private Object valueOf(MInstancia instance) {
        return instance.getValor();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof MISelectItem)) return false;
        
        MISelectItem item = (MISelectItem) obj;
        return getFieldId() != null && getFieldId().equals(item.getFieldId());
    }

    @Override
    public int hashCode() {
        return getFieldId() != null ? getFieldId().hashCode() : -1 ;
    }
}
