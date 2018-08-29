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

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Método de apoio interno na manipulação de XML.
 *
 * @author Daniel C. Bordin on 16/01/2017.
 */
final class XmlUtil {

    private XmlUtil() {}

    /** Verifica se nó é do tipo {@link Node#TEXT_NODE}. */
    public static boolean isNodeTypeText(Node node) {
        return node != null && node.getNodeType() == Node.TEXT_NODE;
    }

    /** Verifica se nó é do tipo {@link Node#ELEMENT_NODE}. */
    public static boolean isNodeTypeElement(Node node) {
        return node != null && node.getNodeType() == Node.ELEMENT_NODE;
    }

    /**
     * Verifica se nó é do tipo {@link Node#ELEMENT_NODE} e têm o nome informado.
     *
     * @param expectedElementName Se null, não verificar se o nome é igual e retorna true (basta ser um Element)
     */
    public static boolean isNodeTypeElement(Node node, String expectedElementName) {
        return isNodeTypeElement(node) && (expectedElementName == null || expectedElementName.equals(
                node.getNodeName()));
    }

    /**
     * Retorna o nó raiz do nó informado, ou seja, sobre na hierarquia até encontrar um nó sem pai.
     *
     * @return O próprio nó se o mesmo já for o raiz.
     */
    public static Node getRootParent(Node node) {
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
    public static Element nextSiblingOfTypeElement(Node node, String elementName) {
        for (Node n = node; n != null; n = n.getNextSibling()) {
            if (isNodeTypeElement(n, elementName)) {
                return (Element) n;
            }
        }
        return null;
    }
}
