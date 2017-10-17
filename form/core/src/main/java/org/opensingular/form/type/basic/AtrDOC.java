package org.opensingular.form.type.basic;

import org.opensingular.form.SAttributeEnabled;
import org.opensingular.form.STranslatorForAttribute;
import org.opensingular.form.type.core.SPackageDocumentation;

import java.util.function.Function;

public class AtrDOC extends STranslatorForAttribute {

    public AtrDOC() {
    }

    public AtrDOC(SAttributeEnabled target) {
        super(target);
    }

    public static <A extends SAttributeEnabled> Function<A, AtrDOC> factory() {
        return AtrDOC::new;
    }

    public AtrDOC hiddenForDocumentation() {
        setAttributeValue(SPackageDocumentation.ATR_DOC_HIDDEN, Boolean.TRUE);
        return this;
    }

}
