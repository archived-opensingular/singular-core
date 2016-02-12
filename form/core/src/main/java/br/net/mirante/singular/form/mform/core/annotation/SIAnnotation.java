package br.net.mirante.singular.form.mform.core.annotation;

import br.net.mirante.singular.form.mform.SIComposite;

/**
 * Instance class form the MTipoAnnotation type.
 *
 * @author Fabricio Buzeto
 */
public class SIAnnotation extends SIComposite {
    public void setText(String text) {
        setValor(STypeAnnotation.FIELD_TEXT, text);
    }
    public String getText() {   return getValorString(STypeAnnotation.FIELD_TEXT);}
    public void setTargetId(Integer id) {
        setValor(STypeAnnotation.FIELD_TARGET_ID, id);
    }
    public Integer getTargetId() {  return getValorInteger(STypeAnnotation.FIELD_TARGET_ID);    }
    public void setApproved(Boolean isApproved) {
        setValor(STypeAnnotation.FIELD_APPROVED, isApproved);
    }
    public Boolean getApproved() {
        return getValorBoolean(STypeAnnotation.FIELD_APPROVED);
    }

    @Override
    public void setValor(Object valor) {
        if(valor instanceof SIAnnotation){
            SIAnnotation other = (SIAnnotation) valor;
            this.setText(other.getText());
            this.setTargetId(other.getTargetId());
            this.setApproved(other.getApproved());
        }
    }

    @Override
    public Object getValor() {
        return this;
    }

    public void clear() {
        setText(null);
        setApproved(null);
    }
}
