/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.internal.xml;

import com.sun.org.apache.xpath.internal.XPathAPI;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.NodeIterator;

import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * Para ao retornar resultado vazio não criar novo objeto.
     */
    private static final String[] LISTA_VAZIA = new String[0];

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
        StringBuffer buffer = new StringBuffer();
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
        StringBuffer buffer = new StringBuffer();
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
    private static void getFullPath(StringBuffer buffer, Node no, Node topo) {

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
    private static void getIndiceElemento(StringBuffer buffer, Node e) {
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
    public static final String getNomeTipo(Node n) {
        if (n == null) {
            return null;
        }
        switch (n.getNodeType()) {
            case Node.ELEMENT_NODE:
                return "Element Node";
            case Node.TEXT_NODE:
                return "Text Node";
            case Node.ATTRIBUTE_NODE:
                return "Attribute Node";
            case Node.CDATA_SECTION_NODE:
                return "CData Section Node";
            case Node.COMMENT_NODE:
                return "Comment Node";
            case Node.DOCUMENT_NODE:
                return "Document node";
            case Node.DOCUMENT_FRAGMENT_NODE:
                return "Document Fragment Node";
            case Node.DOCUMENT_TYPE_NODE:
                return "Document Type Node";
            case Node.ENTITY_REFERENCE_NODE:
                return "Entity Reference Node";
            case Node.ENTITY_NODE:
                return "Entity Node";
            case Node.NOTATION_NODE:
                return "Notation Node";
            case Node.PROCESSING_INSTRUCTION_NODE:
                return "Processing Instruction Node";
            default:
                return "Desconhecido (" + n.getClass().getName() + ") Node";
        }
    }

    /**
     * Verifica se a consulta xPath é complexa ou é simples. No caso de ser
     * simples, pode ser utilizado o algorítmo implementado aqui na classe.
     *
     * @param xPath consulta a ser verificada a complexidadde
     * @return false Para indica necessidade da API XPath
     */
    static boolean isSimples(String xPath) {
        for (int i = xPath.length() - 1; i != -1; i--) {
            switch (xPath.charAt(i)) {
                case '/':
                    // Comando "//" no inicio
                    if ((i == 1) && (xPath.charAt(0) == '/')) {
                        return false;
                    }
                    break;
                case ':':
                    // Se : simples (refeerencia Namespace)
                    // se :: então é xPAth
                    if ((i == 0) || (xPath.charAt(i - 1) == ':')) {
                        return false;
                    }
                    break;
                case '*':
                case '[':
                case ']':
                case '.':
                    return false;
            }
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
    static final boolean isSelectSimples(String xPath) {
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
        // O XPath não funciona a partir o MElement
        if (contextNode instanceof EWrapper) {
            contextNode = ((EWrapper) contextNode).getOriginal();
        }
        if (isSimples(xPath)) {
            return findSimples(contextNode, xPath);
        } else {
            try {
                return XPathAPI.selectSingleNode(contextNode, xPath);
            } catch (TransformerException e) {
                throw new RuntimeException(e);
            }
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
        if (result.getNodeType() != Node.ELEMENT_NODE) {
            throw new RuntimeException(
                    "O elemento resultante ("
                            + getFullPath(result)
                            + ") não é um Element, mas um "
                            + getNomeTipo(result));
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
        // O XPath não funciona a partir o MElement
        if (contextNode instanceof EWrapper) {
            contextNode = ((EWrapper) contextNode).getOriginal();
        }
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
     * @return Sempre not null. Se não encontrar nada retorna array de tamanho
     * zero.
     */
    public static String[] getValores(Node contextNode, String xPath) {
        // O XPath não funciona a partir o MElement
        if (contextNode instanceof EWrapper) {
            contextNode = ((EWrapper) contextNode).getOriginal();
        }

        List<String> lista = null;
        String valor;
        if (isSelectSimples(xPath)) {
            MElementResult rs = new MElementResult((Element) contextNode, xPath);
            while (rs.next()) {
                valor = rs.getValor();
                if (valor != null) {
                    if (lista == null) {
                        lista = new ArrayList<>();
                    }
                    lista.add(valor);
                }
            }
        } else {
            NodeList list = selectNodeList(contextNode, xPath);
            int tam = list.getLength();
            for (int i = 0; i < tam; i++) {
                valor = MElement.getValorTexto(list.item(i));
                if (valor != null) {
                    if (lista == null) {
                        lista = new ArrayList<>();
                    }
                    lista.add(valor);
                }
            }
        }

        //monta o array de String
        if (lista == null) {
            return LISTA_VAZIA;
        }
        return (String[]) lista.toArray(new String[lista.size()]);
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
        // O XPath não funciona a partir o MElement
        if (contextNode instanceof EWrapper) {
            contextNode = ((EWrapper) contextNode).getOriginal();
        }

        try {
            return XPathAPI.selectNodeList(contextNode, xPath);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
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
        // O XPath não funciona a partir o MElement
        if (context instanceof EWrapper) {
            context = ((EWrapper) context).getOriginal();
        }

        try {
            return XPathAPI.selectNodeIterator(context, xPath);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Para caminho simples, faz a pesquisa manual em vez de usar a
     * biblioteca XPath. A diferença de tempo pode ser da ordem de 1000 vezes.
     *
     * @param pai Elemento onde será pesquisado
     * @param path Caminho a ser pesquisa deve conter apenas nomes e /
     * @return O elemento se for encontrado.
     */
    private static final Node findSimples(Node pai, String path) {

        Node resp = pai;
        String nomeElemento;

        if (path.charAt(0) == '/') {
            while (resp.getParentNode() != null) {
                resp = resp.getParentNode();
            }
            if (path.length() == 1) {
                return resp;
            }
            path = path.substring(1);
        }

        while ((resp != null) && (path != null)) {
            int pos = path.indexOf('/');
            if (pos == -1) {
                nomeElemento = path;
                path = null;
            } else {
                nomeElemento = path.substring(0, pos);
                path = path.substring(pos + 1);
            }

            if (nomeElemento.charAt(0) == '@') {
                if ((path != null) || (resp.getNodeType() != Node.ELEMENT_NODE)) {
                    throw new RuntimeException("O xPath '" + path + "' é inválido");
                }
                return ((Element) resp).getAttributeNode(nomeElemento.substring(1));
            }

            Node n = resp.getFirstChild();
            while ((n != null)
                    && ((n.getNodeType() != Node.ELEMENT_NODE)
                    || (!n.getNodeName().equals(nomeElemento)))) {
                n = n.getNextSibling();
            }
            resp = (Element) n;
        }
        return resp;
    }

}
