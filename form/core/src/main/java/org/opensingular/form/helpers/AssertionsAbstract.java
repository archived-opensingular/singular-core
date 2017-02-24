package org.opensingular.form.helpers;

import org.opensingular.form.AtrRef;
import org.opensingular.form.SAttributeEnabled;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.lib.commons.test.AssertionsBase;

import java.util.Objects;

/**
 * Classe com implementações padrãos para um objeto de apoio a assertivas, independente do tipo em questão.
 *
 * @author Daniel C. Boridn
 */
public abstract class AssertionsAbstract<T extends SAttributeEnabled, SELF extends AssertionsAbstract<T, SELF>>
        extends AssertionsBase<T, SELF> {


    public AssertionsAbstract(T target) {
        super(target);
    }

    /**
     * Verifica se o objeto atual têm um atributo equals() ao valor esperado.
     */
    public SELF isAttribute(AtrRef<?, ?, ?> attr, Object expected) {
        Object actual = getTarget().getAttributeValue(attr);
        if (!Objects.equals(actual, expected)) {
            throw new AssertionError(errorMsg("Valor não esperado no atributo '" + attr.getNameFull(), expected,
                    actual));
        }
        return (SELF) this;
    }

    /**
     * Verifica se o atributo required ({@link SPackageBasic#ATR_REQUIRED}) é true.
     */
    public SELF isRequired() {
        return isAttribute(SPackageBasic.ATR_REQUIRED, Boolean.TRUE);
    }

    /**
     * Verifica se o atributo required ({@link SPackageBasic#ATR_REQUIRED}) é false.
     */
    public SELF isNotRequired() {
        return isAttribute(SPackageBasic.ATR_REQUIRED, Boolean.FALSE);
    }

    /**
     * Verifica se o atributo label ({@link SPackageBasic#ATR_LABEL}) é equals() ao valor esperado.
     */
    public SELF isAttrLabel(String expectedLabel) {
        return isAttribute(SPackageBasic.ATR_LABEL, expectedLabel);
    }

    /**
     * Verifica se o atributo subTitle ({@link SPackageBasic#ATR_SUBTITLE}) é equals() ao valor esperado.
     */
    public SELF isAttrSubTitle(String expectedLabel) {
        return isAttribute(SPackageBasic.ATR_SUBTITLE, expectedLabel);
    }

}
