/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.util.xml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

/**
 * Percorredor de uma lista especifica de elementos da um XML (aceita filtro
 * xPath). É ao mesmo tempo um iterador e é um MElement com o valores do
 * elemento atual. Por exemplo:<p>
 * <p>
 * <xmp>
 * <dados-turma>
 * <aluno>
 * <nome>João</nome>
 * <idade>22</idade>
 * </aluno>
 * <aluno>
 * <nome>Maria</nome>
 * <idade>22</idade>
 * </aluno>
 * <aluno>
 * <nome>Roberto</nome>
 * <idade>43</idade>
 * </aluno>
 * <professor>
 * <nome>Dr. Carlos<nome>
 * <especialidade>Física</especialidade>
 * </professor>
 * </dados-turma>
 * </xmp>
 * <p>
 * <i>Lendo todos os filhos</i>
 * <xmp>
 * MElementResult rs = new MElementResult(raiz); //ou raiz.selectElements(null);
 * while( rs.next()) {
 * System.out.println( rs.getTagName() + " - " + rs.getValor("nome"));
 * }
 * // Resultado:
 * // aluno - João
 * // aluno - Maria
 * // aluno - Roberto
 * // professor - Dr. Carlos
 * </xmp>
 * <p>
 * <i>Lendo apenas tags filhas aluno</i>
 * <xmp>
 * MElementResult rs = new MElementResult(raiz, "aluno"); //ou raiz.selectElements("aluno");
 * while( rs.next()) {
 * System.out.println( rs.getTagName() + " - " + rs.getValor("nome"));
 * }
 * // Resultado:
 * // aluno - João
 * // aluno - Maria
 * // aluno - Roberto
 * </xmp>
 * <p>
 * <i>Query xPath: alunos com idade de 22</i>
 * <xmp>
 * MElementResult rs = new MElementResult(raiz, "aluno[idade=22]");
 * while( rs.next()) {
 * System.out.println( rs.getTagName() + " - " + rs.getValor("nome"));
 * }
 * // Resultado:
 * // aluno - João
 * // aluno - Maria
 * </xmp>
 * <p>
 * <i>Query xPath: todos os nomes de baixo da tag aluno</i>
 * <xmp>
 * MElementResult rs = new MElementResult(raiz, "aluno/nome");
 * while( rs.next()) {
 * System.out.println( rs.getValor());
 * }
 * // Resultado:
 * // João
 * // Maria
 * // Roberto
 * </xmp>
 *
 * @author Daniel C. Bordin
 * @see XPathToolkit
 */
public final class MElementResult extends MElement implements EWrapper {

    /**
     * Estado em que o elemento atual é válido.
     */
    public static final byte VALIDO = 0;
    /**
     * Estado em que o elemento atual não percorreu nenhum elemento ainda.
     */
    private static final byte INICIO_BLOCO = 1;
    /**
     * Estado em que o elemento atual está além do último elemento da lista.
     */
    private static final byte FIM_BLOCO = 2;

    /**
     * Elemento que terá os dados retornados
     */
    private Element atual_;
    /**
     * Estado em que o elemento atual se encontra.
     */
    private byte estadoAtual_ = INICIO_BLOCO;

    /**
     * Elemento pai no percorrimento simples de filhos.
     */
    private final Element raiz_;
    /**
     * Nome dos elementos filhos a serem percorridos.
     */
    private final String nomeElemento_;

    /**
     * Lista de todos os nos a serem retornados
     */
    private final NodeList list_;
    /**
     * Indice do atual da lista
     */
    private int atualList_ = -1;

    /**
     * Criar um percorredor baseado na lista resultante de uma pesquisa
     * (provavelmente xPath).
     *
     * @param list A ser percorria
     */
    public MElementResult(NodeList list) {
        if (list == null) {
            throw new IllegalArgumentException("list nula");
        }
        raiz_ = null;
        nomeElemento_ = null;
        list_ = list;
    }

    /**
     * Percorredor para todos os filhos imediantamente abaixo do elemento
     * fornecido.
     *
     * @param raiz -
     */
    public MElementResult(Element raiz) {
        if (raiz == null) {
            throw new IllegalArgumentException("Elemento raiz nulo");
        }
        raiz_ = raiz;
        nomeElemento_ = null;
        list_ = null;
    }

