package br.net.mirante.singular.form.mform.options;

/**
 * Represents an MInstancia that is of a kind of MSelectionableType.
 * It allows specific types to establish their own strategy for defining their key (id)
 * and value (visible description) of the instance.
 */
public interface MSelectionableInstance {

    public void setFieldId(Object value);

    public String getFieldId();

    public void setFieldValue(Object value);

    public String getFieldValue();

    public void setValor(Object o);

    default public void setValue(Object key, Object value)  {
        setFieldId(key);
        setFieldValue(value);
    };

    public MSelectionableType getMTipo();
}
