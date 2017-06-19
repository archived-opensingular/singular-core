/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.internal.lib.commons.xml;

import org.json.JSONWriter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;

public class JSONToolkit implements MElementWriter {

    public JSONToolkit() {
    }

    private static boolean isObject(Node node) {
        return node.getFirstChild() != null && node.getFirstChild().getNodeType() == Node.ELEMENT_NODE;
    }

    private static boolean isProperty(Node node) {
        return node.getFirstChild() != null && node.getFirstChild().getNodeType() == Node.TEXT_NODE;
    }

    private void printJSON(PrintWriter out, Element e) {
        final JSONWriter jsonWriter = new JSONWriter(out);
        jsonWriter.object();
        printJSON(jsonWriter, e);
        jsonWriter.endObject();
    }

    private void printJSON(JSONWriter jsonWriter, Node node) {
        if (isObject(node)) {
            jsonWriter.key(node.getNodeName());
            jsonWriter.object();
            final NodeList nodes = node.getChildNodes();
            for (int index = 0; index < nodes.getLength(); index += 1) {
                printJSON(jsonWriter, nodes.item(index));
            }
            jsonWriter.endObject();
        } else if (isProperty(node)) {
            jsonWriter.key(node.getNodeName());
            jsonWriter.value(node.getTextContent());
        }
    }

    @Override
    public void printDocument(PrintWriter out, Element e, boolean printHeader) {
        printJSON(out, e);
    }

    @Override
    public void printDocument(PrintWriter out, Element e, boolean printHeader, boolean converteEspeciais) {
        printJSON(out, e);
    }

    @Override
    public void printDocumentIndentado(PrintWriter out, Element e, boolean printHeader) {
        printJSON(out, e);
    }
}