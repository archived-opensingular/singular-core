package br.net.mirante.singular.form.mform.core.annotation;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.core.SPackageCore;

/**
 * This type represents an Annotation of a field.
 * For now only composite fields can be anotated but this type does not enforce such rule.
 *
 * @author Fabricio Buzeto
 */
@MInfoTipo(nome = "Annotation", pacote = SPackageCore.class)
public class STypeAnnotation extends STypeComposite<SIAnnotation> {

    public static final String          FIELD_TEXT          = "text",
                                        FIELD_TARGET_ID     = "targetId",
                                        FIELD_APPROVED      = "isApproved"
                                        ;

    public STypeAnnotation() {
        super(SIAnnotation.class);
    }

    @Override
    protected void onLoadType(TipoBuilder tb) {
        super.onLoadType(tb);

        addCampoString(FIELD_TEXT);
        addCampoBoolean(FIELD_APPROVED);
        addCampoInteger(FIELD_TARGET_ID);
    }

    @Override
    public <T extends Object> T converter(Object valor, Class<T> classeDestino) {
        return (T) valor;
    }
}
