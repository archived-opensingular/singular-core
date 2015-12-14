package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoString;

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
    public void setValor(Object valor) {
        // TODO Auto-generated method stub
        super.setValor(valor);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null || !(obj instanceof MISelectItem)) return false;
        
        MISelectItem item = (MISelectItem) obj;
        return getFieldId() != null && getFieldId().equals(item.getFieldId());
    }
    
}
