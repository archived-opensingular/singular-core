package org.opensingular.form.type.basic;

import org.opensingular.form.SAttributeEnabled;
import org.opensingular.form.STranslatorForAttribute;
import org.opensingular.form.type.core.SPackagePersistence;

import java.util.function.Function;

public class AtrIndex extends STranslatorForAttribute {

    public AtrIndex() {
    }

    public AtrIndex(SAttributeEnabled target) {
        super(target);
    }

    public static <A extends SAttributeEnabled> Function<A, AtrIndex> factory() {
        return AtrIndex::new;
    }

    public AtrIndex persistent(Boolean persistent) {
        setAttributeValue(SPackagePersistence.ATR_PERSISTENT, persistent);
        return this;
    }

    public AtrIndex alias(String alias) {
        setAttributeValue(SPackagePersistence.ATR_ALIAS, alias);
        return this;
    }


    public Boolean isPersistent() {
        return getAttributeValue(SPackagePersistence.ATR_PERSISTENT) == null ? Boolean.FALSE: getAttributeValue(SPackagePersistence.ATR_PERSISTENT);
    }

    public String getAlias() {
        return getAttributeValue(SPackagePersistence.ATR_ALIAS);
    }
}
