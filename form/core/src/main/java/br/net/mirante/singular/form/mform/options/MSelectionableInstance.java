package br.net.mirante.singular.form.mform.options;

public interface MSelectionableInstance {

    public void setFieldId(Object value);

    public String getFieldId();

    public void setFieldValue(Object value);

    public String getFieldValue();

    public void setValor(Object o);

    public void setValue(Object key, Object value);

    public MSelectionableType getMTipo();
}
