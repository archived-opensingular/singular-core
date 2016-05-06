/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.util.json;

import org.json.JSONWriter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.PrintWriter;

public class JSONToolkit {

    public static void printJSON(PrintWriter out, Element e) {
        final JSONWriter jsonWriter = new JSONWriter(out);
        jsonWriter.object();
        printJSON(jsonWriter, e);
        jsonWriter.endObject();
    }

    private static void printJSON(JSONWriter jsonWriter, Node node) {
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

    private static boolean isObject(Node node) {
        return node.getFirstChild() != null && node.getFirstChild().getNodeType() == Node.ELEMENT_NODE;
    }

    private static boolean isProperty(Node node) {
        return node.getFirstChild() != null && node.getFirstChild().getNodeType() == Node.TEXT_NODE;
    }

}