    /**
     * Percorredor para o resultado da consulta xPath informada.
     *
     * @param raiz a ter os filhos percorridos
     * @param xPath consulta de filtro a partir da raiz informada. Se null
     * retorna todos os filhos imediatos
     */
    public MElementResult(Element raiz, String xPath) {
        if (raiz == null) {
            throw new IllegalArgumentException("Elemento raiz nulo");
        }
        if ((xPath == null) || XPathToolkit.isSelectSimples(xPath)) {
            raiz_ = raiz;
            nomeElemento_ = xPath;
            list_ = null;
        } else {
            raiz_ = null;
            nomeElemento_ = null;
            list_ = XPathToolkit.selectNodeList(raiz, xPath);
        }
    }

    /**
     * Retorna todos os próximos elementos do result. O elemento atual e
     * anteriores não são retornados.
     *
     * @return Semrpe retorna diferente de zero, mesmo que seja um arry de
     * tamanho zero.
     */
    public final MElement[] getTodos() {
        List<MElement> lista = new ArrayList<>();
        while (next()) {
            lista.add(getAtual());
        }
        return lista.toArray(new MElement[lista.size()]);
    }

    /**
     * Retorna a tag atualmente apontada como um MElement independente e normal.
     * Se for dado um next() o MElement retornando anteriormente não será
     * alterado.
     *
     * @return MElement sepre diferente de null. Se estiver em um estado
     * inválido, uma RuntimeException é disparada.
     */
    public final MElement getAtual() {
        return toMElement(getAtualInterno());
    }

    /**
     * Obtem o Element contido internamente pelo envoltorio.
     *
     * @return Not null ou uma Exception se não houver nenhum atual
     */
    public final Element getOriginal() {
        Element e = getAtualInterno();
        if (e instanceof EWrapper) {
            return ((EWrapper) e).getOriginal();
        }
        return e;
    }

    /**
     * Retorna o element atual para manipulação interna. Se estiver em um
     * estado não válido, dispara erro.
     *
     * @return Sempre diferente de null
     */
    private Element getAtualInterno() {
        if (estadoAtual_ != VALIDO) {
            throw new IllegalStateException(
                    "O elemento atual está no "
                            + ((estadoAtual_ == INICIO_BLOCO) ? "início" : "final")
                            + " da lista");
        }
        return atual_;
    }

    /**
     * Indica se o ponteiro esta antes do primeiro elemento da lista. Uma
     * tentativa de leitura de dados nesse estado, provoca uma exception.
     *
     * @return true se next() não foi chamado nenhuma vez.
     */
    public final boolean isBeforeFirst() {
        return estadoAtual_ == INICIO_BLOCO;
    }

    /**
     * Indica se existe um elemento atual válido. O next() precisa ser chamado
     * pelo meno uma vez para chegar neste estado.
     *
     * @return true se getAtual() != null
     */
    public final boolean isAtualValido() {
        return estadoAtual_ == VALIDO;
    }

    /**
     * Indica que a lista foi percorrida até chegar ao final. O next() precisa
     * ser chamado pelo menos uma vez. Mesmo se o resultado for vazio, o
     * percorredor é iniciado no estado beforeFirst.
     *
     * @return true se a última leitura não encontrou nada
     */
    public final boolean isAfterLast() {
        return estadoAtual_ == FIM_BLOCO;
    }

    /**
     * Determina quanto elemento existe no resultado da consulta.
     *
     * @return -
     */
    public final int count() {
        if (raiz_ == null) {
            return list_.getLength();
        } else {
            return toMElement(raiz_).count(nomeElemento_);
        }
    }

    /**
     * Verifica se existe um próximo elemento para ser lido.
     *
     * @return true se ainda existir.
     */
    public final boolean hasNext() {
        if (estadoAtual_ == FIM_BLOCO) {
            return false;
        }

        if (raiz_ != null) {
            Node no;
            if (estadoAtual_ == INICIO_BLOCO) {
                no = raiz_.getFirstChild();
            } else {
                no = atual_.getNextSibling();
            }
            while (no != null) {
                if (no.getNodeType() == Node.ELEMENT_NODE) {
                    if ((nomeElemento_ == null) || nomeElemento_.equals(no.getNodeName())) {
                        return true;
                    }
                }
                no = no.getNextSibling();
            }
            return false;
        } else {
            return atualList_ < list_.getLength();
        }
    }

