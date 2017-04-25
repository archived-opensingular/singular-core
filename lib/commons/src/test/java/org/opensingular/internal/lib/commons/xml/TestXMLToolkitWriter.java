package org.opensingular.internal.lib.commons.xml;

import org.junit.Test;
import org.opensingular.internal.lib.commons.util.TempFileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class TestXMLToolkitWriter {

    @Test
    public void testPrintNodeMethods() throws FileNotFoundException {
        try(TempFileProvider tmpProvider = TempFileProvider.createForUseInTryClause(this)) {
            File arquivoTemp = tmpProvider.createTempFile(".txt");
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
            writer.close();
        }
    }
}
