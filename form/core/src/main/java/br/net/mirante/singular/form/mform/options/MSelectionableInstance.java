package br.net.mirante.singular.form.mform.options;

/**
 * Represents an MInstancia that is of a kind of MSelectionableType.
 * It allows specific types to establish their own strategy for defining their key (id)
 * and value (visible selectLabel) of the instance.
 */
public interface MSelectionableInstance<TIPO_NATIVO> {

    public void setSelectLabel(String selectLabel);

    public String getSelectLabel();

    public void setSelectValue(TIPO_NATIVO o);

    public TIPO_NATIVO getSelectValue();

    default public void setValueSelectLabel(TIPO_NATIVO value, String selectLabel)  {
        setSelectLabel(selectLabel);
        setSelectValue(value);
    }

    public MSelectionableType getMTipo();
}
