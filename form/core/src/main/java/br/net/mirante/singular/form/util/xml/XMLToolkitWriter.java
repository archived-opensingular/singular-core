/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.util.xml;

import java.io.PrintWriter;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Creation date: (24/04/2000 10:34:52)
 *
 * @author Daniel Bordin - Mirante Informática
 */

final class XMLToolkitWriter {

    /**
     * Define o tamanho da tabulação
     */
    private static final String SPACE = "    ";
    /**
     * Tabulações predefinidas p/ evitar montagem constante de string (cache)
     */
    private static final String[] LISTA_SPACE = new String[15];

    private static final char[] ESPECIAIS = {'&', '<', '>'};
    private static final String[] SUBSTITUTOS = {"&amp;", "&lt;", "&gt;"};

    /**
     * Esconde o construtor por ser uma classe utilitária
     */
    private XMLToolkitWriter() {
    }

    //printAttributes acrescentado por Joao Rafael
    private static void printAttributes(PrintWriter out, Element e) {
        NamedNodeMap map = e.getAttributes();
        int l = map.getLength();
        for (int i = 0; i < l; i++) {
            Attr a = (Attr) map.item(i);
            out.print(" ");
            out.print(a.getName());
            out.print("=\"");
            out.print(a.getValue());
            out.print("\"");
        }
    }

    private static void printAttributes(PrintWriter out, Element e, boolean converteEspeciais) {
        NamedNodeMap map = e.getAttributes();
        int l = map.getLength();
        for (int i = 0; i < l; i++) {
            Attr a = (Attr) map.item(i);
            out.print(" ");
            out.print(a.getName());
            out.print("=\"");
            String texto = a.getValue();
            if (converteEspeciais) {
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
     * @param out saída destino
     * @param e Elemento a partir do qual será impresso.
     * @param printHeader Se true, adiciona string de indentificação de arquivo
     * XML. Se false, depois não será possível fazer parse
     * do resultado sem informaçoes complementares (header).
     */
    public static void printDocument(PrintWriter out, Element e, boolean printHeader) {
        if (printHeader) {
            out.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        }
        printElement(out, e);
    }

    /**
     * Escreve o XML de forma que um eventual parse gere o mesmo XML.
     * Para impressões mais legíveis utilize printTabulado().
     *
     * @param out saída destino
     * @param e Elemento a partir do qual será impresso.
     * @param printHeader Se true, adiciona string de indentificação de arquivo
     * XML. Se false, depois não será possível fazer parse
     * do resultado sem informaçoes complementares (header).
     * @param converteEspeciais converte os caracteres de escape.
     */
    public static void printDocument(PrintWriter out, Element e, boolean printHeader, boolean converteEspeciais) {
        if (printHeader) {
            out.print("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        }
        printElement(out, e, converteEspeciais);
    }

    /**
     * Escreve o XML de forma que um eventual parse gere o mesmo XML.
     * Para impressões mais legíveis utilize printTabulado().
     *
     * @param out saída destino
     * @param e Elemento a partir do qual será impresso.
     * @param printHeader Se true, adiciona string de indentificação de arquivo
     * XML. Se false, depois não será possível fazer parse
     * do resultado sem informaçoes complementares (header).
     */
    public static void printDocumentIndentado(PrintWriter out, Element e, boolean printHeader) {
        if (printHeader) {
            out.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
        }
        printElement(out, e, 0);
    }

    private static void printElement(PrintWriter out, Element e) {
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
        int tam = nList.getLength();
        for (int i = 0; i < tam; i++) {
            printNode(out, nList.item(i));
        }
        out.print("</");
        out.print(e.getNodeName());
        out.print('>');
    }

    private static void printElement(PrintWriter out, Element e, boolean converteEspeciais) {
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
        int tam = nList.getLength();
        for (int i = 0; i < tam; i++) {
            printNode(out, nList.item(i), converteEspeciais);
        }
        out.print("</");
        out.print(e.getNodeName());
        out.print('>');
    }

    private static void printElement(PrintWriter out, Element e, int level) {
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
        int tam = nList.getLength();

        if (tam != 0) {
            boolean pulaLinha = (tam > 1) || (nList.item(0).getNodeType() != Node.TEXT_NODE);
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

    private static void printNode(PrintWriter out, Node node) {
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                printElement(out, (Element) node);
                break;
            case Node.TEXT_NODE:
                out.print(node.getNodeValue());
                break;
            default:
                throw new RuntimeException(
                        "Tipo de nó '" + node.getNodeName() + "' desconhecido: " + node.getNodeType());
        }
    }

    private static void printNode(PrintWriter out, Node node, boolean converteEspeciais) {
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
                throw new RuntimeException(
                        "Tipo de nó '" + node.getNodeName() + "' desconhecido: " + node.getNodeType());
        }
    }

    private static void printConverteCaracteresEspeciais(PrintWriter out, char[] texto) {
        int len = texto.length;
        int ultimoEscrito = 0;
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < 3; j++) {
                if (texto[i] == ESPECIAIS[j]) {
                    out.write(texto, ultimoEscrito, i - ultimoEscrito);
                    out.print(SUBSTITUTOS[j]);
                    ultimoEscrito = i + 1;
                }
            }
        }
        out.write(texto, ultimoEscrito, len - ultimoEscrito);
    }

    private static void printNode(PrintWriter out, Node node, int level) {
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
                throw new RuntimeException(
                        "Tipo de nó '" + node.getNodeName() + "' desconhecido: " + node.getNodeType());
        }
    }

    private static void printTexto(PrintWriter out, String txt, int level) {
        int tam = txt.length();
        int posi = 0;
        boolean consome = false;
        char consomeChar = ' ';
        char c;
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

    private static void printSpace(PrintWriter out, int level) {
        if (level >= LISTA_SPACE.length) {
            //Indentação maior que o cache de espaços
            for (; level != 0; level--) {
                out.print(SPACE);
            }
            return;
        }

        if (LISTA_SPACE[0] == null) {
            //Primeira chama monta array de espaços
            int tamBuffer = SPACE.length() * LISTA_SPACE.length;
            StringBuffer buf = new StringBuffer(tamBuffer);
            for (int i = 0; i < LISTA_SPACE.length; i++) {
                LISTA_SPACE[i] = buf.toString();
                buf.append(SPACE);
            }
        }
        out.print(LISTA_SPACE[level]);
    }
}
