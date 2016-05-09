/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.internal.xml;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public abstract class MDocument implements Document {

    public static final void toMDocument(MDocument no) {
        //Não faz nada
    }

    public static final MDocument toMDocument(Document no) {
        if (no == null) {
            return null;
        } else if (no instanceof MDocument) {
            return (MDocument) no;
        }
        return new MDocumentWrapper(no);
    }

    public static final MDocument toMDocument(Node no) {
        if (no == null) {
            return null;
        } else if (no instanceof MDocument) {
            return (MDocument) no;
        } else if (no.getNodeType() != Node.DOCUMENT_NODE) {
            throw new RuntimeException("no " + XPathToolkit.getFullPath(no) + " não é Document");
        }
        return new MDocumentWrapper((Document) no);
    }

    public static final MDocument newInstance() {
        return toMDocument(MElementWrapper.newDocument());
    }

    public MElement createMElement(String qualifiedName) {
        return createMElementNS(null, qualifiedName);
    }

    public MElement createMElementNS(String namespaceURI, String qualifiedName) {
        namespaceURI = StringUtils.trimToNull(namespaceURI);

        int pos = qualifiedName.lastIndexOf(MElementWrapper.SEPARADOR_ELEMENT);
        String resto = null;
        if (pos != -1) {
            if (pos == 0) {
                throw new RuntimeException("Criação no raiz para elemento salto não faz sentido");
            }
            resto = qualifiedName.substring(pos + 1);
            qualifiedName = qualifiedName.substring(0, pos);
        }
        Element novo = createElementNS(namespaceURI, qualifiedName);
        if (namespaceURI != null) {
            int posPrefixo = qualifiedName.indexOf(':');
            if ((posPrefixo == -1)) {
                novo.setAttribute("xmlns", namespaceURI);
            } else {
                String prefixo = qualifiedName.substring(0, posPrefixo);
                novo.setAttribute("xmlns:" + prefixo, namespaceURI);
            }
        }
        if (resto != null) {
            MElement.toMElement(novo).addElement(resto);
        }
        return MElement.toMElement(novo);
    }

    public MElement createMElementComValor(String qualifiedName, String value) {
        MElement e = createMElement(qualifiedName);
        Text txt = createTextNode(value);
        e.appendChild(txt);
        return e;
    }

    public MElement createRaiz(String qualifiedName) {
        return setRaiz(createMElement(qualifiedName));
    }

    public MElement setRaiz(MElement e) {
        appendChild(e.getOriginal());
        return e;
    }
}
