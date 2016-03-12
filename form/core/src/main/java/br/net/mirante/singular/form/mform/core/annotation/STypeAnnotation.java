package br.net.mirante.singular.form.mform.core.annotation;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.core.SPackageCore;

/**
 * This type represents an Annotation of a field.
 * For now only composite fields can be anotated but this type does not enforce such rule.
 *
 * @author Fabricio Buzeto
 */
@SInfoType(name = "Annotation", spackage = SPackageCore.class)
public class STypeAnnotation extends STypeComposite<SIAnnotation> {

    public static final String          FIELD_TEXT          = "text",
                                        FIELD_TARGET_ID     = "targetId",
                                        FIELD_APPROVED      = "isApproved"
                                        ;

    public STypeAnnotation() {
        super(SIAnnotation.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        addFieldString(FIELD_TEXT);
        addFieldBoolean(FIELD_APPROVED);
        addFieldInteger(FIELD_TARGET_ID);
    }

    @Override
    public <T extends Object> T convert(Object valor, Class<T> classeDestino) {
        return (T) valor;
    }
}
