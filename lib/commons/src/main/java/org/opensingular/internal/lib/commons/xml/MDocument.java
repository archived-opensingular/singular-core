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

import org.apache.commons.lang3.StringUtils;
import org.opensingular.lib.commons.base.SingularException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class MDocument implements Document {

    public static void toMDocument(MDocument no) {
        throw SingularException.rethrow("Não deveria ser chamadado esse metodo com um parâmetro MDocument");
    }

    public static MDocument toMDocument(Document no) {
        if (no == null) {
            return null;
        } else if (no instanceof MDocument) {
            return (MDocument) no;
        }
        return new MDocumentWrapper(no);
    }

    public static MDocument toMDocument(Node no) {
        if (no == null) {
            return null;
        } else if (no instanceof MDocument) {
            return (MDocument) no;
        } else if (no.getNodeType() != Node.DOCUMENT_NODE) {
            throw new SingularException("no " + XPathToolkit.getFullPath(no) + " não é Document");
        }
        return new MDocumentWrapper((Document) no);
    }

    public static MDocument newInstance() {
        return toMDocument(MElementWrapper.newDocument());
    }

    @Nonnull
    public MElement createMElement(@Nonnull String qualifiedName) {
        return createMElementNS(null, qualifiedName);
    }

    @Nonnull
    public MElement createMElementNS(@Nullable String namespaceURI2, @Nonnull String qualifiedName) {
        String resolvedNamespaceURI = StringUtils.trimToNull(namespaceURI2);
        String resolvedQualifiedName = qualifiedName;

        int pos = resolvedQualifiedName.lastIndexOf(MElementWrapper.ELEMENT_PATH_SEPARATOR);
        String resto = null;
        if (pos != -1) {
            if (pos == 0) {
                throw new SingularException("Criação no raiz para elemento salto não faz sentido");
            }
            resto = resolvedQualifiedName.substring(pos + 1);
            resolvedQualifiedName = resolvedQualifiedName.substring(0, pos);
        }
        Element newElement = createElementNS(resolvedNamespaceURI, resolvedQualifiedName);
        if (resolvedNamespaceURI != null) {
            int posPrefixo = resolvedQualifiedName.indexOf(':');
            if ((posPrefixo == -1)) {
                newElement.setAttribute("xmlns", resolvedNamespaceURI);
            } else {
                String prefixo = resolvedQualifiedName.substring(0, posPrefixo);
                newElement.setAttribute("xmlns:" + prefixo, resolvedNamespaceURI);
            }
        }
        MElement result = MElement.toMElementNotNull(newElement);
        if (resto != null) {
            result.addElement(resto);
        }
        return result;
    }

    @Nonnull
    public MElement createMElementWithValue(@Nonnull String qualifiedName, String value) {
        MElement e = createMElement(qualifiedName);
        Text txt = createTextNode(value);
        e.appendChild(txt);
        return e;
    }

    public MElement createRoot(String qualifiedName) {
        return setRoot(createMElement(qualifiedName));
    }

    public MElement setRoot(MElement e) {
        appendChild(e.getOriginal());
        return e;
    }
}
