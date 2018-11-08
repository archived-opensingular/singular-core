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

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.opensingular.internal.lib.commons.util.SingularIOUtils;
import org.opensingular.internal.lib.commons.util.TempFileProvider;
import org.opensingular.lib.commons.test.AssertionsXML;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class TestXMLToolkitWriter {
    @Test
    public void testEscapesInContentAndAttributes1() {
        String expected =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root att=\"ç&quot;&apos;&gt;&lt;\">ç\"'&gt;&lt;</root>";

        assertEquals(expected, createXMLWithSpecialCharacters().toStringExato());

        String fix = StringUtils.remove(StringUtils.remove(createXMLWithSpecialCharacters().toString(), '\r'), '\n');
        assertEquals(expected, fix);
    }

    @Test
    public void testEscapesInContentAndAttributes() throws Exception {
        MElement xml = createXMLWithSpecialCharacters();
        verifyConsistencyWriteAndRead(xml);
    }

    @Test
    public void withValueBreakLine() throws Exception {
        MElement xml = MElement.newInstance("root");
        xml.setTextContent("A\nB");
        verifyConsistencyWriteAndRead(xml);
    }

    private void verifyConsistencyWriteAndRead(@Nonnull MElement xml) throws SAXException, IOException {
        MElement xml2;

        xml2 = MParser.parse(xml.toStringExato());
        new AssertionsXML(xml2).isContentEquals(xml).isEquivalentTo(xml);

        xml2 = MParser.parse(xml.toString());
        new AssertionsXML(xml2).isContentEquals(xml).isEquivalentTo(xml);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        xml.printTabulado(out);
        xml2 = MParser.parse(out.toByteArray());
        new AssertionsXML(xml2).isContentEquals(xml).isEquivalentTo(xml);

        out = new ByteArrayOutputStream();
        xml.print(out);
        xml2 = MParser.parse(out.toByteArray());
        new AssertionsXML(xml2).isContentEquals(xml).isEquivalentTo(xml);

    }

    @Nonnull
    private MElement createXMLWithSpecialCharacters() {
        String special = "ç\"\'><";
        MElement xml = MElement.newInstance("root");
        xml.setAttribute("att", special);
        xml.setTextContent(special);
        return xml;
    }


    @Test
    public void testPrintNodeMethods() throws FileNotFoundException {
        XMLMElementWriter elementWriter = new XMLMElementWriter(StandardCharsets.UTF_8);
        try (TempFileProvider tmpProvider = TempFileProvider.createForUseInTryClause(this)) {
            File arqTemp = tmpProvider.createTempFile(".txt");
            PrintWriter writer = new PrintWriter(arqTemp);

            MDocument document = MDocument.newInstance();
            MElement root = document.createRoot("raiz");
            root.setAttributeNS("uri", "name", "value");

            root.addInt("inteiro", "123").setAttributeNS("uri", "name", "value");
            root.addElement("nome", "valor\n");

            document.createComment("comentario pra dar erro");

            elementWriter.printDocument(writer, root, false);
            elementWriter.printDocumentIndentado(writer, root, false);
            writer.close();
        }
    }

    @Test
    public void testSerialization() {
        XMLMElementWriter e = new XMLMElementWriter(StandardCharsets.UTF_8);
        XMLMElementWriter another = SingularIOUtils.serializeAndDeserialize(e);

        Assertions.assertThat(another.getCharset()).isEqualTo(e.getCharset());
    }
}
