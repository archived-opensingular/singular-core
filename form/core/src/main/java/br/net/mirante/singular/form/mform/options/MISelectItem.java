package br.net.mirante.singular.form.mform.options;

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

}
