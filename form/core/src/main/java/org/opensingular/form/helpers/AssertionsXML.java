/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

import org.opensingular.internal.lib.commons.xml.MElement;
import org.opensingular.internal.lib.commons.xml.MParser;
import org.opensingular.lib.commons.test.AssertionsBase;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Classe de apoio a a escrita de assertivas referentes a um XML ({@link MElement} ou {@link Element}). Dispara
 * {@link AssertionError} se uma assertiva for violada.
 *
 * @author Daniel C. Bordin on 27/02/2017.
 */
public class AssertionsXML extends AssertionsBase<MElement, AssertionsXML> implements Iterable<AssertionsXML> {

    public AssertionsXML(Element e) {
        super(MElement.toMElement(e));
    }

    public AssertionsXML(MElement e) {
        super(e);
    }

    public AssertionsXML(Optional<? extends MElement> e) {
        super(e);
    }

    @Override
    protected String errorMsg(String msg) {
        return getTargetOpt().isPresent() ? "No elemento " + getTarget().getFullPath() + ": " + msg : msg;
    }

    /** Verifica se o elemento atual não é null e o nome do nó corresponde ao indicado. */
    public AssertionsXML isName(String expectedName) {
        if (!Objects.equals(expectedName, getTarget().getNodeName())) {
            throw new AssertionError(errorMsg("Nome do nó incorreto", expectedName, getTarget().getNodeName()));
        }
        return this;
    }

    /** Verifies if the element name matches de expected name space prefix and local name. */
    @Nonnull
    public AssertionsXML isName(String expectedPrefix, String expectedName) {
        return isName(expectedPrefix + ":" + expectedName);
    }

    /** Checks if the current element doesn't have any child. */
    public AssertionsXML hasNoChildren() {
        return hasChildren(0);
    }

    /** Checks the number os sub elements of the current element. */
    public AssertionsXML hasChildren(int expectedNumberOfChildren) {
        if (getTarget().countFilhos() != expectedNumberOfChildren) {
            throw new AssertionError(
                    errorMsg("Number of sub Elements incorrect", expectedNumberOfChildren, getTarget().countFilhos()));
        }
        return this;
    }

    /** Verifica não possui nenhum sob Node, ou seja, não pode ter nenhum sub Element, texto, comentários, etc. */
    public AssertionsXML isEmptyNode() {
        if (getTarget().getFirstChild() != null) {
            throw new AssertionError(errorMsg("Era esperado não possuir nenhuma subinformação"));
        }
        return this;
    }

    /** Verifica se o atributo ID do elemento corresponde ao valor esperado. */
    public AssertionsXML isId(int expectedId) {
        return isAtr("id", String.valueOf(expectedId));
    }

    /** Verifica se o valor do atributo e o esperado. */
    private AssertionsXML isAtr(String atributeName, String expectedValue) {
        String currentValue = getTarget().getAttribute(atributeName);
        if (!Objects.equals(expectedValue, currentValue)) {
            throw new AssertionError(
                    errorMsg("O valor do atributo '" + atributeName + "' diferente do esperado", expectedValue,
                            currentValue));
        }
        return this;
    }

    /** Verifica se o valor texto do elemento é o esperado. */
    public AssertionsXML isValue(String expectedValue) {
        String currentValue = getTarget().getValue();
        if (!Objects.equals(expectedValue, currentValue)) {
            throw new AssertionError(
                    errorMsg("O valor do elemento e diferente do esperado", expectedValue, currentValue));
        }
        return this;
    }

    /**
     * Verifica se o Element possui somente um texto é igual ao esperado. Se possui outros tipos de sub nós
     * (comentários, sub elementos, etc.), então dispara exception.
     */
    public AssertionsXML hasOnlyValue(String expectedValue) {
        if (expectedValue == null) {
            return isEmptyNode();
        }
        if (getTarget().getFirstChild() != null && getTarget().getFirstChild().getNextSibling() != null) {
            throw new AssertionError(errorMsg("Possui mais informação além do próprio valor"));
        }
        return isValue(expectedValue);
    }

    /** Verica se o Element é equivalent ao texto XML informado. */
    public AssertionsXML isContentEquals(String expectedXMLContent) {
        isNotNull();
        MElement expectedXML;
        try {
            expectedXML = MParser.parse(expectedXMLContent);
        } catch (SAXException | IOException e) {
            throw new AssertionError("Não foi possível fazer o parse do XML.", e);
        }
        String currentContent = getTarget().toStringExato();
        String expectedAjustedXML = expectedXML.toStringExato();
        if (! Objects.equals(expectedAjustedXML, currentContent)) {
            throw new AssertionError(
                    errorMsg("O conteúdo XML não é o esperado", expectedXML.toString(), getTarget().toString()));
        }
        return this;
    }

    /** Creates a assertion for each sub Element node. */
    @Override
    @Nonnull
    public Iterator<AssertionsXML> iterator() {
        return new Iterator<AssertionsXML>() {
            private MElement next = getTarget().getPrimeiroFilho();

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public AssertionsXML next() {
                if (next == null) {
                    throw new NoSuchElementException();
                }
                MElement current = next;
                next = next.getProximoIrmao();
                return new AssertionsXML(current);
            }
        };
    }

    /** Verifies if the name space URI of the current element is of the expected value. */
    @Nonnull
    public AssertionsXML isNameSpaceUri(@Nullable String expectedNamespaceUri) {
        isNotNull();
        String namespaceURI = getTarget().getNamespaceURI();
        if (!Objects.equals(expectedNamespaceUri, namespaceURI)) {
            throw new AssertionError(
                    errorMsg("NamesoaceUri different from expected", expectedNamespaceUri, namespaceURI));
        }
        return this;
    }

    /** Verifies if the element has one and just one element, then returns it. */
    @Nonnull
    public AssertionsXML getOnlyChild(@Nonnull String expectedName) {
        hasChildren(1);
        AssertionsXML child = new AssertionsXML(getTarget().getPrimeiroFilho());
        child.isName(expectedName);
        return child;
    }

    /** Verifies if the attribute of the element has the expected value. */
    @Nonnull
    public AssertionsXML isAttribute(@Nonnull String attributeName, @Nullable String expectedValue) {
        Object value = getTarget().getAttribute(attributeName);
        if (!Objects.equals(expectedValue, value)) {
            throw new AssertionError(
                    errorMsg("Value for attribute '" + attributeName + "' different from expected", expectedValue,
                            value));
        }
        return this;
    }

    /** Verifies if the attribute is not in the element. */
    @Nonnull
    public AssertionsXML attributeNotPresent(@Nonnull String attributeName) {
        if (getTarget().hasAttribute(attributeName)) {
            throw new AssertionError(errorMsg("Attribute '" + attributeName + "' wasn't expeted to be present"));
        }
        return this;
    }

    /** Verifies if the element has the expected number os attributes. */
    public AssertionsXML hasAttributes(int expectedNumberOfAttributes) {
        if(expectedNumberOfAttributes != getTarget().getAttributes().getLength()) {
            throw new AssertionError(
                    errorMsg("Number of attribute different from expected", expectedNumberOfAttributes,
                            getTarget().getAttributes().getLength()));
        }
        return this;
    }


    /** Returns the sub element at the indecated position. */
    @Nonnull
    public AssertionsXML getChild(int index) {
        MElement[] elements = getTarget().getElements(null);
        if (elements.length <= index) {
            throw new AssertionError(errorMsg("There is no element at index " + index));
        }
        return new AssertionsXML(elements[index]);
    }
}
