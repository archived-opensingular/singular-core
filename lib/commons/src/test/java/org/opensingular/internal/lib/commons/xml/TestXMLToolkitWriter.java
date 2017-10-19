package org.opensingular.internal.lib.commons.xml;

import net.vidageek.mirror.dsl.Mirror;
import org.junit.Assert;
import org.junit.Test;
import org.opensingular.internal.lib.commons.util.TempFileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class TestXMLToolkitWriter {

    @Test
    public void testPrintNodeMethods() throws FileNotFoundException {
        XMLMElementWriter elementWriter = new XMLMElementWriter(StandardCharsets.UTF_8);
        try (TempFileProvider tmpProvider = TempFileProvider.createForUseInTryClause(this)) {
            File arquivoTemp = tmpProvider.createTempFile(".txt");
            PrintWriter writer = new PrintWriter(arquivoTemp);

            MDocument document = MDocument.newInstance();
            MElement root = document.createRoot("raiz");
            root.setAttributeNS("uri", "name", "value");

            root.addInt("inteiro", "123").setAttributeNS("uri", "name", "value");
            root.addElement("nome", "valor\n");

            document.createComment("comentario pra dar erro");

            elementWriter.printDocument(writer, root, false, false);
            elementWriter.printDocument(writer, root, false);
            elementWriter.printDocumentIndentado(writer, root, false);
            writer.close();
        }
    }

    @Test
    public void testSerialization() throws Exception {
        XMLMElementWriter e = new XMLMElementWriter(StandardCharsets.UTF_8);
        File              f = File.createTempFile("nada", "123123");
        f.deleteOnExit();
        ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(f));
        o.writeObject(e);
        o.close();

        ObjectInputStream oi      = new ObjectInputStream(new FileInputStream(f));
        XMLMElementWriter another = (XMLMElementWriter) oi.readObject();

        Assert.assertEquals(new Mirror().on(e).get().field("charset"), new Mirror().on(another).get().field("charset"));

        f.delete();

    }
}
