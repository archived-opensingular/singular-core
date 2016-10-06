package org.opensingular.singular.form;

import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Apoio a escrita de asserções referente a um {@link SType} e seu conteúdo.
 *
 * @author Daniel C. Bordin
 */
public class AssertionsSType extends AssertionsAbstract<SType, AssertionsSType> {

    AssertionsSType(SType<?> type) {
        super(type);
    }

    /**
     * Retorna um novo objeto de assertiva para o tipo indicado pelo caminho informado.
     */
    public AssertionsSType field(String fieldPath) {
        is(STypeComposite.class);
        return new AssertionsSType(getTarget().getLocalType(fieldPath));
    }

    /**
     * Verifica se o tipo é uma extensão direta do tipo informado. Para ser considerado uma extensão direta, deverá ser
     * da mesma classe do tipo (não pode ser derivado) e seu super tipo {@link SType#getSuperType()} deve ser igual o
     * tipo registrado no dicionário.
     */
    public AssertionsSType isDirectExtensionOf(Class<? extends SType<?>> typeClass) {
        return isDirectExtensionOf(typeClass, null);
    }

    /**
     * Verifica se o tipo encontrado no caminho indicado é uma extensão direta do tipo informado. Para ser considerado
     * uma extensão direta, deverá ser da mesma classe do tipo (não pode ser derivado) e seu super tipo {@link
     * SType#getSuperType()} deve ser igual o tipo registrado no dicionário.
     */
    public AssertionsSType isDirectExtensionOf(Class<? extends SType<?>> typeClass, String fieldPath) {
        SType<?> expectedSuperType = getTarget().getDictionary().getType(typeClass);
        if (fieldPath != null) {
            expectedSuperType = expectedSuperType.getLocalType(fieldPath);
        }
        return isDirectExtensionOf(expectedSuperType);
    }

    /**
     * Verifica se o tipo é uma extensão direta do tipo informado. Para ser considerado uma extensão direta, deverá ser
     * da mesma classe do tipo (não pode ser derivado) e seu super tipo {@link SType#getSuperType()} deve ser igual ao
     * tipo passado como parâmetro.
     */
    public AssertionsSType isDirectExtensionOf(SType<?> expectedSuperType) {
        if (getTarget() == expectedSuperType) {
            throw new AssertionError(errorMsg(
                    "Falha em extender (são iguais):\n Esperado  : que extendese " + expectedSuperType +
                            "\n Encontrado: a mesma referência encontrada diretamente no dicionário"));
        } else if (getTarget().getSuperType() != expectedSuperType) {
            throw new AssertionError(errorMsg("Super tipo inválido:\nEsperado  : " + expectedSuperType +
                    "\nEncontrado: " + getTarget().getSuperType()));

        }
        return this;
    }

    /**
     * Veririca se o super tipo do composite pai do campo atual possui um campo de mesmo nome que é o super tipo do
     * campo atual, ou seja, verifica se o campo atual é uma extensão correta de um tipo que pertencia ao composite
     * original.
     */
    public AssertionsSType isExtensionOfParentCompositeFieldReference() {
        assertThat(getTarget().getParentScope()).isInstanceOf(STypeComposite.class);
        STypeComposite parent = (STypeComposite) getTarget().getParentScope();
        new AssertionsSType(parent.getSuperType()).is(parent.getClass());
        STypeComposite parent2 = (STypeComposite) parent.getSuperType();
        SType<?> parentRef = parent2.getField(getTarget().getNameSimple());
        return isDirectExtensionOf(parentRef);
    }

    private AssertionsSType is(Class<?> typeString, String fieldPath) {
        AssertionsSType assertions = this;
        if (fieldPath != null) {
            assertions = field(fieldPath);
        }
        return assertions.is(typeString);
    }

    /**
     * Verifica se o tipo atual é um {@link STypeComposite}.
     */
    public AssertionsSType isComposite() {
        return is(STypeComposite.class);
    }

    /**
     * Verifica se o tipo indicado pelo caminho informado é um {@link STypeComposite} e com a quantidade de campo
     * indicados.
     *
     * @return Retorna um novo objeto de assertiva para o tipo encontrado.
     */
    public AssertionsSType isComposite(String fieldPath, int expectedFieldsSize) {
        return isComposite(fieldPath).sizeIs(expectedFieldsSize);
    }

    /**
     * Verifica se o tipo indicado pelo caminho informado é um {@link STypeComposite}.
     *
     * @return Retorna um novo objeto de assertiva para o tipo encontrado.
     */
    private AssertionsSType isComposite(String fieldPath) {
        return is(STypeComposite.class, fieldPath);
    }

    private AssertionsSType sizeIs(int expectedFieldsSize) {
        compositeSize(expectedFieldsSize);
        return this;
    }

    /**
     * Verifica se o tipo indicado é um {@link STypeComposite} e com a quantidade de campo indicados.
     */
    public AssertionsSType isComposite(int expectedSize) {
        isComposite();
        compositeSize(expectedSize);
        return this;
    }

