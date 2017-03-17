package org.opensingular.form.internal.xml;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

public class TestMDocument {

    @Test
    public void testCreateMDocument(){
        MElement test = MElement.newInstance("raiz");
        test.addElement("filho");

        Assert.assertNotNull(test.getMDocument());

        Document testDocument = null;
        MDocument testNull = MDocument.toMDocument(testDocument);
        Assert.assertNull(testNull);
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
    }
}
