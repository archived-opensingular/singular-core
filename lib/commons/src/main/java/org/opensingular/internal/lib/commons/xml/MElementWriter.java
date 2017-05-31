package org.opensingular.internal.lib.commons.xml;

import org.w3c.dom.Element;

import java.io.PrintWriter;
import java.io.Serializable;

/**
 * Responsável por escrever um MElement em um determinado formato de saída para o printWriter informado.
 * Ex: {@link XMLMElementWriter}
 */
public interface MElementWriter extends Serializable {

    void printDocument(PrintWriter out, Element e, boolean printHeader);

    void printDocument(PrintWriter out, Element e, boolean printHeader, boolean converteEspeciais);

    void printDocumentIndentado(PrintWriter out, Element e, boolean printHeader);
}
