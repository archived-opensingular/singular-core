package org.opensingular.form.type.basic;

import org.opensingular.form.SAttributeEnabled;
import org.opensingular.form.STranslatorForAttribute;
import org.opensingular.form.type.core.SPackagePersistence;

import java.util.function.Function;

public class AtrPersistence  extends STranslatorForAttribute {

    public AtrPersistence() {
    }

    public AtrPersistence(SAttributeEnabled alvo) {
        super(alvo);
    }

    public static <A extends SAttributeEnabled> Function<A, AtrPersistence> factory() {
        return AtrPersistence::new;
    }

    public AtrPersistence persistent(Boolean persistent) {
        setAttributeValue(SPackagePersistence.ATR_PERSISTENT, persistent);
        return this;
    }

    public AtrPersistence alias(String alias) {
        setAttributeValue(SPackagePersistence.ATR_ALIAS, alias);
        return this;
    }


    public Boolean isPersistent() {
        Boolean attributeValue = getAttributeValue(SPackagePersistence.ATR_PERSISTENT) == null ? false : getAttributeValue(SPackagePersistence.ATR_PERSISTENT);
        return attributeValue;
    }

    public String getAlias() {
        return getAttributeValue(SPackagePersistence.ATR_ALIAS);
    }
}
