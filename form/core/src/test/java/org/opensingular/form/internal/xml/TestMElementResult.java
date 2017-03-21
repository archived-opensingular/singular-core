package org.opensingular.form.internal.xml;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;

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

    @Test(expected = IllegalArgumentException.class)
    public void createWithNodeListNull(){
        NodeList nodeList = null;
        new MElementResult(nodeList);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithElementNullAndXpath(){
        new MElementResult(null, "");
    }

    @Test
    public void createWithNodeList(){
        MElement pai = MElement.newInstance("raiz");
        pai.addElement("nome", "joaquim");
        pai.addElement("idade", "10");

        MElementResult result = new MElementResult(pai.getChildNodes());

        Assert.assertEquals(2, result.count());

        Assert.assertTrue(result.hasNext());
        Assert.assertTrue(result.next());
        Assert.assertTrue(result.next());
        Assert.assertFalse(result.next());
        Assert.assertFalse(result.hasNext());
    }

    @Test
    public void testAtributesMethods(){
        MElement pai = MElement.newInstance("raiz");
        pai.addElement("nome", "joaquim");
        pai.addElement("idade", "10");

        MElementResult result = new MElementResult(pai.getChildNodes());
        result.next();

        result.setAttribute("atr", "val");
        Assert.assertEquals("val", result.getAttribute("atr"));
        Assert.assertTrue(result.hasAttributes());
        Assert.assertTrue(result.hasAttribute("atr"));

        result.removeAttribute("atr");
        Assert.assertEquals("", result.getAttribute("atr"));
    }

    @Test
    public void testAttrNSMethods(){
        MElement pai = MElement.newInstance("raiz");
        pai.addElement("nome", "joaquim");

        MElementResult result = new MElementResult(pai.getChildNodes());
        result.next();

        result.setAttributeNS("arg1", "arg2", "arg3");
        Assert.assertEquals("arg3", result.getAttributeNS("arg1", "arg2"));
        Assert.assertTrue(result.hasAttributeNS("arg1", "arg2"));

//        result.removeAttributeNS("arg1", "arg2");
//        Assert.assertEquals("", result.getAttributeNS("arg1", "arg2"));

        result.getAttributeNodeNS("", "");
        // TODO terminar metodos com NS
        result.getAttributeNS("arg1", "arg2");
    }

    @Test
    public void testNodeMethods(){
        MElement pai = MElement.newInstance("raiz");
        pai.addElement("nome", "joaquim");
        pai.addElement("idade", "10");

        MElementResult result = new MElementResult(pai.getChildNodes());
        result.next();

        result.setNodeValue("valor");
        Assert.assertEquals("nome", result.getNodeName());
        Assert.assertNull(result.getNodeValue());

        Assert.assertEquals("raiz", result.getParentNode().getLocalName());

        Assert.assertEquals("joaquim", result.getFirstChild().getNodeValue());
        Assert.assertTrue(result.hasChildNodes());
    }
}
