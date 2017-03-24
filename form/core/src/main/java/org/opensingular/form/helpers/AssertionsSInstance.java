package org.opensingular.form.helpers;

import org.fest.assertions.api.Assertions;
import org.fest.assertions.api.DateAssert;
import org.fest.assertions.api.IterableAssert;
import org.opensingular.form.*;
import org.opensingular.form.io.FormSerializationUtil;
import org.opensingular.form.validation.IValidationError;

import java.util.Date;
import java.util.Objects;

/**
 * Classe de apoio a a escrita de assertivas referentes a um {@link SInstance}. Dispara {@link AssertionError} se uma
 * assertiva for violada.
 *
 * @author Daniel C.Bordin
 */

public class AssertionsSInstance extends AssertionsAbstract<SInstance, AssertionsSInstance> {

    public AssertionsSInstance(SInstance instance) {
        super(instance);
    }

    @Override
    protected String errorMsg(String msg) {
        return "Na instância '" + getTarget().getName() + "': " + msg;
    }

    /**
     * Verifica se o valor da instância atual é null.
     */
    public AssertionsSInstance isValueNull() {
        return isValueEquals((String) null, null);
    }

    /**
     * Verifica se o valor da instância atual é igual ao esperado.
     */
    public AssertionsSInstance isValueEquals(Object expectedValue) {
        return isValueEquals((String) null, expectedValue);
    }

    public AssertionsSInstance isValueEquals(SType<?> field, Object expectedValue) {
        return isValueEquals(field.getNameSimple(), expectedValue);
    }

    /**
     * Verifica se o valor contido no campo do caminho indicado é igual ao esperado. O caminho pode ser null, nesse caso
     * pega o valor da instância atual.
     */
    public AssertionsSInstance isValueEquals(String fieldPath, Object expectedValue) {
        Object currentValue = getValue(fieldPath);
        if (!Objects.equals(expectedValue, currentValue)) {
            if (fieldPath == null) {
                throw new AssertionError(errorMsg("Valor diferente do esperado", expectedValue, currentValue));
            } else {
                throw new AssertionError(
                        errorMsg("Valor diferente do esperado no path '" + fieldPath + '\'', expectedValue,
                                currentValue));
            }
        }
        return this;
    }

    private Object getValue(String fieldPath) {
        if (fieldPath == null) {
            return getTarget().getValue();
        } else if (getTarget() instanceof ICompositeInstance) {
            return ((ICompositeInstance) getTarget()).getValue(fieldPath);
        }
        throw new AssertionError(errorMsg("O tipo da instância não aceita leitura de path '" + fieldPath + "'"));
    }

    /**
     * Verifica se o campo no caminho indicado é uma lista e se contêm a quantidade indicada de elementos. Se o caminho
     * for null, então faz o teste para a instância atual.
     * @return Um novo objeto de assertivas para o campo do path indicado.
     */
    public AssertionsSInstance isList(String fieldPath, int expectedSize) {
        return field(fieldPath).isList(expectedSize);
    }

    /**
     * Verifica se a instancia atual é uma lista e se contêm a quantidade de elementos indicados.
     */
    public AssertionsSInstance isList(int expectedSize) {
        int current = getTarget(SIList.class).size();
        if (expectedSize != current) {
            throw new AssertionError(errorMsg("Tamanho da lista errado", expectedSize, current));
        }
        return this;
    }

    /**
     * Verifica se a instância atual é uma lista ({@link SIList}).
     */
    public AssertionsSInstance isList() {
        return is(SIList.class);
    }

    /**
     * Verifica se a instância atual é um composite ({@link SIComposite}).
     */
    public AssertionsSInstance isComposite() {
        return is(SIComposite.class);
    }

    /**
     * Retorna um novo objeto de assertivas para o campo indicado pelo caminho passado. O novo objeto pode conter uma
     * instância nula.
     */
    public AssertionsSInstance field(String fieldPath) {
        return fieldPath == null ? this : new AssertionsSInstance(getField(fieldPath));
    }

    private SInstance getField(String fieldPath) {
        if (fieldPath == null) {
            return getTarget();
        } else if (getTarget() instanceof ICompositeInstance) {
            return ((ICompositeInstance) getTarget()).getField(fieldPath);
        }
        throw new AssertionError(errorMsg("O tipo da instância não aceita leitura de path '" + fieldPath + "'"));
    }

    /** Verifica se a anotação existe e possui o texto experado. */
    public AssertionsSInstance isAnnotationTextEquals(String expectedText) {
        return isAnnotationTextEquals((String) null, expectedText);
    }

    /** Verifica se a anotação existe e possui o texto experado. */
    public AssertionsSInstance isAnnotationTextEquals(SType<?> field, String expectedText) {
        return isAnnotationTextEquals(field.getNameSimple(), expectedText);
    }

    /**
     * Verifica se a anotação existe e possui o texto experado. Se o caminho for null, então faz o teste para a
     * instância atual.
     */
    public AssertionsSInstance isAnnotationTextEquals(String fieldPath, String expectedText) {
        AssertionsSInstance field = field(fieldPath);
        String currentText = field.getTarget().asAtrAnnotation().text();
        if(! Objects.equals(expectedText, currentText)) {
            throw new AssertionError(field.errorMsg("Texto da anotação incorreto", expectedText, currentText));
        }
        return this;
    }

    public IterableAssert<IValidationError> assertThatValidationErrors(){
        return Assertions.assertThat(getTarget().getValidationErrors());
    }

    /**
     * Cria uma nova assertiva para o valor da instância, se a instância contiver um valor Date. Senão o valor for
     * diferente de null e não for Date, então dispara exception.
     */
    public DateAssert assertDateValue() {
        Object value = getTarget().getValue();
        if (value instanceof Date || value == null) {
            return Assertions.assertThat((Date) value);
        }
        throw new AssertionError(errorMsg("O Objeto da instancia atual não é do tipo Date"));
    }

    /** Cria uma nova assertiva a partir do resultado da serialização e deserialização da instância atual. */
    public AssertionsSInstance serializeAndDeserialize() {
        isNotNull();
        AssertionsSInstance a = new AssertionsSInstance(FormSerializationUtil.serializeAndDeserialize(getTarget()));
        a.isNotSameAs(getTarget());
        a.is(getTarget().getClass());
        return a;
    }
}