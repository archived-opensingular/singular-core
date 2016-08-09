package br.net.mirante.singular.form.type.core.annotation;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.type.core.SIString;
import br.net.mirante.singular.form.type.core.STypeString;

/**
 * Classificador da anotação é utilizado para classificar os diferentes tipos de anotações que podem
 * estar presentes em um mesmo STYPE
 * @author Vinicius Uriel
 */
@SInfoType(name = "AnnotationClassifierList", spackage = SPackageBasic.class)
public class STypeAnnotationClassifierList extends STypeList<STypeString, SIString> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        setElementsType(getDictionary().getType(STypeString.class));
    }
}
