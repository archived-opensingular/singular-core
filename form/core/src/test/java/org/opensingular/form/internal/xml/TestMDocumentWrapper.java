package org.opensingular.form.internal.xml;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

public class TestMDocumentWrapper {

    @Test(expected = IllegalArgumentException.class)
    public void testCreateMDocumentWrapper(){
        MDocument document = MDocument.newInstance();
        MDocumentWrapper wrapper = new MDocumentWrapper(document);

        Assert.assertNotNull(wrapper);

        document = null;
        new MDocumentWrapper(document);
    }

    @Test
    public void testGetDoctype(){
        MDocument document = MDocument.newInstance();

        MElement pai = document.createRaiz("raiz");
        pai.addElement("nome", "joaquim");

        MElement filhos = pai.addElement("filhos");

        MDocumentWrapper wrapper = new MDocumentWrapper(document);

        wrapper.getDoctype();
    }

    @Test
    public void testMethods(){
        MDocument document = MDocument.newInstance();
        MElement pai = document.createRaiz("raiz");
        pai.addElement("nome", "joaquim");

        MDocumentWrapper wrapper = new MDocumentWrapper(document);

        Assert.assertEquals("elemento", wrapper.createElement("elemento").getNodeName());

        Assert.assertEquals("comentario", wrapper.createComment("comentario").getData());

        Assert.assertTrue(wrapper.createDocumentFragment() instanceof DocumentFragment);

        Assert.assertEquals("CDATASection", wrapper.createCDATASection("CDATASection").getData());

        ProcessingInstruction processingInstruction = wrapper.createProcessingInstruction("raiz", "information");
        Assert.assertEquals("raiz", processingInstruction.getTarget());
        Assert.assertEquals("information", processingInstruction.getData());

        Assert.assertEquals("attribute", wrapper.createAttribute("attribute").getName());

        Assert.assertEquals("entity", wrapper.createEntityReference("entity").getNodeName());

        Assert.assertEquals("qualifiedName", wrapper.createAttributeNS("uri", "qualifiedName").getLocalName());

        Assert.assertTrue(wrapper.getDomConfig() instanceof DOMConfiguration);

        Assert.assertEquals("#document", wrapper.getNodeName());

        Assert.assertEquals(Node.DOCUMENT_NODE, wrapper.getNodeType());

        Assert.assertNull(wrapper.getBaseURI());

        Assert.assertNull(wrapper.lookupPrefix("test"));

        Assert.assertNull(wrapper.lookupNamespaceURI("test"));

        Assert.assertFalse(wrapper.isDefaultNamespace("test"));

        Assert.assertFalse(wrapper.isSupported("feature", "1.0"));

        Assert.assertNull(wrapper.getXmlEncoding());

        Assert.assertNull(wrapper.getInputEncoding());

        Assert.assertTrue(wrapper.getImplementation() instanceof DOMImplementation);

        Assert.assertTrue(wrapper.getDocumentElement() instanceof Element);

        Assert.assertNull(wrapper.getElementById(""));

        Assert.assertNull(wrapper.getOwnerDocument());

        Assert.assertFalse(wrapper.hasAttributes());

        Assert.assertNull(wrapper.getTextContent());

        Assert.assertNull(wrapper.getFeature("feature", "1.0"));

        Assert.assertNull(wrapper.getUserData("key"));
    }

    @Test
    public void testNodeMethods(){
        MDocument document = MDocument.newInstance();
        MElement pai = document.createRaiz("raiz");
        pai.addElement("nome", "joaquim");

        MElement element = pai.addElement("sobrenome", "tadeu da cruz");

        MDocumentWrapper wrapper = new MDocumentWrapper(document);

        Assert.assertEquals(0, wrapper.getElementsByTagNameNS("", "").getLength());

        Assert.assertEquals("raiz", wrapper.getLastChild().getNodeName());

        Assert.assertEquals(1, wrapper.getChildNodes().getLength());

        Assert.assertTrue(wrapper.hasChildNodes());

        Assert.assertFalse(wrapper.isSameNode(element));

        Assert.assertFalse(wrapper.isEqualNode(pai));
    }

    @Test
    public void testGetsAndSets(){
        MDocument document = MDocument.newInstance();
        MElement pai = document.createRaiz("raiz");
        pai.addElement("nome", "joaquim");

        MDocumentWrapper wrapper = new MDocumentWrapper(document);

        wrapper.setXmlStandalone(true);
        Assert.assertTrue(wrapper.getXmlStandalone());

        wrapper.setStrictErrorChecking(true);
        Assert.assertTrue(wrapper.getStrictErrorChecking());

        document.setNodeValue("documentNodeValue"); // como é document_type, nao tem sentido mudar esse valor
        Assert.assertNull(wrapper.getNodeValue());

        wrapper.setDocumentURI("uriDocument");
        Assert.assertEquals("uriDocument", wrapper.getDocumentURI());

    }

    @Test(expected = DOMException.class)
    public void testGetAndSetPrefix(){
        MDocument document = MDocument.newInstance();
        document.createRaiz("raiz");

        MDocumentWrapper wrapper = new MDocumentWrapper(document);

        Assert.assertNull(wrapper.getPrefix());
        wrapper.setPrefix("prefix/posfix");
    }

    @Test(expected = DOMException.class)
    public void testGetAndSetXMlVersion(){
        MDocument document = MDocument.newInstance();
        MElement pai = document.createRaiz("raiz");
        pai.addElement("nome", "joaquim");

        MDocumentWrapper wrapper = new MDocumentWrapper(document);

        Assert.assertEquals("1.0", wrapper.getXmlVersion());
        wrapper.setXmlVersion("2.0");
    }

    @Test
    public void testAdoptNode(){
        MDocument document = MDocument.newInstance();
        MElement pai = document.createRaiz("raiz");
        MDocumentWrapper wrapper = new MDocumentWrapper(document);

        MElement element = MElement.newInstance("elemento");
        element.setNodeValue("valor");

        Assert.assertNull(wrapper.adoptNode(element));
    }

}
