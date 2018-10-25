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

package org.opensingular.internal.lib.commons.xml;

import org.apache.commons.lang3.StringUtils;
import org.opensingular.lib.commons.base.SingularException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Objects;

/**
 * Helper class for XML manipulation and creation.
 *
 * @author Daniel C. Bordin.
 */
public final class XmlUtil {

    /** Cache do builderFactory de acordo com a configuração desejada. */
    private final static DocumentBuilderFactory[] builderFactory__ = new DocumentBuilderFactory[4];

    /** Representa o factory de Document. */
    private static DocumentBuilder documentBuilder__;

    private XmlUtil() {}

    /** Creates a new empty XML Document. */
    @Nonnull
    static synchronized Document newDocument() {

        if (documentBuilder__ == null) {
            DocumentBuilderFactory f = getDocumentBuilderFactory(true, false);
            try {
                documentBuilder__ = f.newDocumentBuilder();
            } catch (Exception e) {
                throw SingularException.rethrow("Fail to create XML document builder", e);
            }
        }
        return documentBuilder__.newDocument();
    }

    /**
     * Retora o document builder de acordo com a configuração desejada. Faz um cache do DocumentBuilder para evitar
     * rodar o algorítmo de pesquisa toda vez.
     *
     * @param namespaceAware Se o builder irá tratar namespace
     * @param validating     Se o builder irá valdiar o XML em função de um DTD. Aplicavel apenas quando for fazer
     *                       parse.
     */
    @Nonnull
    static DocumentBuilderFactory getDocumentBuilderFactory(boolean namespaceAware, boolean validating) {

        int index = (namespaceAware ? 1 : 0) + (validating ? 2 : 0);
        if (builderFactory__[index] == null) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(namespaceAware);
            factory.setValidating(validating);
            builderFactory__[index] = factory;
        }
        return builderFactory__[index];
    }

    /** It the String isn't empty or null, parses it into a XML Element. */
    @Nullable
    public static Element parseXmlOptional(@Nullable String xmlString) {
        if (xmlString == null || StringUtils.isBlank(xmlString)) {
            return null;
        }
        try {
            return MParser.parse(xmlString);
        } catch (Exception e) {
            throw SingularException.rethrow("Fail to read XML", e);
        }
    }

    /**
     * Guaranties to return a valid XML Element parsed from the provided String.
     *
     * @throws SingularException If xml is empty or invalid.
     */
    @Nonnull
    public static Element parseXml(@Nonnull String xmlString) {
        if (StringUtils.isBlank(xmlString)) {
            throw new SingularException("XML String has a empty content");
        }
        try {
            return MParser.parse(xmlString);
        } catch (Exception e) {
            throw SingularException.rethrow("Fail to read XML", e);
        }
    }

    /** Creates a new XML document with a root element with the informed name. */
    @Nonnull
    public static Element newRootElement(@Nonnull String elementName) {
        Objects.requireNonNull(elementName);
        Document d = newDocument();
        Element newElement = d.createElementNS(null, elementName);
        d.appendChild(newElement);
        return newElement;
    }

    /** Adds a child element with informed name. */
    @Nonnull
    public static Element addElement(@Nonnull Element parent, @Nonnull String name) {
        Document d = parent.getOwnerDocument();
        Element newElement = d.createElement(name);
        parent.appendChild(newElement);
        return newElement;
    }

    /** Generates a compact String representation of the XML. */
    @Nonnull
    public static String toStringExact(@Nonnull Element element) {
        return MElement.toMElement(element).toStringExato();
    }

    /** Return the element's child text node value, if available. */
    @Nullable
    public static String getValueText(@Nullable Element element) {
        if (element != null) {
            Node n = element.getFirstChild();
            if (isNodeTypeText(n)) {
                return n.getNodeValue();
            }
        }
        return null;
    }

    /** Return the text content for the informed Node. If it's  Element, returns the child text node. */
    @Nullable
    static String getValueText(@Nullable Node node) {
        if (node == null) {
            return null;
        }
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                Node n = node.getFirstChild();
                if (XmlUtil.isNodeTypeText(n)) {
                    return n.getNodeValue();
                }
                break;
            case Node.ATTRIBUTE_NODE:
            case Node.TEXT_NODE:
                String value = node.getNodeValue();
                if (!StringUtils.isEmpty(value)) {
                    return value;
                }
                break;
            default:
                throw new SingularException("getValueText(Node) não trata nó " + XPathToolkit.getNodeTypeName(node));
        }
        return null;
    }

    /** Verifica se nó é do tipo {@link Node#TEXT_NODE}. */
    static boolean isNodeTypeText(Node node) {
        return node != null && node.getNodeType() == Node.TEXT_NODE;
    }

    /** Verifica se nó é do tipo {@link Node#ELEMENT_NODE}. */
    static boolean isNodeTypeElement(Node node) {
        return node != null && node.getNodeType() == Node.ELEMENT_NODE;
    }

    /**
     * Verifica se nó é do tipo {@link Node#ELEMENT_NODE} e têm o nome informado.
     *
     * @param expectedElementName Se null, não verificar se o nome é igual e retorna true (basta ser um Element)
     */
    static boolean isNodeTypeElement(Node node, String expectedElementName) {
        return isNodeTypeElement(node) && (expectedElementName == null || expectedElementName.equals(
                node.getNodeName()));
    }

    /**
     * Retorna o nó raiz do nó informado, ou seja, sobre na hierarquia até encontrar um nó sem pai.
     *
     * @return O próprio nó se o mesmo já for o raiz.
     */
    @Nonnull
    static Node getRootParent(@Nonnull Node node) {
        Node root = node;
        while (root.getParentNode() != null) {
            root = root.getParentNode();
        }
        return root;
    }

    /**
     * Localiza o próximo nó irmão do nó informado que atenda ao nome informado.
     *
     * @param elementName Se null, não verificar se o nome é igual e retorna true (basta ser um Element)
     */
    @Nullable
    static Element nextSiblingOfTypeElement(Node node, String elementName) {
        for (Node n = node; n != null; n = n.getNextSibling()) {
            if (isNodeTypeElement(n, elementName)) {
                return (Element) n;
            }
        }
        return null;
    }
}