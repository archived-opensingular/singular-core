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

import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.util.Loggable;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

/**
 * Creation date: (24/04/2000 10:34:52)
 *
 * @author Daniel Bordin
 */

public class XMLMElementWriter extends AbstractToolkitWriter implements Loggable {

    /**
     * Define o tamanho da tabulação
     */
    private static final String SPACE = "    ";
    /**
     * Tabulações predefinidas p/ evitar montagem constante de string (cache)
     */


    private final Charset charset;


    /**
     * Esconde o construtor por ser uma classe utilitária
     */
    XMLMElementWriter(Charset charset) {
        this.charset = charset;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeObject(charset.name());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Charset charset = Charset.forName((String) in.readObject());
        try {
            Field f = this.getClass().getDeclaredField("charset");
            f.setAccessible(true);
            f.set(this, charset);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            getLogger().error(e.getMessage(), e);
        }
    }

    //printAttributes acrescentado por Joao Rafael
    private void printAttributes(PrintWriter out, Element e) {
        NamedNodeMap map = e.getAttributes();
        int          l   = map.getLength();
        for (int i = 0; i < l; i++) {
            Attr a = (Attr) map.item(i);
            out.print(" ");
            out.print(a.getName());
            out.print("=\"");
            out.print(a.getValue());
            out.print("\"");
        }
    }

    private void printAttributes(PrintWriter out, Element e, boolean htmlEncodeReserverdCharacters) {
        NamedNodeMap map = e.getAttributes();
        int          l   = map.getLength();
        for (int i = 0; i < l; i++) {
            Attr a = (Attr) map.item(i);
            out.print(" ");
            out.print(a.getName());
            out.print("=\"");
            String texto = a.getValue();
            if (htmlEncodeReserverdCharacters) {
                printConverteCaracteresEspeciais(out, texto.toCharArray());
            } else {
                out.print(texto);
            }
            out.print("\"");
        }
    }

    /**
     * Escreve o XML de forma que um eventual parse gere o mesmo XML.
     * Para impressões mais legíveis utilize printTabulado().
     *
     * @param out         saída destino
     * @param e           Elemento a partir do qual será impresso.
     * @param printHeader Se true, adiciona string de indentificação de arquivo
     *                    XML. Se false, depois não será possível fazer parse
     *                    do resultado sem informaçoes complementares (header).
     */
    @Override
    public void printDocument(PrintWriter out, Element e, boolean printHeader) {
        if (printHeader) {
            printHeader(out);
        }
        printElement(out, e);
    }

    /**
     * Escreve o XML de forma que um eventual parse gere o mesmo XML.
     * Para impressões mais legíveis utilize printTabulado().
     *
     * @param out               saída destino
     * @param e                 Elemento a partir do qual será impresso.
     * @param printHeader       Se true, adiciona string de indentificação de arquivo
     *                          XML. Se false, depois não será possível fazer parse
     *                          do resultado sem informaçoes complementares (header).
     * @param converteEspeciais converte os caracteres de escape.
     */
    @Override
    public void printDocument(PrintWriter out, Element e, boolean printHeader, boolean converteEspeciais) {
        if (printHeader) {
            printHeader(out);
        }
        printElement(out, e, converteEspeciais);
    }

    /**
     * Escreve o XML de forma que um eventual parse gere o mesmo XML.
     * Para impressões mais legíveis utilize printTabulado().
     *
     * @param out         saída destino
     * @param e           Elemento a partir do qual será impresso.
     * @param printHeader Se true, adiciona string de indentificação de arquivo
     *                    XML. Se false, depois não será possível fazer parse
     *                    do resultado sem informaçoes complementares (header).
     */
    @Override
    public void printDocumentIndentado(PrintWriter out, Element e, boolean printHeader) {
        if (printHeader) {
            printHeader(out);
        }
        printElement(out, e, 0);
    }

    private void printHeader(PrintWriter out) {
        out.print("<?xml version=\"1.0\" encoding=\"");
        out.print(charset);
        out.print("\"?>");
    }

    private void printElement(PrintWriter out, Element e) {
        if (!e.hasChildNodes()) {
            out.print("<");
            out.print(e.getNodeName());
            printAttributes(out, e);
            out.print("/>");
            return;
        }

        out.print('<');
        out.print(e.getNodeName());
        printAttributes(out, e);
        out.print(">");

        NodeList nList = e.getChildNodes();
        int      tam   = nList.getLength();
        for (int i = 0; i < tam; i++) {
            printNode(out, nList.item(i));
        }
        out.print("</");
        out.print(e.getNodeName());
        out.print('>');
    }

