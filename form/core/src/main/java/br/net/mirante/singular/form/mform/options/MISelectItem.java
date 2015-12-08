package br.net.mirante.singular.form.mform.options;

import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;

public class MISelectItem extends MIComposto {
    public MISelectItem() {}
    
    public MISelectItem(Object id, Object value) {
        setFieldId(id);
        setFieldValue(value);
    }

    public void setFieldId(Object value) {
        setValor(MTipoSelectItem.FIELD_ID, value);
    }

    public void setFieldValue(Object value) {
        setValor(MTipoSelectItem.FIELD_VALUE, value);
    }

    public String getFieldId() {
        return getValorString(MTipoSelectItem.FIELD_ID);
    }

    public String getFieldValue() {
        return getValorString(MTipoSelectItem.FIELD_VALUE);
    }

    public static MISelectItem create(Object key, Object value, MDicionario dict){
        MTipoSelectItem tipo = dict.getTipo(MTipoSelectItem.class);
        MISelectItem instance = tipo.novaInstancia();
        instance.setFieldId(key);
        instance.setFieldValue(value);
        return instance;
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
