package org.opensingular.internal.lib.commons.xml;

import org.junit.Test;
import org.opensingular.internal.lib.commons.util.TempFileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TestXMLToolkitWriter {

    @Test
    public void testPrintNodeMethods() throws FileNotFoundException {
        XMLMElementWriter elementWriter = new XMLMElementWriter(StandardCharsets.UTF_8);
        try(TempFileProvider tmpProvider = TempFileProvider.createForUseInTryClause(this)) {
            File arquivoTemp = tmpProvider.createTempFile(".txt");
            PrintWriter writer = new PrintWriter(arquivoTemp);

            MDocument document = MDocument.newInstance();
            MElement raiz = document.createRaiz("raiz");
            raiz.setAttributeNS("uri", "name", "value");

            raiz.addInt("inteiro", "123").setAttributeNS("uri", "name", "value");
            raiz.addElement("nome", "valor\n");

            document.createComment("comentario pra dar erro");

            elementWriter.printDocument(writer, raiz, false, false);
            elementWriter.printDocument(writer, raiz, false);
            elementWriter.printDocumentIndentado(writer, raiz, false);
            writer.close();
        }
    }
}
