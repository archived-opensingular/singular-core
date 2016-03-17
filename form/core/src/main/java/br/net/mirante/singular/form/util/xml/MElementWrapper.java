/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.util.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

/**
 * @author Daniel C. Bordin
 */
public class MElementWrapper extends MElement implements EWrapper {

    /**
     * Caracter separador de nomes de Elementos (Xpath).
     */
    static final char SEPARADOR_ELEMENT = '/';

    /**
     * Cache do builderFactory de acordo com a configuração desejada.
     */
    private static DocumentBuilderFactory[] buiderFactory__ = new DocumentBuilderFactory[4];

    /**
     * Representa o factory de Document.
     */
    private static DocumentBuilder documentBuilder__;

    /**
     * Elemento que contem realmente os dados.
     */
    private final Element original_;

    /**
     * Constroi um MElement para ler e alterar o Element informado.
     *
     * @param original -
     */
    public MElementWrapper(Element original) {
        if (original == null) {
            throw new IllegalArgumentException("Elemento original não pode ser " + "null");
        } else if (original instanceof MElementWrapper) {
            original_ = ((MElementWrapper) original).original_;
        } else {
            original_ = original;
        }
    }

    /**
     * Constroi um MElement raiz com o nome informado.
     *
     * @param nomeRaiz -
     */
    public MElementWrapper(String nomeRaiz) {
        original_ = newRootElement(nomeRaiz);
    }

    /**
     * Constroi um MElement raiz com o nome informado.
     *
     * @param namespaceURI Tipicamente o name space possui o formato de uma URL
     * (não é obrigatório) no formato, por exemplo,
     * http://www.miranteinfo.com/sisfinanceiro/cobranca/registraPagamento.
     * @param qualifiedName o nome do elemento que será criado. Pode conter
     * prefixo (ex.: "fin:ContaPagamento").
     */
    public MElementWrapper(String namespaceURI, String qualifiedName) {
        original_ = newRootElement(namespaceURI, qualifiedName);
    }

    /**
     * Obtem o element original.
     *
     * @return -
     */
    @Override
    public final Element getOriginal() {
        return original_;
    }

    /**
     * Instanciado de Document precarregado.
     *
     * @return sempre != null
     */
    static synchronized Document newDocument() {

        if (documentBuilder__ == null) {
            DocumentBuilderFactory f = getDocumentBuilderFactory(true, false);
            try {
                documentBuilder__ = f.newDocumentBuilder();
            } catch (Exception e) {
                //} catch(javax.xml.parsers.ParserConfigurationException e) {
                throw new RuntimeException("Não instancia o parser XML: ", e);
            }
        }
        return documentBuilder__.newDocument();
    }

    /**
     * Cria um elemento XML em um novo documento.
     *
     * @param elementName o nome do elemento que será criado
     * @return o elemento que foi criado
     */
    static final Element newRootElement(String elementName) {
        Document d = newDocument();
        Element newElement = d.createElementNS(null, elementName);
        d.appendChild(newElement);
        return newElement;
    }

    /**
     * Cria um elemento XML em um novo documento para um determinado namespace.
     *
     * @param namespaceURI Tipicamente o name space possui o formato de uma URL
     * (não é obrigatório) no formato, por exemplo,
     * http://www.miranteinfo.com/sisfinanceiro/cobranca/registraPagamento.
     * @param qualifiedName o nome do elemento que será criado. Pode conter
     * prefixo (ex.: "fin:ContaPagamento").
     * @return o elemento que foi criado
     */
    public static Element newRootElement(String namespaceURI, String qualifiedName) {

        Document d = newDocument();
        Element novo = d.createElementNS(namespaceURI, qualifiedName);

        //Verifica se precisa colocar um atributo por conta do Namespace
        if ((qualifiedName != null) && (qualifiedName.length() != 0)) {
            int posPrefixo = qualifiedName.indexOf(':');
            if ((posPrefixo == -1)) {
                novo.setAttribute("xmlns", namespaceURI);
            } else {
                String prefixo = qualifiedName.substring(0, posPrefixo);
                novo.setAttribute("xmlns:" + prefixo, namespaceURI);
            }
            //novo.setAttribute("xmlns:" + nome.getPrefix(), namespaceURI);
        }
        d.appendChild(novo);
        return novo;
    }

