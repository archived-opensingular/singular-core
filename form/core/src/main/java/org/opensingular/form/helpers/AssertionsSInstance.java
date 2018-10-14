/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.form.helpers;

import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.AbstractDateAssert;
import org.assertj.core.api.IterableAssert;
import org.opensingular.form.ICompositeInstance;
import org.opensingular.form.SAttributeEnabled;
import org.opensingular.form.SAttributeUtil;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.io.FormSerializationUtil;
import org.opensingular.form.validation.ValidationError;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Classe de apoio a a escrita de assertivas referentes a um {@link SInstance}. Dispara {@link AssertionError} se uma
 * assertiva for violada.
 *
 * @author Daniel C.Bordin
 */

public class AssertionsSInstance extends AssertionsSAttributeEnabled<AssertionsSInstance, SInstance> {

    public AssertionsSInstance(SInstance instance) {
        super(instance);
    }

    @Override
    @Nonnull
    protected Optional<String> generateDescriptionForCurrentTarget(@Nonnull Optional<SInstance> current) {
        return current.map(i -> "Na instância '" + i.getName());
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
        if (expectedValue instanceof SInstance) {
            isEquivalentInstance(getTarget(), (SInstance) expectedValue, false, true);
            return this;
        } else {
            return isValueEquals((String) null, expectedValue);
        }
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

    /** Verifies if the SType of the SInstance is exactly of the informed type. */
    @Nonnull
    public AssertionsSInstance isExactTypeOf(@Nonnull SType<?> expectedType) {
        if (getTarget().getType() != expectedType) {
            throw new AssertionError(
                    errorMsg("The SInstance type isn't of the expected type", expectedType, getTarget().getType()));
        }
        return this;
    }

    /** Verifies if the SType of the SInstance is of the informed type. */
    @Nonnull
    public AssertionsSInstance isTypeOf(@Nonnull SType<?> expectedType) {
        if (!getTarget().isTypeOf(expectedType)) {
            throw new AssertionError(
                    errorMsg("The SInstance type isn't of the expected type or extension of the type", expectedType,
                            getTarget().getType()));
        }
        return this;
    }

    /** Verifies if the SType of the SInstance is of the informed type. */
    @Nonnull
    public AssertionsSInstance isTypeOf(@Nonnull Class<? extends SType<?>> expectedType) {
        return isTypeOf(getTarget().getDictionary().getType(expectedType));
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
        return isInstanceOf(SIList.class);
    }

    /**
     * Verifica se a instância atual é um composite ({@link SIComposite}).
     */
    public AssertionsSInstance isComposite() {
        return isInstanceOf(SIComposite.class);
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

    public IterableAssert<ValidationError> assertThatValidationErrors(){
        return assertThat(getTarget().getValidationErrors());
    }

    /**
     * Cria uma nova assertiva para o valor da instância, se a instância contiver um valor Date. Senão o valor for
     * diferente de null e não for Date, então dispara exception.
     */
    public AbstractDateAssert<?> assertDateValue() {
        Object value = getTarget().getValue();
        if (value instanceof Date || value == null) {
            return assertThat((Date) value);
        }
        throw new AssertionError(errorMsg("O Objeto da instancia atual não é do tipo Date"));
    }

    public AbstractCharSequenceAssert<?, String> assertStringValue() {
        Object value = getTarget().getValue();
        if (value instanceof String || value == null) {
            return assertThat((String) value);
        }
        throw new AssertionError(errorMsg("O Objeto da instancia atual não é do tipo String"));
    }

    /** Cria uma nova assertiva a partir do resultado da serialização e deserialização da instância atual. */
    public AssertionsSInstance serializeAndDeserialize() {
        isNotNull();
        AssertionsSInstance a = new AssertionsSInstance(FormSerializationUtil.serializeAndDeserialize(getTarget()));
        a.isNotSameAs(getTarget());
        a.isInstanceOf(getTarget().getClass());
        return a;
    }


    public AssertionsSInstance isEquivalentInstance(@Nonnull SInstance original) {
        isEquivalentInstance(original, getTarget(), true);
        return this;
    }

    private static void isEquivalentInstance(SInstance original, SInstance copy, boolean mustHaveSameId) {
        isEquivalentInstance(original, copy, mustHaveSameId, false);
    }

    private static void isEquivalentInstance(SInstance original, SInstance copy, boolean mustHaveSameId,
            boolean ignoreNullValues) {
        try {
            assertThat(copy).isNotSameAs(original);
            assertThat(copy.getClass()).isEqualTo(original.getClass());
            assertThat(copy.getType().getName()).isEqualTo(original.getType().getName());
            assertThat(copy.getType().getClass()).isEqualTo(original.getType().getClass());
            assertThat(copy.getName()).isEqualTo(original.getName());
            if (mustHaveSameId) {
                assertThat(copy.getId()).isEqualTo(original.getId());
            }
            assertThat(copy.getPathFull()).isEqualTo(original.getPathFull());
            if (original.getParent() != null) {
                assertThat(copy.getParent()).isNotNull();
                assertThat(copy.getParent().getPathFull()).isEqualTo(original.getParent().getPathFull());
            } else {
                assertThat(copy.getParent()).isNull();
            }
            if (original instanceof ICompositeInstance) {
                List<SInstance> originalChildren = new ArrayList<>(((ICompositeInstance) original).getChildren());
                List<SInstance> copyChildren     = new ArrayList<>(((ICompositeInstance) copy).getChildren());

                if (ignoreNullValues) {
                    removeNullChildren(originalChildren);
                    removeNullChildren(copyChildren);
                }

                assertThat(copyChildren.size()).isEqualTo(originalChildren.size());
                for (int i = 0; i < originalChildren.size(); i++) {
                    isEquivalentInstance(originalChildren.get(0), copyChildren.get(0), mustHaveSameId);
                }
            } else {
                assertThat(copy.getValue()).isEqualTo(original.getValue());
            }

            assertThat(copy.isAttribute()).isEqualTo(original.isAttribute());
            if(! original.isAttribute()) {
                assertEqualsAttributes(original, copy);
            }
        } catch (AssertionError e) {
            if (e.getMessage().startsWith("Erro comparando")) {
                throw e;
            }
            throw new AssertionError("Erro comparando '" + original.getPathFull() + "'", e);
        }
    }

    private static void removeNullChildren(List<SInstance> children) {
        children.removeIf(child -> child.getValue() == null);
    }

    public static void assertEqualsAttributes(SAttributeEnabled original, SAttributeEnabled copy) {
        try {
            assertThat(copy.getAttributes().size()).isEqualTo(original.getAttributes().size());

            for (SInstance atrOriginal : original.getAttributes()) {
                assertEqualsAttribute(copy, atrOriginal);
            }
        } catch (AssertionError e) {
            if (e.getMessage().startsWith("Erro comparando atributos de ")) {
                throw e;
            }
            throw new AssertionError("Erro comparando atributos de '" + original + "'", e);
        }
    }

    private static void assertEqualsAttribute(SAttributeEnabled copy, SInstance atrOriginal) {
        Optional<SInstance> atrNewOpt = SAttributeUtil.getAttributeDirectly(copy, atrOriginal.getAttributeInstanceInfo().getName());
        try {
            assertThat(atrNewOpt).isPresent();
            atrNewOpt.ifPresent(sInstance -> isEquivalentInstance(atrOriginal, sInstance, false));
        } catch (AssertionError e) {
            throw new AssertionError(
                    "Erro comparando atributo '" + atrOriginal.getAttributeInstanceInfo().getName() + "'", e);
        }
    }


    /**
     * Verifica todos os filhos da instância em questão possuem o mesmo
     * {@link org.opensingular.form.document.SDocument}.
     */
    public void assertCorrectDocumentReference() {
        assertCorrectDocumentReference(getTarget().root(), getTarget());
    }

    private void assertCorrectDocumentReference(@Nonnull SInstance reference, @Nonnull SInstance target) {
        if (reference.getDocument() != target.getDocument()) {
            throw new AssertionError("Inconsitência Interna: O document da instancia '" + target.getPathFull() +
                    "' não é o mesmo da instância '" + reference.getPathFull() + "'");
        }
        target.forEachChild(child -> assertCorrectDocumentReference(reference, child));
    }

    /** Verifica se não possui nenhuma repetição de IDs entre instancia filhas e depois em todo o documento. */
    public void assertUniqueIDs() {
        assertUniqueIDs(getTarget(), new HashMap<>());
    }

    private void assertUniqueIDs(SInstance target, HashMap<Integer, SInstance> usedIds) {
        Integer id = target.getId();
        SInstance old = usedIds.putIfAbsent(id, target);
        if (old != null) {
            throw new AssertionError(
                    "Incossitência Interna: Duas instância do mesmo documento estão usando o mesmo ID '" + id + "': '" +
                            target.getPathFull() + "' e '" + old.getPathFull() + "'");
        }
        target.forEachChild(child -> assertUniqueIDs(child, usedIds));
    }

    /**
     * Verifica se os filhos apontam corretaente para o pai. Ou seja, se os filhos da instância apontam para a instância
     * como sendo o pai.
     */
    public void assertCorrectParentRelation() {
        assertCorrectParentRelation(getTarget());
    }

    private void assertCorrectParentRelation(SInstance target) {
        for (SInstance child : target.getChildren()) {
            if (target != child.getParent()) {
                throw new AssertionError(
                        "Incossitência Interna: A instância '" + child.getPathFull() + "', filho de '" +
                                target.getPathFull() + "', aponta para um outro pai: '" + child.getParent() + "'");
            }
            assertCorrectParentRelation(child);
        }
    }

    /** Verifica se a estrutura a partir do ponto autal está consistente internamente. */
    public void assertCorrectStructure() {
        assertCorrectDocumentReference();
        assertUniqueIDs();
        assertCorrectParentRelation();
        assertCorrectTypeReferences();
    }

    /** Verifica se as instância filhas apontas para os tipos corretos de acordo com o esperado. */
    private void assertCorrectTypeReferences() {
        assertCorrectTypeReferences(getTarget());
    }

    private void assertCorrectTypeReferences(SInstance target) {
        if (target instanceof SIList) {
            assertCorrectTypeReferences((SIList<?>) target);
        } else if (target instanceof SIComposite) {
            assertCorrectTypeReferences((SIComposite) target);
        }
    }

    private void assertCorrectTypeReferences(SIComposite target) {
        STypeComposite<?> compositeType = target.getType();
        for (SInstance child : target) {
            SType<?> expectedType = compositeType.getField(child.getName());
            if (expectedType == null) {
                throw new AssertionError("Field " + child.getName() + " not found in " + compositeType.getPathFull());
            }
            assertExpectedType(expectedType, child, false);
            assertCorrectTypeReferences(child);
        }
    }

    private void assertCorrectTypeReferences(SIList<?> target) {
        STypeList listType = target.getType();
        SType expectedType = listType.getElementsType();
        for (SInstance child : target) {
            assertExpectedType(expectedType, child, true);
            assertCorrectTypeReferences(child);
        }
    }

    void assertExpectedType(@Nonnull SType expectedType, @Nonnull SInstance child, boolean acceptExtended) {
        if (acceptExtended) {
            if (!child.isTypeOf(expectedType)) {
                throw new AssertionError(
                        "Incossitência Interna: A instância '" + child.getPathFull() + "' aponta para o tipo '" +
                                child.getType() + "' mas era esperado que fosse do tipo (ou extendesse o tipo): '" +
                                expectedType + "'.");
            }
        } else {
            if (expectedType != child.getType()) {
                throw new AssertionError(
                        "Incossitência Interna: A instância '" + child.getPathFull() + "' aponta para o tipo '" +
                                child.getType() + "' mas era esperado que apontasse para o tipo '" + expectedType +
                                "'. Não são a mesma instância de tipo, mesmo se apresentarem o mesmo nome.");
            }
        }
    }
}
