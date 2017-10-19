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

import com.sun.org.apache.xpath.internal.XPathAPI;
import org.opensingular.lib.commons.base.SingularException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.opensingular.internal.lib.commons.xml.XmlUtil.isNodeTypeElement;

/**
 * Métodos utilitários para trabalhar com XPath. Para um bom tutorial em
 * XPath veja
 * <a href="http://www.w3schools.com/xpath/default.asp" target="_blank">
 * http://www.w3schools.com/xpath/default.asp</a> ou para exemplos
 * <a href="http://www.zvon.org/xxl/XPathTutorial/General/examples.html"
 * target="_blank">
 * passo-a-passo</a>. Dado o XML de exemplo: <p>
 * <xmp>
 * <pedido>
 * <cd cod="1">
 * <grupo>Pato Fu</grupo>
 * <nome>Acustico</nome>
 * <ano>2002</ano>
 * <faixa>Minha Musica</faixa>
 * <faixa>Sua Musica</faixa>
 * <faixa>Nossa Musica</faixa>
 * </cd>
 * <cd cod="4">
 * <grupo>Paralamas</grupo>
 * <nome>9 Luas</nome>
 * <ano>1999</ano>
 * <faixa>9 Luas</faixa>
 * <faixa>8 Luas</faixa>
 * <faixa>7 Luas</faixa>
 * </cd>
 * <cd cod="6">
 * <grupo>U2</grupo>
 * <nome>Zooropa</nome>
 * <ano>1997</ano>
 * <faixa>Babyface</faixa>
 * <faixa>Numb</faixa>
 * <faixa>Lemon</faixa>
 * </cd>
 * </pedido>
 * </xmp>
 * <p>
 * Os resultados para chamadas utilizando <code>
 * XPathToolkit.selectNode(raiz, xPath)</code> são:
 * <xmp>
 * /pedido/cd[2]/nome                 --> <nome>9 Luas</nome>
 * /pedido[1]/cd[2]/nome              --> <nome>9 Luas</nome>
 * /pedido/cd[2]/faixa[2]             --> <faixa>8 Luas</faixa>
 * /pedido/cd[@cod=6]/nome            --> <nome>Zooropa</nome>
 * /pedido/cd[ano=1997]/faixa[last()] --> <faixa>Lemon</faixa>
 * /pedido/cd[@cod=6]/nome/../ano"    --> <ano>1997</ano>
 * </xmp>
 * <p>
 * A implementação do XPath utilizada é o
 * <a href="http://xml.apache.org/xalan-j"> Xalan</a>
 *
 * @author Daniel C. Bordin
 */
public final class XPathToolkit {

    private static final Map<Short, String> NODE_LABEL_MAP;

    static {
        NODE_LABEL_MAP = new HashMap<>();
        NODE_LABEL_MAP.put(Node.ELEMENT_NODE, "Element Node");
        NODE_LABEL_MAP.put(Node.TEXT_NODE, "Text Node");
        NODE_LABEL_MAP.put(Node.ATTRIBUTE_NODE, "Attribute Node");
        NODE_LABEL_MAP.put(Node.CDATA_SECTION_NODE, "CData Section Node");
        NODE_LABEL_MAP.put(Node.COMMENT_NODE, "Comment Node");
        NODE_LABEL_MAP.put(Node.DOCUMENT_NODE, "Document Node");
        NODE_LABEL_MAP.put(Node.DOCUMENT_FRAGMENT_NODE, "Document Fragment Node");
        NODE_LABEL_MAP.put(Node.DOCUMENT_TYPE_NODE, "Document Type Node");
        NODE_LABEL_MAP.put(Node.ENTITY_REFERENCE_NODE, "Entity Reference Node");
        NODE_LABEL_MAP.put(Node.ENTITY_NODE, "Entity Node");
        NODE_LABEL_MAP.put(Node.NOTATION_NODE, "Notation Node");
        NODE_LABEL_MAP.put(Node.PROCESSING_INSTRUCTION_NODE, "Processing Instruction Node");
    }

    /**
     * Esconde o construtor pro ser uma classe utiliária.
     */
    private XPathToolkit() {
    }

    /**
     * Retorna o path completo do elemento.
     *
     * @see #getFullPath(Node, Node)
     */
    public static String getFullPath(Node no) {
        StringBuilder buffer = new StringBuilder();
        getFullPath(buffer, no, null);
        return buffer.toString();
    }

    /**
     * Retorna o path completo do elemento. O resutlado
     * deve ser semelehante a <code>pedido/item[3]/codigo</code>, que
     * representa o elemento codigo no terceiro elemento item do no pedido.
     *
     * @param no Nó a ter o nome completo adicionado no buffer
     * @param topo Nó até onde será montado o path.
     * Pode ser null para indicar até o topo.
     * @return path completo
     */
    public static String getFullPath(Node no, Node topo) {
        StringBuilder buffer = new StringBuilder();
        getFullPath(buffer, no, topo);
        return buffer.toString();
    }

