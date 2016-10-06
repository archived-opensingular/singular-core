package org.opensingular.singular.form;

import org.opensingular.singular.form.type.basic.SPackageBasic;

import java.util.Objects;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Classe com implementações padrãos para um objeto de apoio a assertivas, independente do tipo em questão.
 *
 * @author Daniel C. Boridn
 */
public abstract class AssertionsAbstract<T extends SAttributeEnabled, SELF extends AssertionsAbstract<T, SELF>> {


    private final T target;

    public AssertionsAbstract(T target) {
        this.target = target;
    }

    /**
     * Objeto alvo das assertivas.
     */
    public final T getTarget() {
        return target;
    }

    /**
     * Retorna o objeto alvo das assertivas já com cast para o tipo da classe informado ou dá uma exception se o objeto
     * não foi da classe informado. Se for null, também gera exception.
     */
    public final <TT extends T> TT getTarget(Class<TT> expectedClass) {
        if (!expectedClass.isInstance(target)) {
            throw new AssertionError(errorMsg("Não é da classe " + expectedClass.getName(), expectedClass,
                    target == null ? null : target.getClass()));
        }
        return expectedClass.cast(target);
    }


    /**
     * Deve ser implementado de modo a colocar na mensagem de erro, que será disparada na exception, informações
     * adicionais sobre o objeto alvo atual {@link #getTarget()} a fim de ajudar o entendimento do erro.
     */
    protected abstract String errorMsg(String msg);

    protected String errorMsg(String msg, Object expected, Object current) {
        return errorMsg(msg + ":\n Esperado  : " + expected + "\n Encontrado: " + current);
    }

    /**
     * Verifica se o objeto atual é nulo.
     */
    public SELF isNull() {
        if (getTarget() != null) {
            throw new AssertionError(errorMsg("Era essperado ser null."));
        }
        return (SELF) this;
    }


    /**
     * Verifica se o objeto atual não é nulo.
     */
    public SELF isNotNull() {
        if (getTarget() == null) {
            throw new AssertionError("O está null. Esperado não ser null.");
        }
        return (SELF) this;
    }

    /**
     * Verifica se o objeto atual é da classe informada.
     */
    protected SELF is(Class<?> typeClass) {
        assertThat(getTarget()).isInstanceOf(typeClass);
        return (SELF) this;
    }

    /**
     * Verifica se o objeto atual é identico ao valor informado (equivalencia usando '==' ).
     */
    public SELF isSameAs(Object expectedValue) {
        if (getTarget() != expectedValue) {
            throw new SingularFormException(errorMsg("Não é a mesma instância (not the same) de " + expectedValue,
                    expectedValue, getTarget()));
        }
        return (SELF) this;
    }

    /**
     * Verifica se o objeto atual não é identico ao valor informado (equivalencia usando '==' ).
     */
    public SELF isNotSameAs(Object notExpectedValue) {
        if (getTarget() == notExpectedValue) {
            throw new SingularFormException(errorMsg(
                    "Era esperado instância diferentes (the same) de " + notExpectedValue + ", mas é igual",
                    "diferente de '" + notExpectedValue + "'", getTarget()));
        }
        return (SELF) this;
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
        return isAttribute(SPackageBasic.ATR_REQUIRED, true);
    }

    /**
     * Verifica se o atributo required ({@link SPackageBasic#ATR_REQUIRED}) é false.
     */
    public SELF isNotRequired() {
        return isAttribute(SPackageBasic.ATR_REQUIRED, false);
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