    /**
     * Retora o document builder de acordo com a configuração desejada. Faz um
     * cache do DocumentBuilder para evitar rodar o algorítmo de pesquisa toda
     * vez.
     *
     * @param namespaceAware Se o builder irá tratar namespace
     * @param validating Se o builder irá valdiar o XML em função de um DTD.
     * Aplicavel apenas quando for fazer parse.
     * @return sempre diferente de null.
     */
    static final DocumentBuilderFactory getDocumentBuilderFactory(boolean namespaceAware,
            boolean validating) {
        // Utiliza cache de DocumentBuilderFactory para evitar que o algorítmo
        // de localização do factory execute toda vez. O problema é que quando
        // em applet, o new Instance fica frequentemente indo ao servidor para
        // tentar lozalizar um implementação de factory.
        // Daniel (08/05/2003)

        int indice = (namespaceAware ? 1 : 0) + (validating ? 2 : 0);
        if (buiderFactory__[indice] == null) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(namespaceAware);
            factory.setValidating(validating);
            buiderFactory__[indice] = factory;
        }
        return buiderFactory__[indice];
    }

    /**
     * Metodo utilizado para colocar apenas o conteúdo de um elemento dentro de
     * outro elemento. Os elementos contidos no elemento <code>no</code> serão
     * colocados ao final da lista de elementos elemento <code>pai</code>
     * (append).
     *
     * @param pai elemento que o conteúdo do outro elemento
     * @param no elemento cujo conteúdo será colocado dentro do elemento pai
     */
    static final void copyElement(Element pai, Element no) {
        if ((pai == null) || (no == null)) {
            throw new IllegalArgumentException("Null não permitido");
        }

        Document doc = pai.getOwnerDocument();

        Node atual = no.getFirstChild();
        while (atual != null) {
            switch (atual.getNodeType()) {
                case (Node.TEXT_NODE):
                    pai.appendChild(doc.createTextNode(atual.getNodeValue()));
                    break;
                case (Node.ELEMENT_NODE):
                    Element novo = newElement(doc, (Element) atual);
                    pai.appendChild(novo);
                    copyElement(novo, (Element) atual);
                    break;
                default:
                    throw new RuntimeException("O no do tipo "
                            + atual.getNodeType()
                            + " não é suportado");
            }
            atual = atual.getNextSibling();
        }
    }

    /**
     * Cria um novo element com os mesmos atributos e namespace do elemento
     * fornecido.
     *
     * @param owner Document a ser utilizado na criação do Element
     * @param original Elemento a sercopiado
     * @return Sempre diferente de null
     */
    private static final Element newElement(Document owner, Element original) {
        Element novo = owner.createElementNS(original.getNamespaceURI(), original.getTagName());

        if (original.hasAttributes()) {
            NamedNodeMap domAttributes = original.getAttributes();
            int noOfAttributes = domAttributes.getLength();
            Attr attr;
            for (int i = 0; i < noOfAttributes; i++) {
                attr = (Attr) domAttributes.item(i);
                if (attr.getNamespaceURI() == null) {
                    novo.setAttribute(attr.getNodeName(), attr.getNodeValue());
                } else {
                    novo.setAttributeNS(attr.getNamespaceURI(), attr.getNodeName(), attr
                            .getNodeValue());
                }
            }
        }

        return novo;
    }

    /**
     * Metodo utilizado para colocar um elemento (e seu conteúdo) dentro de
     * outro elemento, podendo ser usado um outro nome ao invés do nome do
     * elemento sendo copiado. O elemento é copiado ao final da lista de
     * elementos (append) do elemento de destino.
     *
     * @param pai elemento que receberá um novo elemento com o conteúdo do
     * elemento <code>no</code>
     * @param no elemento cujo conteúdo será colocado dentro de um elemento de
     * nome <code>novoNome</code>, que será colocado dentro do
     * elemento <code>pai</code>
     * @param novoNome nome do elemento que receberá o conteúdo do elemento
     * <code>no</code> e que será colocado dentro de <code>pai</code>;
     * se for <code>null</code>, é usado o nome do elemento
     * <code>no</code>
     * @return O novo no criado no novo pai
     */
    static final Element copyElement(Element pai, Element no, String novoNome) {
        if ((pai == null) || (no == null)) {
            throw new IllegalArgumentException("Null não permitido");
        }
        Document doc = pai.getOwnerDocument();

        Element novo;
        if (novoNome == null) {
            novo = newElement(doc, no);
        } else {
            novo = doc.createElement(novoNome);
        }
        pai.appendChild(novo);
        copyElement(novo, no);

        return novo;
    }

    /**
     * Adiciona um elemento binario no formato BASE64 dentro do elemento pai. O
     * formato BASE64 é definido pelo RFC1521 do RFC1521. Ele transforma um
     * binário em uma string, um codificação de 6 bits. Deste modo, um array
     * binário ocupa 33% mais espaço no formato BASE, contudo passa a ser uma
     * string simples. É necessário levar em consideração questões de gasto de
     * memória e de custo de conversão de binário para string e string para
     * binário ao se decidir pelo uso deste formato.
     *
     * @param value o array binário do elemento adicionado (a ser convertido p/
     * BASE64)
     * @return o elemento que foi adicionado
     */
    static final String toBASE64(byte[] value) {
        if (value == null) {
            return null;
        }
        return Base64.encodeBytes(value);
    }

    /**
     * Converte um binario para String no formato BASE64 até esgotar a
     * InputStream. O formato BASE64 é definido pelo RFC1521 do RFC1521. Ele
     * transforma um binário em uma string, um codificação de 6 bits. Deste
     * modo, um array binário ocupa 33% mais espaço no formato BASE, contudo
     * passa a ser uma string simples. É necessário levar em consideração
     * questões de gasto de memória e de custo de conversão de binário para
     * string e string para binário ao se decidir pelo uso deste formato.
     *
     * @param in Stream com os dados a serem convertidos p/ BASE64.
     * @return -
     *
     * @throws IOException Se houver erro na leitura do dados
     */
    public static String toBASE64(InputStream in) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException("inputstream está null");
        }
        return Base64.encodeFromInputStream(in);
    }

    /**
     * Converte a String com codificação BASE64 de volta para um array de bytes.
     *
     * @param stringValue String a ser convertida
     * @return null se a string for null
     */
    static final byte[] fromBASE64(String stringValue) {
        if (stringValue == null) {
            return null;
        }
        return Base64.decode(stringValue);
    }

    /**
     * Converte a String com codificação BASE64 escrevendo o resultado para a
     * stream informada.
     *
     * @param stringValue String a ser convertida.
     * @param out Destino do bytes decodificados.
     * @throws IOException Se problema com a stream de output.
     */
    static final void fromBASE64(String stringValue, OutputStream out) throws IOException {
        if (stringValue == null || out == null) {
            throw new IllegalArgumentException("parametro null");
        }
        Base64.decodeToOutputStream(stringValue, out);
    }

    /**
     * Adiciona um elemento a um elemento pai. <br>
     * O elemento é adicionado sem valor.
     *
     * @param parent o elemento dentro do qual um elemento será inserido
     * @param namespaceURI poder ser null
     * @param qualifiedName o nome do elemento que será inserido
     * @return o elemento que foi adicionado
     */
    static final Element addElementNS(Node parent, String namespaceURI, String qualifiedName) {
        Document d = parent.getOwnerDocument();

        int pos = qualifiedName.lastIndexOf(SEPARADOR_ELEMENT);
        if (pos != -1) {
            if (pos == 0) {
                while (parent.getParentNode() != null) {
                    parent = parent.getParentNode();
                }
            } else {
                parent = getElementCriando(d, parent, namespaceURI, qualifiedName.substring(0, pos));
            }
            qualifiedName = qualifiedName.substring(pos + 1);
            namespaceURI = null;
        }
        Element novo;
        if (isVazio(namespaceURI)) {
            if ((parent.getNamespaceURI() != null) && isVazio(parent.getPrefix())) {
                namespaceURI = parent.getNamespaceURI();
            } else {
                namespaceURI = null; //Podia ser String vazia
            }
            novo = d.createElementNS(namespaceURI, qualifiedName);
        } else {
            novo = d.createElementNS(namespaceURI, qualifiedName);

            if (namespaceURI != null && !namespaceURI.equals(parent.getNamespaceURI())) {
                int posPrefixo = qualifiedName.indexOf(':');
                if ((posPrefixo == -1)) {
                    novo.setAttribute("xmlns", namespaceURI);
                } else {
                    String prefixo = qualifiedName.substring(0, posPrefixo);
                    novo.setAttribute("xmlns:" + prefixo, namespaceURI);
                }
                //novo.setAttribute("xmlns:" + nome.getPrefix(), namespaceURI);
            }
        }
        parent.appendChild(novo);
        return novo;
    }

    /**
     * Adiciona um elemento com valor a um elemento pai. <br>
     *
     * @param pai o elemento dentro do qual um elemento será inserido
     * @param nome o nome do elemento que será inserido
     * @param value o valor <code>String</code> do elemento adicionado
     * @return o elemento que foi adicionado
     */
    static final Element addElement(Element pai, String nome, String value) {
        if (value == null) {
            throw new IllegalArgumentException("O set do valor de "
                    + XPathToolkit.getFullPath(pai)
                    + "/"
                    + nome
                    + ": não é permitido valor null. Se for necessário um "
                    + "element empty, utilize addElement sem parâmetro valor");
        }

        Element novo;
        int pos = nome.lastIndexOf('@');
        if (pos != -1) {
            String atributo = nome.substring(pos + 1);
            if ((pos > 1) && (nome.charAt(pos - 1) == '/')) {
                pos--; //Conse a barra antes da arroba (ex: cd/@cod)
            }
            if (pos > 0) {
                nome = nome.substring(0, pos);
                novo = getElementCriando(pai.getOwnerDocument(), pai, null, nome);
            } else {
                novo = pai;
            }

            if (value.length() == 0) {
                novo.removeAttribute(atributo);
            } else {
                novo.setAttribute(atributo, value);
            }
        } else {
            novo = addElementNS(pai, null, nome);
            if (value.length() != 0) {
                Document d = pai.getOwnerDocument();
                Text txt = d.createTextNode(value);
                novo.appendChild(txt);
            }
        }

        return novo;
    }

    private static final Element getElementCriando(Document d, Node pai, String namespaceURI,
            String qualifiedName) {

        String subTrecho = null;
        int pos = qualifiedName.indexOf(SEPARADOR_ELEMENT);
        if (pos != -1) {
            if (pos == 0) {
                while (pai.getParentNode() != null) {
                    pai = pai.getParentNode();
                }
                qualifiedName = qualifiedName.substring(1);
                pos = qualifiedName.indexOf(SEPARADOR_ELEMENT);
            }
            if (pos != -1) {
                subTrecho = qualifiedName.substring(pos + 1);
                qualifiedName = qualifiedName.substring(0, pos);
            }
        }

        Node n = pai.getFirstChild();
        while ((n != null)
                && ((n.getNodeType() != Node.ELEMENT_NODE) || (!n.getNodeName().equals(
                qualifiedName)))) {
            n = n.getNextSibling();
        }

        Element e = (Element) n;
        if (e == null) {
            if (isVazio(namespaceURI)) {
                if (!isVazio(pai.getNamespaceURI()) && isVazio(pai.getPrefix())) {
                    namespaceURI = pai.getNamespaceURI();
                }
            }
            e = d.createElementNS(namespaceURI, qualifiedName);
            pai.appendChild(e);
        }

        // verifica se precisa procurar um sub-item
        if (subTrecho != null) {
            return getElementCriando(d, e, null, subTrecho);
        }

        return e;
    }

    /**
     * Verifica se a string é null ou tamanho 0.
     *
     * @param s String a ser verificada
     * @return se null ou tamanho igual a 0
     */
    private static boolean isVazio(String s) {
        return (s == null) || (s.length() == 0);
    }

    //---------------------------------------------------------
    // Método abstratos da interface MElement
    //---------------------------------------------------------

    /**
     * @see org.w3c.dom.Element#getTagName()
     */
    @Override
    public String getTagName() {
        return original_.getTagName();
    }

    /**
     * @see org.w3c.dom.Element#getAttribute(String)
     */
    @Override
    public String getAttribute(String arg0) {
        return original_.getAttribute(arg0);
    }

    /**
     * @see org.w3c.dom.Element#setAttribute(String, String)
     */
    @Override
    public void setAttribute(String arg0, String arg1) {
        original_.setAttribute(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Element#removeAttribute(String)
     */
    @Override
    public void removeAttribute(String arg0) {
        original_.removeAttribute(arg0);
    }

    /**
     * @see org.w3c.dom.Element#getAttributeNode(String)
     */
    @Override
    public Attr getAttributeNode(String arg0) {
        return original_.getAttributeNode(arg0);
    }

    /**
     * @see org.w3c.dom.Element#setAttributeNode(Attr)
     */
    @Override
    public Attr setAttributeNode(Attr arg0) {
        return original_.setAttributeNode(arg0);
    }

    /**
     * @see org.w3c.dom.Element#removeAttributeNode(Attr)
     */
    @Override
    public Attr removeAttributeNode(Attr arg0) {
        return original_.removeAttributeNode(arg0);
    }

    /**
     * @see org.w3c.dom.Element#getElementsByTagName(String)
     */
    @Override
    public NodeList getElementsByTagName(String arg0) {
        return original_.getElementsByTagName(arg0);
    }

    /**
     * @see org.w3c.dom.Element#getAttributeNS(String, String)
     */
    @Override
    public String getAttributeNS(String arg0, String arg1) {
        return original_.getAttributeNS(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Element#setAttributeNS(String, String, String)
     */
    @Override
    public void setAttributeNS(String arg0, String arg1, String arg2) {
        original_.setAttributeNS(arg0, arg1, arg2);
    }

    /**
     * @see org.w3c.dom.Element#removeAttributeNS(String, String)
     */
    @Override
    public void removeAttributeNS(String arg0, String arg1) {
        original_.removeAttributeNS(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Element#getAttributeNodeNS(String, String)
     */
    @Override
    public Attr getAttributeNodeNS(String arg0, String arg1) {
        return original_.getAttributeNodeNS(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Element#setAttributeNodeNS(Attr)
     */
    @Override
    public Attr setAttributeNodeNS(Attr arg0) {
        return original_.setAttributeNodeNS(arg0);
    }

    /**
     * @see org.w3c.dom.Element#getElementsByTagNameNS(String, String)
     */
    @Override
    public NodeList getElementsByTagNameNS(String arg0, String arg1) {
        return original_.getElementsByTagNameNS(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Element#hasAttribute(String)
     */
    @Override
    public boolean hasAttribute(String arg0) {
        return original_.hasAttribute(arg0);
    }

    /**
     * @see org.w3c.dom.Element#hasAttributeNS(String, String)
     */
    @Override
    public boolean hasAttributeNS(String arg0, String arg1) {
        return original_.hasAttributeNS(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Node#getNodeName()
     */
    @Override
    public String getNodeName() {
        return original_.getNodeName();
    }

    /**
     * @see org.w3c.dom.Node#getNodeValue()
     */
    @Override
    public String getNodeValue() {
        return original_.getNodeValue();
    }

    /**
     * @see org.w3c.dom.Node#setNodeValue(String)
     */
    @Override
    public void setNodeValue(String arg0) {
        original_.setNodeValue(arg0);
    }

    /**
     * @see org.w3c.dom.Node#getNodeType()
     */
    @Override
    public short getNodeType() {
        return original_.getNodeType();
    }

    /**
     * @see org.w3c.dom.Node#getParentNode()
     */
    @Override
    public Node getParentNode() {
        return original_.getParentNode();
    }

    /**
     * @see org.w3c.dom.Node#getChildNodes()
     */
    @Override
    public NodeList getChildNodes() {
        return original_.getChildNodes();
    }

    /**
     * @see org.w3c.dom.Node#getFirstChild()
     */
    @Override
    public Node getFirstChild() {
        return original_.getFirstChild();
    }

    /**
     * @see org.w3c.dom.Node#getLastChild()
     */
    @Override
    public Node getLastChild() {
        return original_.getLastChild();
    }

    /**
     * @see org.w3c.dom.Node#getPreviousSibling()
     */
    @Override
    public Node getPreviousSibling() {
        return original_.getPreviousSibling();
    }

    /**
     * @see org.w3c.dom.Node#getNextSibling()
     */
    @Override
    public Node getNextSibling() {
        return original_.getNextSibling();
    }

    /**
     * @see org.w3c.dom.Node#getAttributes()
     */
    @Override
    public NamedNodeMap getAttributes() {
        return original_.getAttributes();
    }

    /**
     * @see org.w3c.dom.Node#getOwnerDocument()
     */
    @Override
    public Document getOwnerDocument() {
        return original_.getOwnerDocument();
    }

    /**
     * @see org.w3c.dom.Node#insertBefore(Node, Node)
     */
    @Override
    public Node insertBefore(Node arg0, Node arg1) {
        if (arg0 instanceof MElementWrapper) {
            arg0 = ((MElementWrapper) arg0).original_;
        }
        if (arg1 instanceof MElementWrapper) {
            arg1 = ((MElementWrapper) arg0).original_;
        }
        return original_.insertBefore(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Node#replaceChild(Node, Node)
     */
    @Override
    public Node replaceChild(Node arg0, Node arg1) {
        if (arg0 instanceof MElementWrapper) {
            arg0 = ((MElementWrapper) arg0).original_;
        }
        return original_.replaceChild(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Node#removeChild(Node)
     */
    @Override
    public Node removeChild(Node arg0) {
        if (arg0 instanceof MElementWrapper) {
            arg0 = ((MElementWrapper) arg0).original_;
        }
        return original_.removeChild(arg0);
    }

    /**
     * @see org.w3c.dom.Node#appendChild(Node)
     */
    @Override
    public Node appendChild(Node arg0) {
        if (arg0 instanceof MElementWrapper) {
            arg0 = ((MElementWrapper) arg0).original_;
        }
        return original_.appendChild(arg0);
    }

    /**
     * @see org.w3c.dom.Node#hasChildNodes()
     */
    @Override
    public boolean hasChildNodes() {
        return original_.hasChildNodes();
    }

    /**
     * @see org.w3c.dom.Node#cloneNode(boolean)
     */
    @Override
    public Node cloneNode(boolean arg0) {
        return original_.cloneNode(arg0);
    }

    /**
     * @see org.w3c.dom.Node#normalize()
     */
    @Override
    public void normalize() {
        original_.normalize();
    }

    /**
     * @see org.w3c.dom.Node#isSupported(String, String)
     */
    @Override
    public boolean isSupported(String arg0, String arg1) {
        return original_.isSupported(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Node#getNamespaceURI()
     */
    @Override
    public String getNamespaceURI() {
        return original_.getNamespaceURI();
    }

    /**
     * @see org.w3c.dom.Node#getPrefix()
     */
    @Override
    public String getPrefix() {
        return original_.getPrefix();
    }

    /**
     * @see org.w3c.dom.Node#setPrefix(String)
     */
    @Override
    public void setPrefix(String arg0) {
        original_.setPrefix(arg0);
    }

    /**
     * @see org.w3c.dom.Node#getLocalName()
     */
    @Override
    public String getLocalName() {
        return original_.getLocalName();
    }

    /**
     * @see org.w3c.dom.Node#hasAttributes()
     */
    @Override
    public boolean hasAttributes() {
        return original_.hasAttributes();
    }

    //-------------------------------------------
    // Métodos para o Jdk 1.5
    //-------------------------------------------

    /**
     * @see org.w3c.dom.Element#getSchemaTypeInfo()
     */
    @Override
    public TypeInfo getSchemaTypeInfo() {
        return original_.getSchemaTypeInfo();
    }

    /**
     * @see org.w3c.dom.Element#setIdAttribute(java.lang.String, boolean)
     */
    @Override
    public void setIdAttribute(String name, boolean isId) throws DOMException {
        original_.setIdAttribute(name, isId);
    }

    /**
     * @see org.w3c.dom.Element#setIdAttributeNS(java.lang.String, java.lang.String, boolean)
     */
    @Override
    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        original_.setIdAttributeNS(namespaceURI, localName, isId);
    }

    /**
     * @see org.w3c.dom.Element#setIdAttributeNode(org.w3c.dom.Attr, boolean)
     */
    @Override
    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        original_.setIdAttributeNode(idAttr, isId);
    }

    /**
     * @see org.w3c.dom.Node#getBaseURI()
     */
    @Override
    public String getBaseURI() {
        return original_.getBaseURI();
    }

    /**
     * @see org.w3c.dom.Node#compareDocumentPosition(org.w3c.dom.Node)
     */
    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
        return original_.compareDocumentPosition(other);
    }

    /**
     * @see org.w3c.dom.Node#getTextContent()
     */
    @Override
    public String getTextContent() throws DOMException {
        return original_.getTextContent();
    }

    /**
     * @see org.w3c.dom.Node#setTextContent(java.lang.String)
     */
    @Override
    public void setTextContent(String textContent) throws DOMException {
        original_.setTextContent(textContent);

    }

    /**
     * @see org.w3c.dom.Node#isSameNode(org.w3c.dom.Node)
     */
    @Override
    public boolean isSameNode(Node other) {
        return original_.isSameNode(other);
    }

    /**
     * @see org.w3c.dom.Node#lookupPrefix(java.lang.String)
     */
    @Override
    public String lookupPrefix(String namespaceURI) {
        return original_.lookupPrefix(namespaceURI);
    }

    /**
     * @see org.w3c.dom.Node#isDefaultNamespace(java.lang.String)
     */
    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        return original_.isDefaultNamespace(namespaceURI);
    }

    /**
     * @see org.w3c.dom.Node#lookupNamespaceURI(java.lang.String)
     */
    @Override
    public String lookupNamespaceURI(String prefix) {
        return original_.lookupNamespaceURI(prefix);
    }

    /**
     * @see org.w3c.dom.Node#isEqualNode(org.w3c.dom.Node)
     */
    @Override
    public boolean isEqualNode(Node arg) {
        return original_.isEqualNode(arg);
    }

    /**
     * @see org.w3c.dom.Node#getFeature(java.lang.String, java.lang.String)
     */
    @Override
    public Object getFeature(String feature, String version) {
        return original_.getFeature(feature, version);
    }

    /**
     * @see org.w3c.dom.Node#setUserData(java.lang.String, java.lang.Object, org.w3c.dom.UserDataHandler)
     */
    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return original_.setUserData(key, data, handler);
    }

    /**
     * @see org.w3c.dom.Node#getUserData(java.lang.String)
     */
    @Override
    public Object getUserData(String key) {
        return original_.getUserData(key);
    }
    //-------------------------------------------
    // Fim Métodos para o Jdk 1.5
    //-------------------------------------------

}