    /**
     * Adiciona no buffer o path completo do elemento. A sintaxe não é
     * padrão. O resutlado deve ser semelehante a
     * <code>pedido/item[3]/codigo</code>, que dizer o elemento codigo no
     * terceiro elemento item do no pedido.
     *
     * @param buffer destino do nome a ser montado
     * @param no Nó a ter o nome completo adicionado no buffer
     * @param topo Nó até onde será montado o path. O path incluirá esse nó.
     * Pode ser null para indicar até o topo.
     */
    private static void getFullPath(StringBuilder buffer, Node no, Node topo) {

        Node parent = no.getParentNode();
        if (parent == null) {
            if (no.getNodeType() == Node.ATTRIBUTE_NODE) {
                parent = ((Attr) no).getOwnerElement();
            }
        } else {
            if (parent.getNodeType() == Node.DOCUMENT_NODE) {
                parent = null;
            }
        }

        if (parent == null) {
            buffer.append('/');
        } else {
            /* XXX: O método "isSameNode" não deveria ser usado aqui? */
            if (parent != topo) {
                getFullPath(buffer, parent, topo);
                buffer.append('/');
            }
        }

        /* XXX: O método "isSameNode" não deveria ser usado aqui? */
        if (no != topo) {
            switch (no.getNodeType()) {
                case Node.ELEMENT_NODE:
                    buffer.append(no.getNodeName());
                    getIndiceElemento(buffer, no);
                    break;
                case Node.ATTRIBUTE_NODE:
                    buffer.append('@');
                    buffer.append(no.getNodeName());
                    break;
                default:
            }
        }

    }

    /**
     * Adiciona ao buffer a posição do nó no em relação ao seus nós
     * irmãos com mesmo nome no formato "[9]".Se for o único com o
     * nome no mesmo nível, não faz nada.
     *
     * @param buffer Destino do texto
     * @param e Node a ser pesquisado o nível
     */
    private static void getIndiceElemento(StringBuilder buffer, Node e) {
        int pos = -1;
        // Verifica se possui no com mesmo nome antes
        Node cursor = e.getPreviousSibling();
        while (cursor != null) {
            if ((cursor.getNodeType() == e.getNodeType())
                    && (e.getNodeName().equals(cursor.getNodeName()))) {
                if (pos == -1) {
                    pos = 2;
                } else {
                    pos++;
                }
            }
            cursor = cursor.getPreviousSibling();
        }
        if (pos != -1) {
            buffer.append('[').append(pos).append(']');
            return;
        } // Verifica se possui no com mesmo nome depois
        cursor = e.getNextSibling();
        while (cursor != null) {
            if ((cursor.getNodeType() == e.getNodeType())
                    && (e.getNodeName().equals(cursor.getNodeName()))) {
                buffer.append("[1]");
                return;
            }
            cursor = cursor.getNextSibling();
        }

    }

    /**
     * Retorna o nome completo do tipo do Node.
     *
     * @param n No alvo
     * @return String Retorna o nome por extenso ou null se N for null.
     */
    public static String getNodeTypeName(Node n) {
        if (n == null) {
            return null;
        }

        String nodeLabel = NODE_LABEL_MAP.get(n.getNodeType());
        return nodeLabel != null ? nodeLabel : "Desconhecido (" + n.getClass().getName() + ") Node";
    }

    /**
     * Verifica se a consulta xPath é complexa ou é simples. No caso de ser
     * simples, pode ser utilizado o algorítmo implementado aqui na classe.
     *
     * @param xPath consulta a ser verificada a complexidadde
     * @return false Para indica necessidade da API XPath
     */
    static boolean isSimples(String xPath) {

        Pattern pattern = Pattern.compile("[*\\[\\].]|::");

        if (xPath.startsWith("//")
                || xPath.startsWith(":")
                || pattern.matcher(xPath).find()) {
            return false;
        }

        return true;
    }

    /**
     * Verifica se a consulta xPath para retornar lista de Element é simples
     * (ou seja, pode utilizar algoritmo próprio em vez do xPath real).
     *
     * @param xPath consulta a ser verificada a complexidadde
     * @return false Para indica necessidade da API XPath
     */
    static boolean isSelectSimples(String xPath) {
        if (xPath == null) {
            return true;
        }
        for (int i = xPath.length() - 1; i != -1; i--) {
            switch (xPath.charAt(i)) {
                case '/':
                case ':':
                case '*':
                case '[':
                case ']':
                case '.':
                    return false;
                default:
            }
        }
        return true;
    }

    /**
     * Use an XPath string to select a single node.
     * XPath namespace prefixes are resolved from the context node.<br>
     * Esse método é um atalho de conveniência.
     *
     * @param contextNode The node to start searching from.
     * @param xPath A valid XPath string.
     * @return Node The first node found that matches the XPath, or null.
     */
    public static Node selectNode(Node contextNode, String xPath) {
        if (isSimples(xPath)) {
            return findSimples(EWrapper.getOriginal(contextNode), xPath);
        }
        try {
            return XPathAPI.selectSingleNode(EWrapper.getOriginal(contextNode), xPath);
        } catch (TransformerException e) {
            throw SingularException.rethrow(e);
        }
    }

