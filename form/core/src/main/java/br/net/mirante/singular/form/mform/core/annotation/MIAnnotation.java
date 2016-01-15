package br.net.mirante.singular.form.mform.core.annotation;

import br.net.mirante.singular.form.mform.MIComposto;

/**
 * Created by nuk on 15/01/16.
 */
public class MIAnnotation extends MIComposto {
    public void setText(String text) {
        setValor(MTipoAnnotation.FIELD_TEXT, text);
    }
    public String getText() {
        return getValorString(MTipoAnnotation.FIELD_TEXT);
    }

    @Override
    public void setValor(Object valor) {
        if(valor instanceof MIAnnotation){
            MIAnnotation other = (MIAnnotation) valor;
            this.setText(other.getText());
        }
    }

    @Override
    public Object getValor() {
        return this;
    }
}
