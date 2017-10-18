/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.helpers;

import org.fest.assertions.api.Assertions;
import org.fest.assertions.api.DateAssert;
import org.fest.assertions.api.IterableAssert;
import org.fest.assertions.api.StringAssert;
import org.opensingular.form.ICompositeInstance;
import org.opensingular.form.SAttributeEnabled;
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
        if (expectedValue instanceof SInstance) {
            assertEquivalentInstance(getTarget(), (SInstance) expectedValue, false, true);
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

    public IterableAssert<ValidationError> assertThatValidationErrors(){
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

    public StringAssert assertStringValue() {
        Object value = getTarget().getValue();
        if (value instanceof String || value == null) {
            return Assertions.assertThat((String) value);
        }
        throw new AssertionError(errorMsg("O Objeto da instancia atual não é do tipo String"));
    }

    /** Cria uma nova assertiva a partir do resultado da serialização e deserialização da instância atual. */
    public AssertionsSInstance serializeAndDeserialize() {
        isNotNull();
        AssertionsSInstance a = new AssertionsSInstance(FormSerializationUtil.serializeAndDeserialize(getTarget()));
        a.isNotSameAs(getTarget());
        a.is(getTarget().getClass());
        return a;
    }


    public static void assertEquivalentInstance(SInstance original, SInstance copy) {
        assertEquivalentInstance(original, copy, true);
    }

    public static void assertEquivalentInstance(SInstance original, SInstance copy, boolean mustHaveSameId) {
        assertEquivalentInstance(original, copy, mustHaveSameId, false);
    }

    public static void assertEquivalentInstance(SInstance original, SInstance copy, boolean mustHaveSameId, boolean ignoreNullValues) {
        try {
            assertNotSame(original, copy);
            assertEquals(original.getClass(), copy.getClass());
            assertEquals(original.getType().getName(), copy.getType().getName());
            assertEquals(original.getType().getClass(), copy.getType().getClass());
            assertEquals(original.getName(), copy.getName());
            if (mustHaveSameId) {
                assertEquals(original.getId(), copy.getId());
            }
            assertEquals(original.getPathFull(), copy.getPathFull());
            if (original.getParent() != null) {
                assertNotNull(copy.getParent());
                assertEquals(original.getParent().getPathFull(), copy.getParent().getPathFull());
            } else {
                assertNull(copy.getParent());
            }
            if (original instanceof ICompositeInstance) {
                List<SInstance> originalChildren = new ArrayList<>(((ICompositeInstance) original).getChildren());
                List<SInstance> copyChildren     = new ArrayList<>(((ICompositeInstance) copy).getChildren());

                if (ignoreNullValues) {
                    removeNullChildren(originalChildren);
                    removeNullChildren(copyChildren);
                }

                assertEquals(originalChildren.size(), copyChildren.size());
                for (int i = 0; i < originalChildren.size(); i++) {
                    assertEquivalentInstance(originalChildren.get(0), copyChildren.get(0), mustHaveSameId);
                }
            } else {
                assertEquals(original.getValue(), copy.getValue());
            }

            assertEquals(original.isAttribute(), copy.isAttribute());
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

    public static void removeNullChildren(List<SInstance> children) {
        children.removeIf(child -> child.getValue() == null);
    }

    public static void assertEqualsAttributes(SAttributeEnabled original, SAttributeEnabled copy) {
        try {
            assertEquals(original.getAttributes().size(), copy.getAttributes().size());

            for (SInstance atrOriginal : original.getAttributes()) {
                assertEqualsAtribute(copy, atrOriginal);
            }
        } catch (AssertionError e) {
            if (e.getMessage().startsWith("Erro comparando atributos de ")) {
                throw e;
            }
            throw new AssertionError("Erro comparando atributos de '" + original + "'", e);
        }
    }

    public static void assertEqualsAtribute(SAttributeEnabled copy, SInstance atrOriginal) {
        Optional<SInstance> atrNovoOpt = copy.getAttributeDirectly(atrOriginal.getAttributeInstanceInfo().getName());
        try {
            if (atrNovoOpt.isPresent()) {
                SInstance atrNovo = atrNovoOpt.get();
                assertNotNull(atrNovo);
                assertEquivalentInstance(atrOriginal, atrNovo, false);
            } else {
                fail();
            }
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
        assertCorrectDocumentReference(getTarget().getDocumentRoot(), getTarget());
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
        assertUniqueIDs(getTarget(), new HashMap<Integer, SInstance>());
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
    public void assertCorrectHierachyStructure() {
        assertCorrectDocumentReference();
        assertUniqueIDs();
        assertCorrectParentRelation();
        assertCorrectTypeReferences();

    }

    /** Verifica se as instância filhas apontas para os tipos corretos de acordo com o esperado. */
    public void assertCorrectTypeReferences() {
        assertCorrectTypeReferences(getTarget());
    }

    public void assertCorrectTypeReferences(SInstance target) {
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
            assertExpectedType(expectedType, child);
            assertCorrectTypeReferences(child);
        }
    }

    private void assertCorrectTypeReferences(SIList<?> target) {
        STypeList listType = target.getType();
        SType expectedType = listType.getElementsType();
        for (SInstance child : target) {
            assertExpectedType(expectedType, child);
            assertCorrectTypeReferences(child);
        }
    }

    private void assertExpectedType(SType expectedType, SInstance child) {
        if (expectedType != child.getType()) {
            throw new AssertionError(
                    "Incossitência Interna: A instância '" + child.getPathFull() + "' aponta para o tipo '" +
                            child.getType() + "' mas era esperado que apontasse para o tipo '" + expectedType +
                            "'. Não são a mesma instância de tipo, mesmo se apresentarem o mesmo nome.");
        }
    }
}