    /**
     * Use an XPath string to select a single node.
     * XPath namespace prefixes are resolved from the context node.<br>
     * Esse método é um atalho de conveniência.
     *
     * @param contextNode The node to start searching from.
     * @param xPath A valid XPath string.
     * @return Node The first node found that matches the XPath, or null.
     */
    public static Element selectElement(Node contextNode, String xPath) {
        Node result = selectNode(contextNode, xPath);
        if (result == null) {
            return null;
        }
        if (!isNodeTypeElement(result)) {
            throw new SingularException(
                    "O elemento resultante (" + getFullPath(result) + ") não é um Element, mas um " +
                            getNodeTypeName(result));
        }
        return (Element) result;
    }

    /**
     * Use an XPath string to select a single node.
     * XPath namespace prefixes are resolved from the context node.<br>
     * Esse método é um atalho de conveniência.
     *
     * @param contextNode The node to start searching from.
     * @param xPath A valid XPath string.
     * @return Node The first node found that matches the XPath, or null.
     */
    public static MElementResult selectElements(Node contextNode, String xPath) {
        if (contextNode instanceof Element) {
            if (xPath == null) {
                return new MElementResult((Element) contextNode);
            } else if (isSelectSimples(xPath)) {
                return new MElementResult((Element) contextNode, xPath);
            }
        }
        return new MElementResult(selectNodeList(contextNode, xPath));
    }

    /**
     * Retorna o valor do texto de todos os nos resutlantes da consulta.
     *
     * @param contextNode Ponto de partida da pesquisa
     * @param xPath Consulta para seleção dos nodes
     * @return Sempre not null. Se não encontrar nada retorna vazio.
     */
    public static List<String> getValues(Node contextNode, String xPath) {
        List<String> resultList = null;
        if (isSelectSimples(xPath)) {
            MElementResult rs = new MElementResult((Element) contextNode, xPath);
            while (rs.next()) {
                resultList = addToList(resultList, rs.getValue());
            }
        } else {
            NodeList nodesList = selectNodeList(contextNode, xPath);
            int tam = nodesList.getLength();
            for (int i = 0; i < tam; i++) {
                resultList = addToList(resultList, MElement.getValueText(nodesList.item(i)));
            }
        }
        return resultList == null ? Collections.emptyList() : resultList;
    }
    private static List<String> addToList(List<String> list, String value) {
        List<String> nova = list;
        if (value != null) {
            if (list == null) {
                nova = new ArrayList<>();
            }
            nova.add(value);
        }
        return nova;
    }

    /**
     * Use an XPath string to select a nodelist. XPath namespace prefixes
     * are resolved from the contextNode. Esse método é um atalho de
     * conveniência.
     *
     * @param contextNode The node to start searching from.
     * @param xPath A valid XPath string.
     * @return NodeList A NodeIterator, should never be null.
     */
    public static NodeList selectNodeList(Node contextNode, String xPath) {
        try {
            // O XPath não funciona a partir o MElement
            return XPathAPI.selectNodeList(EWrapper.getOriginal(contextNode), xPath);
        } catch (TransformerException e) {
            throw SingularException.rethrow(e.getMessage(), e);
        }
    }

    /**
     * Use an XPath string to select a nodelist. XPath namespace prefixes
     * are resolved from the contextNode.
     *
     * @param context The node to start searching from.
     * @param xPath A valid XPath string.
     * @return NodeIterator A NodeIterator, should never be null.
     */
    public static NodeIterator selectNodeIterator(Node context, String xPath) {
        try {
            return XPathAPI.selectNodeIterator(EWrapper.getOriginal(context), xPath);
        } catch (TransformerException e) {
            throw SingularException.rethrow(e.getMessage(), e);
        }
    }

    /**
     * Para caminho simples, faz a pesquisa manual em vez de usar a
     * biblioteca XPath. A diferença de tempo pode ser da ordem de 1000 vezes.
     *
     * @param parent Elemento onde será pesquisado
     * @param path Caminho a ser pesquisa deve conter apenas nomes e /
     * @return O elemento se for encontrado.
     */
    private static Node findSimples(final Node parent, final String nodePath) {

        Node resp = parent;
        String path = nodePath;
        if (path.charAt(0) == '/') {
            resp = XmlUtil.getRootParent(resp);
            if (path.length() == 1) {
                return resp;
            }
            path = path.substring(1);
        }

        String elementName;
        while ((resp != null) && (path != null)) {
            int pos = path.indexOf('/');
            if (pos == -1) {
                elementName = path;
                path = null;
            } else {
                elementName = path.substring(0, pos);
                path = path.substring(pos + 1);
            }

            if (elementName.charAt(0) == '@') {
                if ((path != null) || !isNodeTypeElement(resp)) {
                    throw SingularException.rethrow("O xPath '" + path + "' é inválido");
                }
                return ((Element) resp).getAttributeNode(elementName.substring(1));
            }

            resp = XmlUtil.nextSiblingOfTypeElement(resp.getFirstChild(), elementName);
        }
        return resp;
    }

}