    private void printElement(PrintWriter out, Element e, boolean converteEspeciais) {
        if (!e.hasChildNodes()) {
            out.print("<");
            out.print(e.getNodeName());
            printAttributes(out, e, converteEspeciais);
            out.print("/>");
            return;
        }

        out.print('<');
        out.print(e.getNodeName());
        printAttributes(out, e, converteEspeciais);
        out.print(">");

        NodeList nList = e.getChildNodes();
        int      tam   = nList.getLength();
        for (int i = 0; i < tam; i++) {
            printNode(out, nList.item(i), converteEspeciais);
        }
        out.print("</");
        out.print(e.getNodeName());
        out.print('>');
    }

    private void printElement(PrintWriter out, Element e, int level) {
        printSpace(out, level);
        if (!e.hasChildNodes()) {
            out.print('<');
            out.print(e.getNodeName());
            printAttributes(out, e);
            out.println("/>");
            return;
        }

        out.print('<');
        out.print(e.getNodeName());
        printAttributes(out, e);
        out.print(">");

        NodeList nList = e.getChildNodes();
        int      tam   = nList.getLength();

        if (tam != 0) {
            boolean pulaLinha = (tam > 1) || !XmlUtil.isNodeTypeText(nList.item(0));
            if (pulaLinha) {
                out.println();
            }
            for (int i = 0; i < tam; i++) {
                printNode(out, nList.item(i), level + 1);
            }
            if (pulaLinha) {
                printSpace(out, level);
            }
        }
        out.print("</");
        out.print(e.getNodeName());
        out.println('>');

    }

    private void printNode(PrintWriter out, Node node) {
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                printElement(out, (Element) node);
                break;
            case Node.TEXT_NODE:
                out.print(node.getNodeValue());
                break;
            default:
                throw new SingularException(
                        "Tipo de nó '" + node.getNodeName() + "' desconhecido: " + node.getNodeType());
        }
    }

    private void printNode(PrintWriter out, Node node, boolean converteEspeciais) {
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                printElement(out, (Element) node, converteEspeciais);
                break;
            case Node.TEXT_NODE:
                String texto = node.getNodeValue();
                if (converteEspeciais) {
                    printConverteCaracteresEspeciais(out, texto.toCharArray());
                } else {
                    out.print(node.getNodeValue());
                }
                break;
            default:
                throw new SingularException(
                        "Tipo de nó '" + node.getNodeName() + "' desconhecido: " + node.getNodeType());
        }
    }


    private void printNode(PrintWriter out, Node node, int level) {
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                printElement(out, (Element) node, level);
                break;
            case Node.TEXT_NODE:
                String txt = node.getNodeValue();
                if (txt == null) {
                    //Ignora
                } else if (txt.indexOf('\n') == -1) {
                    out.print(txt);
                } else {
                    out.println();
                    printTexto(out, txt, level);
                    printSpace(out, level - 1);
                }
                break;
            default:
                throw new SingularException(
                        "Tipo de nó '" + node.getNodeName() + "' desconhecido: " + node.getNodeType());
        }
    }

    private void printTexto(PrintWriter out, String txt, int level) {
        int     tam         = txt.length();
        int     posi        = 0;
        boolean consome     = false;
        char    consomeChar = ' ';
        char    c;
        for (int i = 0; i < tam; i++) {
            c = txt.charAt(i);
            if (consome) {
                if (c == consomeChar) {
                    continue;
                } else {
                    consome = false;
                    posi = i;
                }
            }
            if (c == '\n') {
                printSpace(out, level);
                out.println(txt.substring(posi, i));
                consome = true;
                consomeChar = '\r';
                posi = i + 1;
            } else if (c == '\r') {
                printSpace(out, level);
                out.println(txt.substring(posi, i));
                consome = true;
                consomeChar = '\n';
                posi = i + 1;
            }
        }
        if (posi >= tam) {
            out.println();
        } else {
            printSpace(out, level);
            out.println(txt.substring(posi));
        }
    }

    private void printSpace(PrintWriter out, int level) {
        String[] LISTA_SPACE = new String[15];
        if (level >= LISTA_SPACE.length) {
            //Indentação maior que o cache de espaços
            for (int i = level; i != 0; i--) {
                out.print(SPACE);
            }
            return;
        }

        if (LISTA_SPACE[0] == null) {
            //Primeira chama monta array de espaços
            int tamBuffer = SPACE.length() * LISTA_SPACE.length;
            StringBuilder buf = new StringBuilder(tamBuffer);
            for (int i = 0; i < LISTA_SPACE.length; i++) {
                LISTA_SPACE[i] = buf.toString();
                buf.append(SPACE);
            }
        }
        out.print(LISTA_SPACE[level]);
    }
}
