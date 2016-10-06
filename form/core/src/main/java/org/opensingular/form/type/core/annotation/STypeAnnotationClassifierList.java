package org.opensingular.form.type.core.annotation;

import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.SInfoType;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeString;

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
