package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.MIComposto;

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
        return getValorAtributo(MTipoSelectItem.ID_FIELD);
    }

    public void setFieldValue(Object value) {
        setValor(value(), value);
    }
    
    private String value() {
        return getValorAtributo(MTipoSelectItem.VALUE_FIELD);
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
