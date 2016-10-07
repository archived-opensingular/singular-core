package org.opensingular.form;

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

    protected String errorMsg(String msg, Object expected, Object current) {
        return errorMsg(msg + ":\n Esperado  : " + expected + "\n Encontrado: " + current);
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
}
