package org.opensingular.internal.lib.commons.xml;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;

import java.util.Iterator;

public class TestMElementResult {

    @Test
    public void testCreateObject(){
        MElement element = MElement.newInstance("raiz");

        MElementResult result = new MElementResult(element);
        Assert.assertNotNull(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithElementNull(){
        MElement element = null;
        new MElementResult(element);
    }

    @Test
    public void createWithNodeList(){
        MElement parent = MElement.newInstance("raiz");
        parent.addElement("nome", "joaquim");
        parent.addElement("idade", "10");

        MElementResult result = new MElementResult(parent.getChildNodes());

        Assert.assertEquals(2, result.count());

        Assert.assertTrue(result.hasNext());
        Assert.assertTrue(result.next());
        Assert.assertTrue(result.next());
        Assert.assertFalse(result.next());
        Assert.assertFalse(result.hasNext());
    }

    @Test
    public void testAtributesMethods(){
        MElement parent = MElement.newInstance("raiz");
        parent.addElement("nome", "joaquim");
        parent.addElement("idade", "10");

        MElementResult result = new MElementResult(parent.getChildNodes());
        result.next();

        result.setAttribute("atr", "val");
        Assert.assertEquals("val", result.getAttribute("atr"));
        Assert.assertTrue(result.hasAttributes());
        Assert.assertTrue(result.hasAttribute("atr"));

        result.removeAttribute("atr");
        Assert.assertEquals("", result.getAttribute("atr"));


        result.setAttribute("attr", "valor");
        Attr attr = result.getAttributeNode("attr");

        Assert.assertEquals("valor", attr.getValue());

        result.setAttributeNode(attr);
        result.removeAttributeNode(attr);

        Assert.assertEquals("", result.getAttribute("attr"));
        Assert.assertEquals(0, result.getElementsByTagName("arg1").getLength());

        Assert.assertEquals(0, result.getAttributes().getLength());
    }

    @Test
    public void testAttrNSMethods(){
        MElement parent = MElement.newInstance("raiz");
        parent.addElement("nome", "joaquim");

        MElementResult result = new MElementResult(parent.getChildNodes());
        result.next();

        result.setAttributeNS("arg1", "arg2", "arg3");
        Assert.assertEquals("arg3", result.getAttributeNS("arg1", "arg2"));
        Assert.assertTrue(result.hasAttributeNS("arg1", "arg2"));

        Attr attributeNodeNS = result.getAttributeNodeNS("arg1", "arg2");
        Assert.assertEquals("arg3", attributeNodeNS.getValue());

        result.removeAttributeNS("arg1", "arg2");
        Assert.assertEquals("", result.getAttributeNS("arg1", "arg2"));

        Assert.assertEquals(0, result.getElementsByTagNameNS("arg0", "arg2").getLength());
    }

    @Test
    public void testNodeMethods(){
        MElement parent = MElement.newInstance("raiz");
        MElement name = parent.addElement("nome", "joaquim");
        parent.addElement("nome", "joao");
        parent.addElement("idade", "10");

        MElementResult result = new MElementResult(parent.getChildNodes());
        result.next();

        result.setNodeValue("valor");
        Assert.assertEquals("nome", result.getNodeName());
        Assert.assertNull(result.getNodeValue());

        Assert.assertEquals("raiz", result.getParentNode().getLocalName());

        Assert.assertEquals("joaquim", result.getFirstChild().getNodeValue());
        Assert.assertTrue(result.hasChildNodes());

        Assert.assertEquals(1, result.getChildNodes().getLength());

        Assert.assertEquals("joao", result.getNextSibling().getLastChild().getNodeValue());

        Assert.assertEquals("joaquim", result.getLastChild().getNodeValue());

        Assert.assertEquals("nome", result.cloneNode(true).getNodeName());

        Assert.assertEquals("#document", result.getOwnerDocument().getNodeName());

        Assert.assertNull(result.getPreviousSibling());

        result.normalize();

    }

    @Test(expected = DOMException.class)
    public void testCompareDocumentPosition(){
        MElement parent = MElement.newInstance("raiz");
        MElement nome = parent.addElement("nome", "joaquim");

        MElementResult result = new MElementResult(parent.getChildNodes());
        result.next();
        result.compareDocumentPosition(nome);
    }

    @Test
    public void testSetTextContentAndSetIdAttribute(){
        MElement parent = MElement.newInstance("raiz");
        parent.addElement("nome", "joaquim");

        MElementResult result = new MElementResult(parent.getChildNodes());
        result.next();
        result.setTextContent("novo valor");
        Assert.assertEquals("novo valor", result.getCurrent().getTextContent());

        result.setAttributeNS("arg0", "arg1", "arg2");
        Attr attributeNodeNS = result.getAttributeNodeNS("arg0", "arg1");

        result.setIdAttributeNode(attributeNodeNS, false);
        Assert.assertFalse(attributeNodeNS.isId());

        result.setAttribute("arg0", "arg1");
        result.setIdAttribute("arg0", false);
        Assert.assertFalse(result.getAttributeNode("arg0").isId());

        result.setPrefix("");
        Assert.assertNull(result.getPrefix());
    }

    @Test
    public void testSomeMethods(){
        MElement parent = MElement.newInstance("raiz");
        parent.addElement("nome", "joaquim");
        parent.addElement("nome", "joao");
        MElement idade = parent.addElement("idade", "10");

        MElementResult result = new MElementResult(parent.getChildNodes());
        result.next();

        Assert.assertNull(result.getNamespaceURI());
        Assert.assertNull(result.getBaseURI());
        Assert.assertNull(result.getPrefix());
        Assert.assertNull(result.getSchemaTypeInfo().getTypeNamespace());
        Assert.assertNull(result.lookupNamespaceURI("uri"));
        Assert.assertNull(result.lookupPrefix("uri"));
        Assert.assertNull(result.getFeature("feature", "1.0"));
        Assert.assertNull(result.getUserData("key"));

        Assert.assertFalse(result.isSupported("feature", "1.0"));
        Assert.assertEquals("nome", result.getLocalName());
        Assert.assertEquals("joaquim", result.getTextContent());

        Assert.assertFalse(result.isSameNode(idade));
        Assert.assertFalse(result.isEqualNode(idade));

        Assert.assertFalse(result.isDefaultNamespace("test"));
    }

    @Test
    public void testIterator(){
        MElement parent = MElement.newInstance("raiz");
        parent.addElement("nome", "joaquim");
        parent.addElement("nome", "joao");
        parent.addElement("idade", "10");

        MElementResult result = new MElementResult(parent.getChildNodes());
        Iterator<MElement> iterator = result.iterator();

        Assert.assertTrue(iterator.hasNext());

        Assert.assertEquals("nome", iterator.next().getNodeName());

        iterator.remove();

        iterator.next();
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("idade", iterator.next().getNodeName());
    }
}