    /**
     * Caminha o elemento atual do percorredor para o próximo da lista.
     *
     * @return <code>false</code> se não existem mais elementos na lista de
     * percorrimento; <code>true</code> se encontrou o
     * próximo elemento da lista
     */
    public final boolean next() {
        if (estadoAtual_ == FIM_BLOCO) {
            return false;
        }

        Node no = null;
        atualList_++;
        if (raiz_ != null) {
            if (estadoAtual_ == INICIO_BLOCO) {
                no = raiz_.getFirstChild();
            } else {
                no = atual_.getNextSibling();
            }
            while (no != null) {
                if (no.getNodeType() == Node.ELEMENT_NODE) {
                    if ((nomeElemento_ == null) || nomeElemento_.equals(no.getNodeName())) {
                        break;
                    }
                }
                no = no.getNextSibling();
            }
        } else {
            atual_ = null;
            if (atualList_ < list_.getLength()) {
                no = list_.item(atualList_);
                if (no == null) {
                    throw new RuntimeException(
                            "O result da consulta na posição " + atualList_ + " está null");
                } else if (no.getNodeType() != Node.ELEMENT_NODE) {
                    throw new RuntimeException(
                            "O result da consulta na posição "
                                    + atualList_
                                    + " não é um Element. É um no do tipo "
                                    + XPathToolkit.getNomeTipo(no));
                }
            }
        }
        atual_ = (Element) no;

        if (atual_ == null) {
            estadoAtual_ = FIM_BLOCO;
        } else {
            estadoAtual_ = VALIDO;
        }

        return estadoAtual_ == VALIDO;
    }

    /**
     * Transformar o result em um iterator (todas as modificações no iterator
     * se refletem no result).
     *
     * @return Sempre diferente de null
     */
    public Iterator<MElement> iterator() {
        return new Iterator<MElement>() {

            public void remove() {
                Element original = getOriginal();
                Node pai = original.getParentNode();
                if (pai != null) {
                    pai.removeChild(original);
                }
            }

            public boolean hasNext() {
                if (isBeforeFirst()) {
                    return MElementResult.this.next();
                } else {
                    return getAtual() != null;
                }
            }

            public MElement next() {
                MElement o = getAtual();
                MElementResult.this.next();
                return o;
            }
        };
    }

    //---------------------------------------------------------
    // Método abstratos da interface MElement
    //---------------------------------------------------------

    /**
     * @see org.w3c.dom.Element#getTagName()
     */
    public String getTagName() {
        return getAtualInterno().getTagName();
    }

    /**
     * @see org.w3c.dom.Element#getAttribute(String)
     */
    public String getAttribute(String arg0) {
        return getAtualInterno().getAttribute(arg0);
    }

