package org.opensingular.form.internal.xml;

import org.junit.Test;
import org.opensingular.form.SingularFormException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class TestXMLToolkitWriter {

    @Test
    public void testPrintNodeMethods(){
        File arquivoTemp = null;
        try {
            arquivoTemp = File.createTempFile("arquivo", Long.toString(System.currentTimeMillis())+".txt");
            PrintWriter writer = new PrintWriter(arquivoTemp);

            MDocument document = MDocument.newInstance();
            MElement raiz = document.createRaiz("raiz");
            raiz.setAttributeNS("uri", "name", "value");

            raiz.addInt("inteiro", "123").setAttributeNS("uri", "name", "value");
            raiz.addElement("nome", "valor\n");

            document.createComment("comentario pra dar erro");

            XMLToolkitWriter.printDocument(writer, raiz, false, false);
            XMLToolkitWriter.printDocument(writer, raiz, false);
            XMLToolkitWriter.printDocumentIndentado(writer, raiz, false);

        } catch (Exception e) {
            throw SingularFormException.rethrow(e);
        } finally {
            arquivoTemp.delete();
        }
    }
}