    private void compositeSize(int expectedSize) {
        assertThat(((STypeComposite) getTarget()).getFields().size()).isEqualTo(expectedSize);
    }

    /**
     * Verifica se o tipo indicado pelo caminho informado é um {@link STypeList}.
     *
     * @return Retorna um novo objeto de assertiva para o tipo encontrado.
     */
    public AssertionsSType isList(String fieldPath) {
        return is(STypeList.class, fieldPath);
    }

    /**
     * Verifica se o tipo atual é uma {@link STypeList} e retorna um novo objketo de assertiva para o tipo de elementos
     * da lista.
     */
    public AssertionsSType listElementType() {
        return listElementType(null);
    }

    /**
     * Verifica se o tipo encontrado no caminho indicado é uma {@link STypeList} e retorna um novo objeto de assertiva
     * para o tipo de elementos da lista.
     */
    public AssertionsSType listElementType(String fieldPath) {
        AssertionsSType field = isList(fieldPath);
        SType<?> typeList = ((STypeList<?, ?>) field.getTarget()).getElementsType();
        if (typeList == null) {
            throw new AssertionError(errorMsg(
                    "era esperado que a lista tivesse um tipo, mas getElementsType() retornou null", "'Valor não nulo'",
                    null));
        }
        return new AssertionsSType(typeList);
    }

    /**
     * Verifica se o tipo atual não é um referência recursica {@link SType#isRecursiveReference()}.
     */
    public AssertionsSType isNotRecursiveReference() {
        if (getTarget().isRecursiveReference()) {
            throw new AssertionError(errorMsg("é uma referência recursiva"));
        }
        return this;
    }

    /**
     * Verifica se o tipo atual é um referência recursica {@link SType#isRecursiveReference()}.
     */
    public AssertionsSType isRecursiveReference() {
        if (!getTarget().isRecursiveReference()) {
            throw new AssertionError(errorMsg("não é uma referência recursiva"));
        }
        return this;
    }

    /**
     * Verifica se o tipo atual é um {@link STypeString}.
     */
    public AssertionsSType isString() {
        return is(STypeString.class);
    }

    /**
     * Verifica se o tipo indicado pelo caminho informado é um {@link STypeString}.
     *
     * @return Retorna um novo objeto de assertiva para o tipo encontrado.
     */
    public AssertionsSType isString(String fieldPath) {
        return is(STypeString.class, fieldPath);
    }

    /**
     * Verifica se o tipo indicado pelo caminho informado é um {@link STypeInteger}.
     *
     * @return Retorna um novo objeto de assertiva para o tipo encontrado.
     */
    public AssertionsSType isInteger(String fieldPath) {
        return is(STypeInteger.class, fieldPath);
    }

    /**
     * Verifica se o tipo indicado pelo caminho informado é um {@link STypeDecimal}.
     *
     * @return Retorna um novo objeto de assertiva para o tipo encontrado.
     */
    public AssertionsSType isDecimal(String fieldPath) {
        return is(STypeDecimal.class, fieldPath);
    }

    /**
     * Verifica se o tipo atual tem todos os tipos informados como campos dependentes e mais nenhum campo.
     */
    public AssertionsSType dependentsTypesAre(SType<?>... types) {
        Set<SType<?>> expectedSet = new LinkedHashSet<>(Arrays.asList(types));
        Set<SType<?>> currentSet = getTarget().getDependentTypes();
        isDependentType(types);
        for (SType<?> type : types) {
            if (!currentSet.contains(type)) {
                throw new AssertionError(errorMsg("A lista de dependente de " + getTarget() + " não contêm " + type));
            }
        }
        for (SType<?> type : currentSet) {
            if (!expectedSet.contains(type)) {
                throw new AssertionError(errorMsg(
                        "O tipo " + type + " foi encontrado como dependente de " + getTarget() +
                                ", mas isso não era esperado"));
            }
        }
        return this;
    }

    /** Verifica se o tipo atual tem todos os tipos informados como campos dependentes. */
    public AssertionsSType isDependentType(SType<?>... types) {
        for (SType<?> type : types) {
            if (!getTarget().isDependentType(type)) {
                throw new AssertionError(errorMsg("O tipo " + type + " não está como dependente de " + getTarget() +
                        " ( isDependentType(type) retornou false)"));
            }
        }
        return this;
    }

    /** Verifica se o tipo atual não possui nenhum dos tipos informados como campos dependentes. */
    public AssertionsSType isNotDependentType(SType<?>... types) {
        for (SType<?> type : types) {
            if (getTarget().isDependentType(type)) {
                throw new AssertionError(errorMsg("O tipo " + type + " está como dependente de " + getTarget() +
                        " ( isDependentType(type) retornou true)"));
            }
        }
        return this;
    }

    @Override
    protected String errorMsg(String msg) {
        return "No tipo '" + getTarget().getName() + "': " + msg;
    }
}
