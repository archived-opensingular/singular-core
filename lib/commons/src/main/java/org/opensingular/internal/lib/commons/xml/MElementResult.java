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

import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.internal.function.SupplierUtil;
import org.opensingular.lib.commons.lambda.ISupplier;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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
 * System.out.println( rs.getTagName() + " - " + rs.getValue("nome"));
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
 * System.out.println( rs.getTagName() + " - " + rs.getValue("nome"));
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
 * System.out.println( rs.getTagName() + " - " + rs.getValue("nome"));
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
 * System.out.println( rs.getValue());
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
    public static final byte VALID = 0;
    /**
     * Estado em que o elemento atual não percorreu nenhum elemento ainda.
     */
    private static final byte BLOCK_START = 1;
    /**
     * Estado em que o elemento atual está além do último elemento da lista.
     */
    private static final byte BLOCK_END = 2;

    /**
     * Elemento que terá os dados retornados
     */
    private ISupplier<Element> current;
    /**
     * Estado em que o elemento atual se encontra.
     */
    private byte currentState = BLOCK_START;

    /**
     * Elemento pai no percorrimento simples de filhos.
     */
    private final ISupplier<Element> root;
    /**
     * Nome dos elementos filhos a serem percorridos.
     */
    private final String elementName;

    /**
     * Lista de todos os nos a serem retornados
     */
    private final List<ISupplier<Element>> list;
    /**
     * Indice do atual da lista
     */
    private int currentList = -1;

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
        this.root = null;
        this.elementName = null;
        this.list = convert(list);
    }

    /**
     * Percorredor para todos os filhos imediantamente abaixo do elemento
     * fornecido.
     *
     * @param root -
     */
    public MElementResult(Element root) {
        if (root == null) {
            throw new IllegalArgumentException("Elemento raiz nulo");
        }
        this.root = SupplierUtil.serializable(root);
        this.elementName = null;
        this.list = null;
    }

    /**
     * Percorredor para o resultado da consulta xPath informada.
     *
     * @param root  a ter os filhos percorridos
     * @param xPath consulta de filtro a partir da raiz informada. Se null
     *              retorna todos os filhos imediatos
     */
    public MElementResult(@Nonnull Element root, @Nullable String xPath) {
        if ((xPath == null) || XPathToolkit.isSelectSimples(xPath)) {
            this.root = SupplierUtil.serializable(root);
            this.elementName = xPath;
            this.list = null;
        } else {
            this.root = null;
            this.elementName = null;
            this.list = convert(XPathToolkit.selectNodeList(root, xPath));
        }
    }

    private List<ISupplier<Element>> convert(NodeList nodeList) {
        List<ISupplier<Element>> newList = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node no = nodeList.item(i);
            if (no == null) {
                throw new SingularException("O result da consulta na posição " + currentList + " está null");
            } else if (!XmlUtil.isNodeTypeElement(no)) {
                throw new SingularException(
                        "O result da consulta na posição " + currentList + " não é um Element. É um no do tipo " +
                                XPathToolkit.getNodeTypeName(no));
            }
            newList.add(SupplierUtil.serializable((Element) no));
        }
        return newList;
    }

    /**
     * Retorna todos os próximos elementos do result. O elemento atual e
     * anteriores não são retornados.
     *
     * @return Semrpe retorna diferente de zero, mesmo que seja um arry de
     * tamanho zero.
     */
    public final MElement[] getAll() {
        List<MElement> list = new ArrayList<>();
        while (next()) {
            list.add(getCurrent());
        }
        return list.toArray(new MElement[list.size()]);
    }

    /**
     * Retorna a tag atualmente apontada como um MElement independente e normal.
     * Se for dado um next() o MElement retornando anteriormente não será
     * alterado.
     *
     * @return MElement sepre diferente de null. Se estiver em um estado
     * inválido, uma RuntimeException é disparada.
     */
    public final MElement getCurrent() {
        return toMElement(getCurrentInternal());
    }

    /**
     * Obtem o Element contido internamente pelo envoltorio.
     *
     * @return Not null ou uma Exception se não houver nenhum atual
     */
    public final Element getOriginal() {
        Element e = getCurrentInternal();
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
    private Element getCurrentInternal() {
        if (currentState != VALID) {
            throw new IllegalStateException(
                    "O elemento atual está no " + ((currentState == BLOCK_START) ? "início" : "final") + " da lista");
        }
        return current.get();
    }

    /**
     * Indica se o ponteiro esta antes do primeiro elemento da lista. Uma
     * tentativa de leitura de dados nesse estado, provoca uma exception.
     *
     * @return true se next() não foi chamado nenhuma vez.
     */
    public final boolean isBeforeFirst() {
        return currentState == BLOCK_START;
    }

    /**
     * Indica se existe um elemento atual válido. O next() precisa ser chamado
     * pelo meno uma vez para chegar neste estado.
     *
     * @return true se getCurrent() != null
     */
    public final boolean isCurrentValid() {
        return currentState == VALID;
    }

    /**
     * Indica que a lista foi percorrida até chegar ao final. O next() precisa
     * ser chamado pelo menos uma vez. Mesmo se o resultado for vazio, o
     * percorredor é iniciado no estado beforeFirst.
     *
     * @return true se a última leitura não encontrou nada
     */
    public final boolean isAfterLast() {
        return currentState == BLOCK_END;
    }

    /**
     * Determina quanto elemento existe no resultado da consulta.
     *
     * @return -
     */
    public final int count() {
        if (root == null) {
            return list.size();
        } else {
            return toMElement(root.get()).count(elementName);
        }
    }

    /**
     * Verifica se existe um próximo elemento para ser lido.
     *
     * @return true se ainda existir.
     */
    public final boolean hasNext() {
        if (currentState == BLOCK_END) {
            return false;
        }

        if (root != null) {
            Node no;
            if (currentState == BLOCK_START) {
                no = root.get().getFirstChild();
            } else {
                no = current.get().getNextSibling();
            }
            return XmlUtil.nextSiblingOfTypeElement(no, elementName) != null;
        } else {
            return currentList < list.size();
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
        if (currentState == BLOCK_END) {
            return false;
        }

        currentList++;
        if (root != null) {
            Node node;
            if (currentState == BLOCK_START) {
                node = root.get().getFirstChild();
            } else {
                node = current.get().getNextSibling();
            }
            node = XmlUtil.nextSiblingOfTypeElement(node, elementName);
            current = node == null ? null : SupplierUtil.serializable((Element) node);
        } else {
            current = null;
            if (currentList < list.size()) {
                current = list.get(currentList);
            }
        }

        if (current == null) {
            currentState = BLOCK_END;
        } else {
            currentState = VALID;
        }

        return currentState == VALID;
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
                Node parent = original.getParentNode();
                if (parent != null) {
                    parent.removeChild(original);
                }
            }

            public boolean hasNext() {
                if (isBeforeFirst()) {
                    return MElementResult.this.next();
                } else {
                    return getCurrent() != null;
                }
            }

            public MElement next() {
                MElement o = getCurrent();
                if (o == null) {
                    throw new NoSuchElementException();
                }
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
        return getCurrentInternal().getTagName();
    }

    /**
     * @see org.w3c.dom.Element#getAttribute(String)
     */
    public String getAttribute(String arg0) {
        return getCurrentInternal().getAttribute(arg0);
    }

    /**
     * @see org.w3c.dom.Element#setAttribute(String, String)
     */
    public void setAttribute(String arg0, String arg1) throws DOMException {
        getCurrentInternal().setAttribute(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Element#removeAttribute(String)
     */
    public void removeAttribute(String arg0) throws DOMException {
        getCurrentInternal().removeAttribute(arg0);
    }

    /**
     * @see org.w3c.dom.Element#getAttributeNode(String)
     */
    public Attr getAttributeNode(String arg0) {
        return getCurrentInternal().getAttributeNode(arg0);
    }

    /**
     * @see org.w3c.dom.Element#setAttributeNode(Attr)
     */
    public Attr setAttributeNode(Attr arg0) throws DOMException {
        return getCurrentInternal().setAttributeNode(arg0);
    }

    /**
     * @see org.w3c.dom.Element#removeAttributeNode(Attr)
     */
    public Attr removeAttributeNode(Attr arg0) throws DOMException {
        return getCurrentInternal().removeAttributeNode(arg0);
    }

    /**
     * @see org.w3c.dom.Element#getElementsByTagName(String)
     */
    public NodeList getElementsByTagName(String arg0) {
        return getCurrentInternal().getElementsByTagName(arg0);
    }

    /**
     * @see org.w3c.dom.Element#getAttributeNS(String, String)
     */
    public String getAttributeNS(String arg0, String arg1) {
        return getCurrentInternal().getAttributeNS(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Element#setAttributeNS(String, String, String)
     */
    public void setAttributeNS(String arg0, String arg1, String arg2) throws DOMException {
        getCurrentInternal().setAttributeNS(arg0, arg1, arg2);
    }

    /**
     * @see org.w3c.dom.Element#removeAttributeNS(String, String)
     */
    public void removeAttributeNS(String arg0, String arg1) throws DOMException {
        getCurrentInternal().removeAttributeNS(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Element#getAttributeNodeNS(String, String)
     */
    public Attr getAttributeNodeNS(String arg0, String arg1) {
        return getCurrentInternal().getAttributeNodeNS(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Element#setAttributeNodeNS(Attr)
     */
    public Attr setAttributeNodeNS(Attr arg0) throws DOMException {
        return getCurrentInternal().setAttributeNodeNS(arg0);
    }

    /**
     * @see org.w3c.dom.Element#getElementsByTagNameNS(String, String)
     */
    public NodeList getElementsByTagNameNS(String arg0, String arg1) {
        return getCurrentInternal().getElementsByTagNameNS(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Element#hasAttribute(String)
     */
    public boolean hasAttribute(String arg0) {
        return getCurrentInternal().hasAttribute(arg0);
    }

    /**
     * @see org.w3c.dom.Element#hasAttributeNS(String, String)
     */
    public boolean hasAttributeNS(String arg0, String arg1) {
        return getCurrentInternal().hasAttributeNS(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Node#getNodeName()
     */
    public String getNodeName() {
        return getCurrentInternal().getNodeName();
    }

    /**
     * @see org.w3c.dom.Node#getNodeValue()
     */
    public String getNodeValue() throws DOMException {
        return getCurrentInternal().getNodeValue();
    }

    /**
     * @see org.w3c.dom.Node#setNodeValue(String)
     */
    public void setNodeValue(String arg0) throws DOMException {
        getCurrentInternal().setNodeValue(arg0);
    }

    /**
     * @see org.w3c.dom.Node#getNodeType()
     */
    public short getNodeType() {
        return getCurrentInternal().getNodeType();
    }

    /**
     * @see org.w3c.dom.Node#getParentNode()
     */
    public Node getParentNode() {
        return getCurrentInternal().getParentNode();
    }

    /**
     * @see org.w3c.dom.Node#getChildNodes()
     */
    public NodeList getChildNodes() {
        return getCurrentInternal().getChildNodes();
    }

    /**
     * @see org.w3c.dom.Node#getFirstChild()
     */
    public Node getFirstChild() {
        return getCurrentInternal().getFirstChild();
    }

    /**
     * @see org.w3c.dom.Node#getLastChild()
     */
    public Node getLastChild() {
        return getCurrentInternal().getLastChild();
    }

    /**
     * @see org.w3c.dom.Node#getPreviousSibling()
     */
    public Node getPreviousSibling() {
        return getCurrentInternal().getPreviousSibling();
    }

    /**
     * @see org.w3c.dom.Node#getNextSibling()
     */
    public Node getNextSibling() {
        return getCurrentInternal().getNextSibling();
    }

    /**
     * @see org.w3c.dom.Node#getAttributes()
     */
    public NamedNodeMap getAttributes() {
        return getCurrentInternal().getAttributes();
    }

    /**
     * @see org.w3c.dom.Node#getOwnerDocument()
     */
    public Document getOwnerDocument() {
        return getCurrentInternal().getOwnerDocument();
    }

    /**
     * @see org.w3c.dom.Node#insertBefore(Node, Node)
     */
    public Node insertBefore(Node arg0, Node arg1) throws DOMException {
        return getCurrentInternal().insertBefore(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Node#replaceChild(Node, Node)
     */
    public Node replaceChild(Node arg0, Node arg1) throws DOMException {
        return getCurrentInternal().replaceChild(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Node#removeChild(Node)
     */
    public Node removeChild(Node arg0) throws DOMException {
        return getCurrentInternal().removeChild(arg0);
    }

    /**
     * @see org.w3c.dom.Node#appendChild(Node)
     */
    public Node appendChild(Node arg0) throws DOMException {
        return getCurrentInternal().appendChild(arg0);
    }

    /**
     * @see org.w3c.dom.Node#hasChildNodes()
     */
    public boolean hasChildNodes() {
        return getCurrentInternal().hasChildNodes();
    }

    /**
     * @see org.w3c.dom.Node#cloneNode(boolean)
     */
    public Node cloneNode(boolean arg0) {
        return getCurrentInternal().cloneNode(arg0);
    }

    /**
     * @see org.w3c.dom.Node#normalize()
     */
    public void normalize() {
        getCurrentInternal().normalize();
    }

    /**
     * @see org.w3c.dom.Node#isSupported(String, String)
     */
    public boolean isSupported(String arg0, String arg1) {
        return getCurrentInternal().isSupported(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Node#getNamespaceURI()
     */
    public String getNamespaceURI() {
        return getCurrentInternal().getNamespaceURI();
    }

    /**
     * @see org.w3c.dom.Node#getPrefix()
     */
    public String getPrefix() {
        return getCurrentInternal().getPrefix();
    }

    /**
     * @see org.w3c.dom.Node#setPrefix(String)
     */
    public void setPrefix(String arg0) throws DOMException {
        getCurrentInternal().setPrefix(arg0);
    }

    /**
     * @see org.w3c.dom.Node#getLocalName()
     */
    public String getLocalName() {
        return getCurrentInternal().getLocalName();
    }

    /**
     * @see org.w3c.dom.Node#hasAttributes()
     */
    public boolean hasAttributes() {
        return getCurrentInternal().hasAttributes();
    }

    //-------------------------------------------
    // Métodos para o Jdk 1.5
    //-------------------------------------------

    /**
     * @see org.w3c.dom.Element#getSchemaTypeInfo()
     */
    public TypeInfo getSchemaTypeInfo() {
        return getCurrentInternal().getSchemaTypeInfo();
    }

    /**
     * @see org.w3c.dom.Element#setIdAttribute(java.lang.String, boolean)
     */
    public void setIdAttribute(String name, boolean isId) throws DOMException {
        getCurrentInternal().setIdAttribute(name, isId);
    }

    /**
     * @see org.w3c.dom.Element#setIdAttributeNS(java.lang.String, java.lang.String, boolean)
     */
    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        getCurrentInternal().setIdAttributeNS(namespaceURI, localName, isId);
    }

    /**
     * @see org.w3c.dom.Element#setIdAttributeNode(org.w3c.dom.Attr, boolean)
     */
    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        getCurrentInternal().setIdAttributeNode(idAttr, isId);
    }

    /**
     * @see org.w3c.dom.Node#getBaseURI()
     */
    public String getBaseURI() {
        return getCurrentInternal().getBaseURI();
    }

    /**
     * @see org.w3c.dom.Node#compareDocumentPosition(org.w3c.dom.Node)
     */
    public short compareDocumentPosition(Node other) throws DOMException {
        return getCurrentInternal().compareDocumentPosition(other);
    }

    /**
     * @see org.w3c.dom.Node#getTextContent()
     */
    public String getTextContent() throws DOMException {
        return getCurrentInternal().getTextContent();
    }

    /**
     * @see org.w3c.dom.Node#setTextContent(java.lang.String)
     */
    public void setTextContent(String textContent) throws DOMException {
        getCurrentInternal().setTextContent(textContent);

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
        return getCurrentInternal().lookupPrefix(namespaceURI);
    }

    /**
     * @see org.w3c.dom.Node#isDefaultNamespace(java.lang.String)
     */
    public boolean isDefaultNamespace(String namespaceURI) {
        return getCurrentInternal().isDefaultNamespace(namespaceURI);
    }

    /**
     * @see org.w3c.dom.Node#lookupNamespaceURI(java.lang.String)
     */
    public String lookupNamespaceURI(String prefix) {
        return getCurrentInternal().lookupNamespaceURI(prefix);
    }

    /**
     * @see org.w3c.dom.Node#isEqualNode(org.w3c.dom.Node)
     */
    public boolean isEqualNode(Node arg) {
        return getCurrentInternal().isEqualNode(arg);
    }

    /**
     * @see org.w3c.dom.Node#getFeature(java.lang.String, java.lang.String)
     */
    public Object getFeature(String feature, String version) {
        return getCurrentInternal().getFeature(feature, version);
    }

    /**
     * @see org.w3c.dom.Node#setUserData(java.lang.String, java.lang.Object, org.w3c.dom.UserDataHandler)
     */
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return getCurrentInternal().setUserData(key, data, handler);
    }

    /**
     * @see org.w3c.dom.Node#getUserData(java.lang.String)
     */
    public Object getUserData(String key) {
        return getCurrentInternal().getUserData(key);
    }
    //-------------------------------------------
    // Fim Métodos para o Jdk 1.5
    //-------------------------------------------

}
