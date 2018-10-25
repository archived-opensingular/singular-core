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

package org.opensingular.lib.commons.test;

import org.opensingular.internal.lib.commons.xml.MElement;
import org.opensingular.internal.lib.commons.xml.MParser;
import org.opensingular.internal.lib.commons.xml.XPathToolkit;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
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
public class AssertionsXML extends AssertionsBase<AssertionsXML, MElement> implements Iterable<AssertionsXML> {

    public AssertionsXML(Element e) {
        super(MElement.toMElement(e));
    }

    public AssertionsXML(MElement e) {
        super(e);
    }

    public AssertionsXML(@Nonnull Optional<? extends Element> e) {
        this(e.orElse(null));
    }

    @Override
    @Nonnull
    protected  Optional<String> generateDescriptionForCurrentTarget(@Nonnull Optional<MElement> current) {
        return current.map(element -> "No elemento " + element.getFullPath());
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
    @Nonnull
    public AssertionsXML isValue(@Nullable String expectedValue) {
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
    @Nonnull
    public AssertionsXML isContentEquals(@Nonnull String expectedXMLContent) {
        isNotNull();
        try {
            return isContentEquals(MParser.parse(expectedXMLContent));
        } catch (SAXException | IOException e) {
            throw new AssertionError("Não foi possível fazer o parse do XML.", e);
        }
    }

    /** Verica se o Element é equivalent ao texto XML informado. */
    @Nonnull
    public AssertionsXML isContentEquals(@Nonnull MElement expectedXML) {
        String currentXMLString = getTarget().toStringExato();
        String expectedXMLString = expectedXML.toStringExato();
        if (!Objects.equals(expectedXMLString, currentXMLString)) {
            throw new AssertionError(errorMsg("O conteúdo XML não é o esperado", expectedXMLString, currentXMLString));
        }
        return this;
    }

    @Nonnull
    public AssertionsXML isEquivalentTo(@Nonnull Element expectedXML) {
        isEquivalent(expectedXML, getTarget());
        return this;
    }

    /**
     * Verifica se ambos os nos são iguais fazendo uma comparação em profundidade.
     */
    public static void isEquivalent(@Nonnull Node n1, @Nonnull Node n2) {
        if (n1 == n2) {
            return;
        }

        isEquivalent(n1, n2, "NodeName", n1.getNodeName(), n2.getNodeName());
        isEquivalent(n1, n2, "NodeValue", n1.getNodeValue(), n2.getNodeValue());
        isEquivalent(n1, n2, "Namespace", n1.getNamespaceURI(), n2.getNamespaceURI());
        isEquivalent(n1, n2, "Prefix", n1.getPrefix(), n2.getPrefix());
        isEquivalent(n1, n2, "LocalName", n1.getLocalName(), n2.getLocalName());

        if (isSameClass(Element.class, n1, n2)) {
            Element e1 = (Element) n1;
            Element e2 = (Element) n2;
            verifyIfSameAttributes(e1, e2);
            verifyIfSameChildren(e1, e2);

        } else if (!isSameClass(Attr.class, n1, n2) && !isSameClass(Text.class, n1, n2)) {
            throw new AssertionError("Tipo de nó " + n1.getClass() + " não tratado");
        }
    }

    private static void verifyIfSameAttributes(@Nonnull Element e1, @Nonnull Element e2) {
        NamedNodeMap nn1 = e1.getAttributes();
        NamedNodeMap nn2 = e2.getAttributes();
        if (nn1.getLength() != nn2.getLength()) {
            throw new AssertionError(
                    "O número atributos em " + XPathToolkit.getFullPath(e1) + " (qtd=" + nn1.getLength() +
                            " é diferente de n2 (qtd=" + nn2.getLength() + ")");
        }
        for (int i = 0; i < nn1.getLength(); i++) {
            isEquivalent((Attr) nn1.item(i), (Attr) nn2.item(i));
        }
    }

    private static void verifyIfSameChildren(@Nonnull Element e1, @Nonnull Element e2) {
        Node child1 = e1.getFirstChild();
        Node child2 = e2.getFirstChild();
        int count = 0;
        while ((child1 != null) && (child2 != null)) {
            isEquivalent(child1, child2);
            child1 = child1.getNextSibling();
            child2 = child2.getNextSibling();
            count++;
        }
        if (child1 != null) {
            throw new AssertionError("Há mais node [" + count + "] " + XPathToolkit.getNodeTypeName(child1) + " (" +
                    XPathToolkit.getFullPath(child1) + ") em n1:" + XPathToolkit.getFullPath(e1));
        }
        if (child2 != null) {
            throw new AssertionError("Há mais node [" + count + "] " + XPathToolkit.getNodeTypeName(child2) + " (" +
                    XPathToolkit.getFullPath(child2) + ") em n2:" + XPathToolkit.getFullPath(e2));
        }
    }

    /**
     * Verifica se os atributos são iguais. Existe pois a comparação de atributos possui particularidades.
     */
    private static void isEquivalent(@Nonnull Attr n1, @Nonnull Attr n2) {
        if (n1 == n2) {
            return;
        }
        isEquivalent(n1, n2, "NodeName", n1.getNodeName(), n2.getNodeName());
        isEquivalent(n1, n2, "NodeValue", n1.getNodeValue(), n2.getNodeValue());

        //Por algum motivo depois do parse Prefix passa de null para não null
        //isIgual(n1, n2, "Prefix", n1.getPrefix(), n2.getPrefix());
        //Por algum motivo depois do parse Localname passe de não null para
        // null
        //isIgual(n1, n2, "LocalName", n1.getLocalName(), n2.getLocalName());

        if (!(n1.getNodeName().startsWith("xmlns") && n2.getNodeName().startsWith("xmlns"))) {
            isEquivalent(n1, n2, "Namespace", n1.getNamespaceURI(), n2.getNamespaceURI());
        }
    }

    /**
     * Verifica se ambos o nós são da classe informada. Se apenas um for, um
     * erro é disparado devido a incompatibilidade.
     */
    private static boolean isSameClass(Class<?> c, Node original, Node newNode) {
        if (c.isInstance(original)) {
            if (c.isInstance(newNode)) {
                return true;
            } else {
                throw new AssertionError(XPathToolkit.getFullPath(original) + " não é da mesma classe que " +
                        XPathToolkit.getFullPath(newNode));
            }
        } else if (c.isInstance(newNode)) {
            throw new AssertionError(XPathToolkit.getFullPath(original) + " não é da mesma classe que " +
                    XPathToolkit.getFullPath(newNode));
        }
        return false;
    }

    /**
     * Verifica a igualdade de um determiando para de objetos já considerando a
     * situação de um deles ser null.
     */
    private static void isEquivalent(Node n1, Node n2, String partName, Object v1, Object v2) {
        if (((v1 == null) && (v2 != null)) || ((v1 != null) && !v1.equals(v2))) {
            throw new AssertionError(
                    "O(a) " + partName + " em  " + XPathToolkit.getFullPath(n2) + " (" + formatValue(v2) +
                            ") está diferente do original em " + XPathToolkit.getFullPath(n1) + " (" + formatValue(v1) +
                            ")");
        }
    }

    @Nonnull
    private static String formatValue(@Nullable Object o) {
        return o == null ? "null" : "'" + o + "'";
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

    /** Finds the elements for the xPath. */
    @Nonnull
    public AssertionsXML getElement(String xPath) {
        return new AssertionsXML(getTarget().getElement(xPath));
    }
}