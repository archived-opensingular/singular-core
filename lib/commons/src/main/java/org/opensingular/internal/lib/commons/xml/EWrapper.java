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

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Indica que uma classe possui um Element embutido internamente. Permite
 * assim obter o elemento original.
 *
 * @author Daniel C. Bordin
 */
interface EWrapper {

    /**
     * Obtem o Element contido internamente pelo envoltorio.
     *
     * @return Geralmente not null, mas depente do wrapper
     */
    public Element getOriginal();

    /**
     * Desenvelopa se necessário. Ou seja, recupera a elemento original ser for um {@link EWrapper} ou então retorna o próprio
     * valor passado.
     */
    public static Element getOriginal(Element element) {
        return element instanceof EWrapper ? ((EWrapper) element).getOriginal() : element;
    }

    /**
     * Desenvelopa se necessário. Ou seja, recupera a elemento original ser for um {@link EWrapper} ou então retorna o próprio
     * valor passado.
     */
    public static Node getOriginal(Node node) {
        return node instanceof EWrapper ? ((EWrapper) node).getOriginal() : node;
    }
}
