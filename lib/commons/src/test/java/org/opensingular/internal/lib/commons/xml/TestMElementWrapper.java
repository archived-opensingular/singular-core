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

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import java.nio.charset.StandardCharsets;

public class TestMElementWrapper {

    @Test(expected = NullPointerException.class)
    public void testConstructNullException(){
        new MElementWrapper((Element) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCopyElementNullException(){
        MElement element = MElement.newInstance("raiz");
        MElementWrapper.copyElement(element, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCopyElementWithNewNameException(){
        MElement element = MElement.newInstance("raiz");
        MElementWrapper.copyElement(element, null, "valor");
    }

    @Test
    public void testCopyElementWithNewNameNullException(){
        MElement element = MElement.newInstance("raiz");
        MElement child  = element.addElement("filho", "filhoVal");
        Element newName = MElementWrapper.copyElement(element, child, "novoNome");
        Assert.assertEquals("novoNome", newName.getNodeName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToBase64(){
        Assert.assertNull(MElementWrapper.toBASE64(null));
        MElementWrapper.toBASE64(null, StandardCharsets.UTF_8);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFromBase64() {
        Assert.assertNull(MElementWrapper.fromBASE64(null));
        MElementWrapper.fromBASE64(null, null);
    }

    @Test(expected = DOMException.class)
    public void testAddElementNS(){
        MElement element = MElement.newInstance("raiz");
        MElement child = element.addElement("filho", "filhoVal");

        Element option2 = MElementWrapper.addElementNS(child, "notEmpty", "notEmpty");
        Assert.assertEquals("notEmpty", option2.getNodeName());

        MElementWrapper.addElementNS(child, "notEmpty", "/notEmpty");
    }

    @Test
    public void testAtrNode(){
        MElement element = MElement.newInstance("raiz");
        element.addElement("filho", "filhoVal");

        MElementWrapper wrapper = new MElementWrapper(element);

        wrapper.setAttribute("atrr", "val");

        Attr atrr = wrapper.getAttributeNode("atrr");
        Assert.assertEquals("atrr", atrr.getNodeName());

        atrr.setNodeValue("novoValor");
        Assert.assertEquals("novoValor", wrapper.getAttributeNode("atrr").getNodeValue());

        wrapper.setIdAttributeNode(atrr, false);

        wrapper.setIdAttribute("atrr", false);

        Assert.assertFalse(atrr.isId());

        wrapper.setAttributeNode(atrr);
        Assert.assertTrue(wrapper.hasAttribute("atrr"));

        wrapper.removeAttributeNode(atrr);
        Assert.assertNull(wrapper.getAttributeNode("atrr"));
    }

    @Test
    public void testAtrsNS(){
        MElement element = MElement.newInstance("raiz");
        element.addElement("filho", "filhoVal");

        MElementWrapper wrapper = new MElementWrapper(element);

        Assert.assertEquals(0, wrapper.getElementsByTagName("empty").getLength());

        wrapper.setAttributeNS("arg0", "arg1", "arg2");
        Assert.assertEquals("arg2", wrapper.getAttributeNS("arg0", "arg1"));

        wrapper.setIdAttributeNS("arg0", "arg1", false);

        Assert.assertFalse(wrapper.getAttributeNodeNS("arg0", "arg1").isId());

        wrapper.removeAttributeNS("arg0", "arg1");
        Assert.assertEquals("", wrapper.getAttributeNS("arg0", "arg1"));
    }

    @Test
    public void testAttrNodeNs(){
        MElement element = MElement.newInstance("raiz");
        element.addElement("filho", "filhoVal");

        MElementWrapper wrapper = new MElementWrapper(element);

        wrapper.setAttributeNS("arg0", "arg1", "arg2");

        Attr attributeNodeNS = wrapper.getAttributeNodeNS("arg0", "arg1");
        Assert.assertEquals("arg2", attributeNodeNS.getValue());

        attributeNodeNS.setValue("novoValor");
        wrapper.setAttributeNodeNS(attributeNodeNS);
        Assert.assertEquals("novoValor", wrapper.getAttributeNodeNS("arg0", "arg1").getValue());

        Assert.assertTrue(wrapper.hasAttributeNS("arg0", "arg1"));

        wrapper.removeAttributeNode(attributeNodeNS);
        Assert.assertEquals("", wrapper.getAttributeNS("arg0", "arg1"));

        Assert.assertEquals(0, wrapper.getElementsByTagNameNS("arg0", "arg1").getLength());
    }

    @Test(expected = DOMException.class)
    public void testSomeMethods(){
        MElement element = MElement.newInstance("raiz");
        MElement child = element.addElement("filho", "filhoVal");

        MElementWrapper wrapper = new MElementWrapper(child);
        Assert.assertFalse(wrapper.isSupported("feature", "1.0"));

        Assert.assertNull(wrapper.getBaseURI());

        Assert.assertNull(wrapper.getFeature("feature", "1.0"));

        Assert.assertNull(wrapper.getUserData("data"));

        Assert.assertNull(wrapper.lookupPrefix("prefix"));

        Assert.assertNull(wrapper.lookupNamespaceURI("uri"));

        Assert.assertEquals("filhoVal", wrapper.getTextContent());

        Assert.assertFalse(wrapper.isSameNode(element));

        Assert.assertFalse(wrapper.isEqualNode(element));

        Assert.assertEquals("filho", wrapper.cloneNode(false).getNodeName());

        Assert.assertFalse(wrapper.isDefaultNamespace("namespace"));

        Assert.assertNotNull(wrapper.getSchemaTypeInfo());

        wrapper.setPrefix("");
        Assert.assertNull(wrapper.getPrefix());

        wrapper.normalize();

        wrapper.compareDocumentPosition(element);
    }
}
