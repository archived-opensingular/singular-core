package br.net.mirante.singular.form.mform.core.annotation;

import br.net.mirante.singular.form.mform.MIComposto;

/**
 * Instance class form the MTipoAnnotation type.
 *
 * @author Fabricio Buzeto
 */
public class MIAnnotation extends MIComposto {
    public void setText(String text) {
        setValor(MTipoAnnotation.FIELD_TEXT, text);
    }
    public String getText() {   return getValorString(MTipoAnnotation.FIELD_TEXT);}
    public void setTargetId(Integer id) {
        setValor(MTipoAnnotation.FIELD_TARGET_ID, id);
    }
    public Integer getTargetId() {  return getValorInteger(MTipoAnnotation.FIELD_TARGET_ID);    }
    public void setApproved(Boolean isApproved) {
        setValor(MTipoAnnotation.FIELD_APPROVED, isApproved);
    }
    public Boolean getApproved() {
        return getValorBoolean(MTipoAnnotation.FIELD_APPROVED);
    }

    @Override
    public void setValor(Object valor) {
        if(valor instanceof MIAnnotation){
            MIAnnotation other = (MIAnnotation) valor;
            this.setText(other.getText());
            this.setTargetId(other.getTargetId());
            this.setApproved(other.getApproved());
        }
    }

    @Override
    public Object getValor() {
        return this;
    }
}
