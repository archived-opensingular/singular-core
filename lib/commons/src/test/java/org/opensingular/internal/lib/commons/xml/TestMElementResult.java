/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.internal.lib.commons.xml;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;

import javax.annotation.Nonnull;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TestMElementResult {

    @Test
    public void testCreateObject(){
        MElement element = MElement.newInstance("raiz");

        MElementResult result = new MElementResult(element);
        Assert.assertNotNull(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createWithElementNull(){
        new MElementResult((MElement) null);
    }

    @Test
    public void createWithNodeList(){
        MElement parent = MElement.newInstance("raiz");
        parent.addElement("nome", "joaquim");
        parent.addElement("idade", "10");

        MElementResult result = new MElementResult(parent.getChildNodes());

        assertEquals(2, result.count());

        assertTrue(result.hasNext());
        assertTrue(result.next());
        assertTrue(result.next());
        Assert.assertFalse(result.next());
        Assert.assertFalse(result.hasNext());
    }

    @Test
    public void testAttributesMethods() {
        MElement parent = MElement.newInstance("raiz");
        parent.addElement("nome", "joaquim");
        parent.addElement("idade", "10");

        MElementResult result = new MElementResult(parent.getChildNodes());
        result.next();

        result.setAttribute("atr", "val");
        assertEquals("val", result.getAttribute("atr"));
        assertTrue(result.hasAttributes());
        assertTrue(result.hasAttribute("atr"));

        result.removeAttribute("atr");
        assertEquals("", result.getAttribute("atr"));


        result.setAttribute("attr", "valor");
        Attr attr = result.getAttributeNode("attr");

        assertEquals("valor", attr.getValue());

        result.setAttributeNode(attr);
        result.removeAttributeNode(attr);

        assertEquals("", result.getAttribute("attr"));
        assertEquals(0, result.getElementsByTagName("arg1").getLength());

        assertEquals(0, result.getAttributes().getLength());
    }

    @Test
    public void testAttrNSMethods(){
        MElement parent = MElement.newInstance("raiz");
        parent.addElement("nome", "joaquim");

        MElementResult result = new MElementResult(parent.getChildNodes());
        result.next();

        result.setAttributeNS("arg1", "arg2", "arg3");
        assertEquals("arg3", result.getAttributeNS("arg1", "arg2"));
        assertTrue(result.hasAttributeNS("arg1", "arg2"));

        Attr attributeNodeNS = result.getAttributeNodeNS("arg1", "arg2");
        assertEquals("arg3", attributeNodeNS.getValue());

        result.removeAttributeNS("arg1", "arg2");
        assertEquals("", result.getAttributeNS("arg1", "arg2"));

        assertEquals(0, result.getElementsByTagNameNS("arg0", "arg2").getLength());
    }

    @Test
    public void testNodeMethods(){
        MElement parent = MElement.newInstance("raiz");
        parent.addElement("nome", "joaquim");
        parent.addElement("nome", "joao");
        parent.addElement("idade", "10");

        MElementResult result = new MElementResult(parent.getChildNodes());
        result.next();

        result.setNodeValue("valor");
        assertEquals("nome", result.getNodeName());
        Assert.assertNull(result.getNodeValue());

        assertEquals("raiz", result.getParentNode().getLocalName());

        assertEquals("joaquim", result.getFirstChild().getNodeValue());
        assertTrue(result.hasChildNodes());

        assertEquals(1, result.getChildNodes().getLength());

        assertEquals("joao", result.getNextSibling().getLastChild().getNodeValue());

        assertEquals("joaquim", result.getLastChild().getNodeValue());

        assertEquals("nome", result.cloneNode(true).getNodeName());

        assertEquals("#document", result.getOwnerDocument().getNodeName());

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
        assertEquals("novo valor", result.getCurrent().getTextContent());

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
        assertEquals("nome", result.getLocalName());
        assertEquals("joaquim", result.getTextContent());

        Assert.assertFalse(result.isSameNode(idade));
        Assert.assertFalse(result.isEqualNode(idade));

        Assert.assertFalse(result.isDefaultNamespace("test"));
    }

    @Test
    public void testIterator(){
        MElement parent = createIteratorTestXml();
        MElementResult result = new MElementResult(parent.getChildNodes());
        Iterator<MElement> iterator = result.iterator();

        assertTrue(iterator.hasNext());

        assertEquals("nome", iterator.next().getNodeName());

        iterator.remove();

        iterator.next();
        assertTrue(iterator.hasNext());
        assertEquals("idade", iterator.next().getNodeName());
    }

    @Nonnull
    private MElement createIteratorTestXml() {
        MElement parent = MElement.newInstance("raiz");
        parent.addElement("nome", "joaquim");
        parent.addElement("nome", "joao");
        parent.addElement("idade", "10");
        return parent;
    }

    @Test
    public void testHasNextAfterFinish() {
        MElement parent = createIteratorTestXml();
        MElementResult result = new MElementResult(parent.getChildNodes());
        int count = 0;
        for (MElement e : result) {
            assertNotNull(e);
            count++;
        }
        assertEquals(3, count);
        assertFalse(result.iterator().hasNext());
        Assertions.assertThatThrownBy(() -> result.iterator().next()).isExactlyInstanceOf(IllegalStateException.class);

        parent = createIteratorTestXml();
        Iterator<MElement> it = new MElementResult(parent.getChildNodes()).iterator();
        assertTrue(it.hasNext());
        assertNotNull(it.next());
        assertTrue(it.hasNext());
        assertNotNull(it.next());
        assertTrue(it.hasNext());
        assertNotNull(it.next());
        assertFalse(it.hasNext());
        Assertions.assertThatThrownBy(it::next).isExactlyInstanceOf(IllegalStateException.class);
    }
}