    /**
     * @see org.w3c.dom.Element#setAttribute(String, String)
     */
    public void setAttribute(String arg0, String arg1) throws DOMException {
        getAtualInterno().setAttribute(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Element#removeAttribute(String)
     */
    public void removeAttribute(String arg0) throws DOMException {
        getAtualInterno().removeAttribute(arg0);
    }

    /**
     * @see org.w3c.dom.Element#getAttributeNode(String)
     */
    public Attr getAttributeNode(String arg0) {
        return getAtualInterno().getAttributeNode(arg0);
    }

    /**
     * @see org.w3c.dom.Element#setAttributeNode(Attr)
     */
    public Attr setAttributeNode(Attr arg0) throws DOMException {
        return getAtualInterno().setAttributeNode(arg0);
    }

    /**
     * @see org.w3c.dom.Element#removeAttributeNode(Attr)
     */
    public Attr removeAttributeNode(Attr arg0) throws DOMException {
        return getAtualInterno().removeAttributeNode(arg0);
    }

    /**
     * @see org.w3c.dom.Element#getElementsByTagName(String)
     */
    public NodeList getElementsByTagName(String arg0) {
        return getAtualInterno().getElementsByTagName(arg0);
    }

    /**
     * @see org.w3c.dom.Element#getAttributeNS(String, String)
     */
    public String getAttributeNS(String arg0, String arg1) {
        return getAtualInterno().getAttributeNS(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Element#setAttributeNS(String, String, String)
     */
    public void setAttributeNS(String arg0, String arg1, String arg2) throws DOMException {
        getAtualInterno().setAttributeNS(arg0, arg1, arg2);
    }

    /**
     * @see org.w3c.dom.Element#removeAttributeNS(String, String)
     */
    public void removeAttributeNS(String arg0, String arg1) throws DOMException {
        getAtualInterno().removeAttributeNS(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Element#getAttributeNodeNS(String, String)
     */
    public Attr getAttributeNodeNS(String arg0, String arg1) {
        return getAtualInterno().getAttributeNodeNS(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Element#setAttributeNodeNS(Attr)
     */
    public Attr setAttributeNodeNS(Attr arg0) throws DOMException {
        return getAtualInterno().setAttributeNodeNS(arg0);
    }

    /**
     * @see org.w3c.dom.Element#getElementsByTagNameNS(String, String)
     */
    public NodeList getElementsByTagNameNS(String arg0, String arg1) {
        return getAtualInterno().getElementsByTagNameNS(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Element#hasAttribute(String)
     */
    public boolean hasAttribute(String arg0) {
        return getAtualInterno().hasAttribute(arg0);
    }

    /**
     * @see org.w3c.dom.Element#hasAttributeNS(String, String)
     */
    public boolean hasAttributeNS(String arg0, String arg1) {
        return getAtualInterno().hasAttributeNS(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Node#getNodeName()
     */
    public String getNodeName() {
        return getAtualInterno().getNodeName();
    }

    /**
     * @see org.w3c.dom.Node#getNodeValue()
     */
    public String getNodeValue() throws DOMException {
        return getAtualInterno().getNodeValue();
    }

    /**
     * @see org.w3c.dom.Node#setNodeValue(String)
     */
    public void setNodeValue(String arg0) throws DOMException {
        getAtualInterno().setNodeValue(arg0);
    }

    /**
     * @see org.w3c.dom.Node#getNodeType()
     */
    public short getNodeType() {
        return getAtualInterno().getNodeType();
    }

    /**
     * @see org.w3c.dom.Node#getParentNode()
     */
    public Node getParentNode() {
        return getAtualInterno().getParentNode();
    }

    /**
     * @see org.w3c.dom.Node#getChildNodes()
     */
    public NodeList getChildNodes() {
        return getAtualInterno().getChildNodes();
    }

    /**
     * @see org.w3c.dom.Node#getFirstChild()
     */
    public Node getFirstChild() {
        return getAtualInterno().getFirstChild();
    }

    /**
     * @see org.w3c.dom.Node#getLastChild()
     */
    public Node getLastChild() {
        return getAtualInterno().getLastChild();
    }

    /**
     * @see org.w3c.dom.Node#getPreviousSibling()
     */
    public Node getPreviousSibling() {
        return getAtualInterno().getPreviousSibling();
    }

    /**
     * @see org.w3c.dom.Node#getNextSibling()
     */
    public Node getNextSibling() {
        return getAtualInterno().getNextSibling();
    }

    /**
     * @see org.w3c.dom.Node#getAttributes()
     */
    public NamedNodeMap getAttributes() {
        return getAtualInterno().getAttributes();
    }

    /**
     * @see org.w3c.dom.Node#getOwnerDocument()
     */
    public Document getOwnerDocument() {
        return getAtualInterno().getOwnerDocument();
    }

    /**
     * @see org.w3c.dom.Node#insertBefore(Node, Node)
     */
    public Node insertBefore(Node arg0, Node arg1) throws DOMException {
        return getAtualInterno().insertBefore(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Node#replaceChild(Node, Node)
     */
    public Node replaceChild(Node arg0, Node arg1) throws DOMException {
        return getAtualInterno().replaceChild(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Node#removeChild(Node)
     */
    public Node removeChild(Node arg0) throws DOMException {
        return getAtualInterno().removeChild(arg0);
    }

    /**
     * @see org.w3c.dom.Node#appendChild(Node)
     */
    public Node appendChild(Node arg0) throws DOMException {
        return getAtualInterno().appendChild(arg0);
    }

    /**
     * @see org.w3c.dom.Node#hasChildNodes()
     */
    public boolean hasChildNodes() {
        return getAtualInterno().hasChildNodes();
    }

    /**
     * @see org.w3c.dom.Node#cloneNode(boolean)
     */
    public Node cloneNode(boolean arg0) {
        return getAtualInterno().cloneNode(arg0);
    }

    /**
     * @see org.w3c.dom.Node#normalize()
     */
    public void normalize() {
        getAtualInterno().normalize();
    }

    /**
     * @see org.w3c.dom.Node#isSupported(String, String)
     */
    public boolean isSupported(String arg0, String arg1) {
        return getAtualInterno().isSupported(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Node#getNamespaceURI()
     */
    public String getNamespaceURI() {
        return getAtualInterno().getNamespaceURI();
    }

    /**
     * @see org.w3c.dom.Node#getPrefix()
     */
    public String getPrefix() {
        return getAtualInterno().getPrefix();
    }

    /**
     * @see org.w3c.dom.Node#setPrefix(String)
     */
    public void setPrefix(String arg0) throws DOMException {
        getAtualInterno().setPrefix(arg0);
    }

    /**
     * @see org.w3c.dom.Node#getLocalName()
     */
    public String getLocalName() {
        return getAtualInterno().getLocalName();
    }

    /**
     * @see org.w3c.dom.Node#hasAttributes()
     */
    public boolean hasAttributes() {
        return getAtualInterno().hasAttributes();
    }

    //-------------------------------------------
    // Métodos para o Jdk 1.5
    //-------------------------------------------

    /**
     * @see org.w3c.dom.Element#getSchemaTypeInfo()
     */
    public TypeInfo getSchemaTypeInfo() {
        return getAtualInterno().getSchemaTypeInfo();
    }

    /**
     * @see org.w3c.dom.Element#setIdAttribute(java.lang.String, boolean)
     */
    public void setIdAttribute(String name, boolean isId) throws DOMException {
        getAtualInterno().setIdAttribute(name, isId);
    }

    /**
     * @see org.w3c.dom.Element#setIdAttributeNS(java.lang.String, java.lang.String, boolean)
     */
    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        getAtualInterno().setIdAttributeNS(namespaceURI, localName, isId);
    }

    /**
     * @see org.w3c.dom.Element#setIdAttributeNode(org.w3c.dom.Attr, boolean)
     */
    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        getAtualInterno().setIdAttributeNode(idAttr, isId);
    }

    /**
     * @see org.w3c.dom.Node#getBaseURI()
     */
    public String getBaseURI() {
        return getAtualInterno().getBaseURI();
    }

    /**
     * @see org.w3c.dom.Node#compareDocumentPosition(org.w3c.dom.Node)
     */
    public short compareDocumentPosition(Node other) throws DOMException {
        return getAtualInterno().compareDocumentPosition(other);
    }

    /**
     * @see org.w3c.dom.Node#getTextContent()
     */
    public String getTextContent() throws DOMException {
        return getAtualInterno().getTextContent();
    }

    /**
     * @see org.w3c.dom.Node#setTextContent(java.lang.String)
     */
    public void setTextContent(String textContent) throws DOMException {
        getAtualInterno().setTextContent(textContent);

    }

    /**
     * @see org.w3c.dom.Node#isSameNode(org.w3c.dom.Node)
     */
    public boolean isSameNode(Node other) {
        return getOriginal().isSameNode(other);
    }

    /**
     * @see org.w3c.dom.Node#lookupPrefix(java.lang.String)
     */
    public String lookupPrefix(String namespaceURI) {
        return getAtualInterno().lookupPrefix(namespaceURI);
    }

    /**
     * @see org.w3c.dom.Node#isDefaultNamespace(java.lang.String)
     */
    public boolean isDefaultNamespace(String namespaceURI) {
        return getAtualInterno().isDefaultNamespace(namespaceURI);
    }

    /**
     * @see org.w3c.dom.Node#lookupNamespaceURI(java.lang.String)
     */
    public String lookupNamespaceURI(String prefix) {
        return getAtualInterno().lookupNamespaceURI(prefix);
    }

    /**
     * @see org.w3c.dom.Node#isEqualNode(org.w3c.dom.Node)
     */
    public boolean isEqualNode(Node arg) {
        return getAtualInterno().isEqualNode(arg);
    }

    /**
     * @see org.w3c.dom.Node#getFeature(java.lang.String, java.lang.String)
     */
    public Object getFeature(String feature, String version) {
        return getAtualInterno().getFeature(feature, version);
    }

    /**
     * @see org.w3c.dom.Node#setUserData(java.lang.String, java.lang.Object, org.w3c.dom.UserDataHandler)
     */
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return getAtualInterno().setUserData(key, data, handler);
    }

    /**
     * @see org.w3c.dom.Node#getUserData(java.lang.String)
     */
    public Object getUserData(String key) {
        return getAtualInterno().getUserData(key);
    }
    //-------------------------------------------
    // Fim Métodos para o Jdk 1.5
    //-------------------------------------------

}
