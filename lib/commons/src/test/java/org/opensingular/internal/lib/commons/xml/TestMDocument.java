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
import org.opensingular.lib.commons.base.SingularException;
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
        MElement raiz = document.createRaiz("raiz");

        MDocument documentCopyInstance = MDocument.toMDocument((Node) document);
        Assert.assertEquals(document, documentCopyInstance);

        MDocument documentClone = MDocument.toMDocument(document.cloneNode(true));
        Assert.assertEquals(document.getFirstChild().getNodeName(), documentClone.getFirstChild().getNodeName());

        Node node = null;
        Assert.assertNull(MDocument.toMDocument(node));

        MDocument.toMDocument((Node) raiz); // generate document from nodeType diferent of documentNode
    }

    @Test
    public void testGetDocumentFromElement(){
        MElement test = MElement.newInstance("raiz");
        test.addElement("filho");

        Assert.assertTrue(test.getMDocument() instanceof Document);
    }

    @Test
    public void testCreateRaiz(){
        MDocument document = MDocument.newInstance();

        String raizGeradaDocument = document.createRaiz("raiz").toString();
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
