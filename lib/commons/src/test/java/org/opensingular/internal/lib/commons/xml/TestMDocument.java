package org.opensingular.internal.lib.commons.xml;

import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.base.SingularException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class TestMDocument {

    @Test
    public void testToMDocumentByDocument(){
        Document testDocument = null;
        MDocument testNull = MDocument.toMDocument(testDocument);
        Assert.assertNull(testNull);

        Document documentByMDocument = MDocument.newInstance();
        Assert.assertTrue(MDocument.toMDocument(documentByMDocument) instanceof MDocument);
    }

    @Test(expected = SingularException.class)
    public void testToMDocumentByMDocument(){
        MDocument.toMDocument(MDocument.newInstance());
    }

    @Test(expected = SingularException.class)
    public void testToMDocumentByNode(){
        MDocument document = MDocument.newInstance();
        MElement root = document.createRoot("raiz");

        MDocument documentCopyInstance = MDocument.toMDocument((Node) document);
        Assert.assertEquals(document, documentCopyInstance);

        MDocument documentClone = MDocument.toMDocument(document.cloneNode(true));
        Assert.assertEquals(document.getFirstChild().getNodeName(), documentClone.getFirstChild().getNodeName());

        Node node = null;
        Assert.assertNull(MDocument.toMDocument(node));

        MDocument.toMDocument((Node) root); // generate document from nodeType diferent of documentNode
    }

    @Test
    public void testGetDocumentFromElement(){
        MElement test = MElement.newInstance("raiz");
        test.addElement("filho");

        Assert.assertTrue(test.getMDocument() instanceof Document);
    }

    @Test
    public void testCreateRoot(){
        MDocument document = MDocument.newInstance();

        String raizGeradaDocument = document.createRoot("raiz").toString();
        String raizGeradaMElement = MElement.newInstance("raiz").toString();

        Assert.assertEquals(raizGeradaMElement, raizGeradaDocument);

        MElement mElementNS = document.createMElementNS("namespace", "nomeQualificado");
        MElement element = MElement.newInstance("namespace", "nomeQualificado");
        Assert.assertEquals(element.toString(), mElementNS.toString());

        MElement mElementNSWithBar = document.createMElementNS("namespace", "nomeQualificado/filho");
        Assert.assertEquals("nomeQualificado", mElementNSWithBar.getNodeName());
        Assert.assertEquals("filho", mElementNSWithBar.getNode("filho").getNodeName());

        document.getParentNode();
    }

    @Test(expected =  SingularException.class)
    public void testCreateElementNSThrowException(){
        MDocument document = MDocument.newInstance();
        document.createMElementNS("qualquerUm", "/testeNome");
    }

    @Test
    public void testCreateElementNS(){
        MDocument document = MDocument.newInstance();
        MElement element = document.createMElementNS("qualquerUm", "testeNome:essecodigoteste");

        Assert.assertEquals("testeNome", element.getPrefix());
        Assert.assertEquals("essecodigoteste", element.getLocalName());
        Assert.assertEquals("testeNome:essecodigoteste", element.getNodeName());
    }
}
