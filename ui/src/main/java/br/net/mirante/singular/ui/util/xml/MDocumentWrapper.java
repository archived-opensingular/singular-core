package br.net.mirante.singular.ui.util.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

final class MDocumentWrapper extends MDocument {

    private final Document original_;

    /**
     * Constroi um MElement para ler e alterar o Element informado.
     *
     * @param original -
     */
    public MDocumentWrapper(Document original) {
        if (original == null) {
            throw new IllegalArgumentException("Document original não pode ser " + "null");
        } else if (original instanceof MDocumentWrapper) {
            original_ = ((MDocumentWrapper) original).original_;
        } else {
            original_ = original;
        }
    }

    @Override
    public DocumentType getDoctype() {
        return original_.getDoctype();
    }

    @Override
    public DOMImplementation getImplementation() {
        return original_.getImplementation();
    }

    @Override
    public Element getDocumentElement() {
        return original_.getDocumentElement();
    }

    @Override
    public Element createElement(String tagName) throws DOMException {
        return original_.createElement(tagName);
    }

    @Override
    public DocumentFragment createDocumentFragment() {
        return original_.createDocumentFragment();
    }

    @Override
    public Text createTextNode(String data) {
        return original_.createTextNode(data);
    }

    @Override
    public Comment createComment(String data) {
        return original_.createComment(data);
    }

    @Override
    public CDATASection createCDATASection(String data) throws DOMException {
        return original_.createCDATASection(data);
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
        return original_.createProcessingInstruction(target, data);
    }

    @Override
    public Attr createAttribute(String name) throws DOMException {
        return original_.createAttribute(name);
    }

    @Override
    public EntityReference createEntityReference(String name) throws DOMException {
        return original_.createEntityReference(name);
    }

    @Override
    public NodeList getElementsByTagName(String tagname) {
        return original_.getElementsByTagName(tagname);
    }

    @Override
    public Node importNode(Node importedNode, boolean deep) throws DOMException {
        return original_.importNode(importedNode, deep);
    }

    @Override
    public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
        return original_.createElementNS(namespaceURI, qualifiedName);
    }

    @Override
    public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
        return original_.createAttributeNS(namespaceURI, qualifiedName);
    }

    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return original_.getElementsByTagNameNS(namespaceURI, localName);
    }

    @Override
    public Element getElementById(String elementId) {
        return original_.getElementById(elementId);
    }

    @Override
    public String getInputEncoding() {
        return original_.getInputEncoding();
    }

    @Override
    public String getXmlEncoding() {
        return original_.getXmlEncoding();
    }

    @Override
    public boolean getXmlStandalone() {
        return original_.getXmlStandalone();
    }

    @Override
    public void setXmlStandalone(boolean xmlStandalone) throws DOMException {
        original_.setXmlStandalone(xmlStandalone);
    }

    @Override
    public String getXmlVersion() {
        return original_.getXmlVersion();
    }

    @Override
    public void setXmlVersion(String xmlVersion) throws DOMException {
        original_.setXmlVersion(xmlVersion);
    }

    @Override
    public boolean getStrictErrorChecking() {
        return original_.getStrictErrorChecking();
    }

    @Override
    public void setStrictErrorChecking(boolean strictErrorChecking) {
        original_.setStrictErrorChecking(strictErrorChecking);
    }

    @Override
    public String getDocumentURI() {
        return original_.getDocumentURI();
    }

    @Override
    public void setDocumentURI(String documentURI) {
        original_.setDocumentURI(documentURI);
    }

    @Override
    public Node adoptNode(Node source) throws DOMException {
        return original_.adoptNode(source);
    }

    @Override
    public DOMConfiguration getDomConfig() {
        return original_.getDomConfig();
    }

    @Override
    public void normalizeDocument() {
        original_.normalizeDocument();
    }

    @Override
    public Node renameNode(Node n, String namespaceURI, String qualifiedName) throws DOMException {
        return original_.renameNode(n, namespaceURI, qualifiedName);
    }

    @Override
    public String getNodeName() {
        return original_.getNodeName();
    }

    @Override
    public String getNodeValue() throws DOMException {
        return original_.getNodeValue();
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        original_.setNodeValue(nodeValue);
    }

    @Override
    public short getNodeType() {
        return original_.getNodeType();
    }

    @Override
    public Node getParentNode() {
        return original_.getParentNode();
    }

    @Override
    public NodeList getChildNodes() {
        return original_.getChildNodes();
    }

    @Override
    public Node getFirstChild() {
        return original_.getFirstChild();
    }

    @Override
    public Node getLastChild() {
        return original_.getLastChild();
    }

    @Override
    public Node getPreviousSibling() {
        return original_.getPreviousSibling();
    }

    @Override
    public Node getNextSibling() {
        return original_.getNextSibling();
    }

    @Override
    public NamedNodeMap getAttributes() {
        return original_.getAttributes();
    }

    @Override
    public Document getOwnerDocument() {
        return original_.getOwnerDocument();
    }

    @Override
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        return original_.insertBefore(newChild, refChild);
    }

    @Override
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        return original_.replaceChild(newChild, oldChild);
    }

    @Override
    public Node removeChild(Node oldChild) throws DOMException {
        return original_.removeChild(oldChild);
    }

    @Override
    public Node appendChild(Node newChild) throws DOMException {
        return original_.appendChild(newChild);
    }

    @Override
    public boolean hasChildNodes() {
        return original_.hasChildNodes();
    }

    @Override
    public Node cloneNode(boolean deep) {
        return original_.cloneNode(deep);
    }

    @Override
    public void normalize() {
        original_.normalize();
    }

    @Override
    public boolean isSupported(String feature, String version) {
        return original_.isSupported(feature, version);
    }

    @Override
    public String getNamespaceURI() {
        return original_.getNamespaceURI();
    }

    @Override
    public String getPrefix() {
        return original_.getPrefix();
    }

    @Override
    public void setPrefix(String prefix) throws DOMException {
        original_.setPrefix(prefix);
    }

    @Override
    public String getLocalName() {
        return original_.getLocalName();
    }

    @Override
    public boolean hasAttributes() {
        return original_.hasAttributes();
    }

    @Override
    public String getBaseURI() {
        return original_.getBaseURI();
    }

    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
        return original_.compareDocumentPosition(other);
    }

    @Override
    public String getTextContent() throws DOMException {
        return original_.getTextContent();
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
        original_.setTextContent(textContent);
    }

    @Override
    public boolean isSameNode(Node other) {
        return original_.isSameNode(other);
    }

    @Override
    public String lookupPrefix(String namespaceURI) {
        return original_.lookupPrefix(namespaceURI);
    }

    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        return original_.isDefaultNamespace(namespaceURI);
    }

    @Override
    public String lookupNamespaceURI(String prefix) {
        return original_.lookupNamespaceURI(prefix);
    }

    @Override
    public boolean isEqualNode(Node arg) {
        return original_.isEqualNode(arg);
    }

    @Override
    public Object getFeature(String feature, String version) {
        return original_.getFeature(feature, version);
    }

    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return original_.setUserData(key, data, handler);
    }

    @Override
    public Object getUserData(String key) {
        return original_.getUserData(key);
    }

}
