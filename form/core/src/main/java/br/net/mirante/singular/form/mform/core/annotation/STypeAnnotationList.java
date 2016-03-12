package br.net.mirante.singular.form.mform.core.annotation;

import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.core.SPackageCore;

/**
 * This type encloses a MTipoLista of MTipoAnnotation and can be used in order to persist
 * a set of anotations without much hassle.
 *
 * @author Fabricio Buzeto
 */
@SInfoType(name = STypeAnnotationList.NAME, spackage = SPackageCore.class)
public class STypeAnnotationList extends STypeList {

    public static final String NAME = "AnnotationList";

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        setElementsType(tb.getType().getDictionary().getType(STypeAnnotation.class));
    }
}
