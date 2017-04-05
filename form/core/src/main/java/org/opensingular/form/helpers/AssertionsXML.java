/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package org.opensingular.form.helpers;

import org.opensingular.form.internal.xml.MElement;
import org.opensingular.form.internal.xml.MParser;
import org.opensingular.lib.commons.test.AssertionsBase;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Classe de apoio a a escrita de assertivas referentes a um XML ({@link MElement} ou {@link Element}). Dispara
 * {@link AssertionError} se uma assertiva for violada.
 *
 * @author Daniel C. Bordin on 27/02/2017.
 */
public class AssertionsXML extends AssertionsBase<MElement, AssertionsXML> {

    public AssertionsXML(Element e) {
        super(MElement.toMElement(e));
    }

    public AssertionsXML(MElement e) {
        super(e);
    }

    public AssertionsXML(Optional<? extends MElement> e) {
        super(e);
    }

    @Override
    protected String errorMsg(String msg) {
        return getTargetOpt().isPresent() ? "No elemento " + getTarget().getFullPath() + ": " + msg : msg;
    }

    /** Verifica se o elemento atual não é null e o nome do nó corresponde ao indicado. */
    public AssertionsXML isName(String expetedName) {
        if (!Objects.equals(expetedName, getTarget().getNodeName())) {
            throw new AssertionError(errorMsg("Nome do nó incorreto", expetedName, getTarget().getNodeName()));
        }
        return this;
    }

    /** Verifica se a quantidade de sub nós do tipo Element é zero. */
    public AssertionsXML hasNoChildren() {
        if (getTarget().countFilhos() != 0) {
            throw new AssertionError(errorMsg("Qtd de sub Elements incorretos", 0, getTarget().countFilhos()));
        }
        return this;
    }

    /** Verifica não possui nenhum sob Node, ou seja, não pode ter nenhum sub Element, texto, comentários, etc. */
    public AssertionsXML isEmptyNode() {
        if (getTarget().getFirstChild() != null) {
            throw new AssertionError(errorMsg("Era esperado não possuir nenhuma subinformação"));
        }
        return this;
    }

    /** Verifica se o atributo ID do elemento corresponde ao valor esperado. */
    public AssertionsXML isId(int expectedId) {
        return isAtr("id", String.valueOf(expectedId));
    }

    /** Verifica se o valor do atributo e o esperado. */
    private AssertionsXML isAtr(String atributeName, String expectedValue) {
        String currentValue = getTarget().getAttribute(atributeName);
        if (!Objects.equals(expectedValue, currentValue)) {
            throw new AssertionError(
                    errorMsg("O valor do atributo '" + atributeName + "' diferente do esperado", expectedValue,
                            currentValue));
        }
        return this;
    }

    /** Verifica se o valor texto do elemento é o esperado. */
    public AssertionsXML isValue(String expectedValue) {
        String currentValue = getTarget().getValor();
        if (!Objects.equals(expectedValue, currentValue)) {
            throw new AssertionError(
                    errorMsg("O valor do elemento e diferente do esperado", expectedValue, currentValue));
        }
        return this;
    }

    /**
     * Verifica se o Element possui somente um texto é igual ao esperado. Se possui outros tipos de sub nós
     * (comentários, sub elementos, etc.), então dispara exception.
     */
    public AssertionsXML hasOnlyValue(String expectedValue) {
        if (expectedValue == null) {
            return isEmptyNode();
        }
        if (getTarget().getFirstChild() != null && getTarget().getFirstChild().getNextSibling() != null) {
            throw new AssertionError(errorMsg("Possui mais informação além do próprio valor"));
        }
        return isValue(expectedValue);
    }

    /** Verica se o Element é equivalent ao texto XML informado. */
    public AssertionsXML isContentEquals(String expectedXMLContent) {
        isNotNull();
        MElement expectedXML;
        try {
            expectedXML = MParser.parse(expectedXMLContent);
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
        String currentContent = getTarget().toStringExato();
        String expectedAjustedXML = expectedXML.toStringExato();
        if (! Objects.equals(expectedAjustedXML, currentContent)) {
            throw new AssertionError(
                    errorMsg("O conteúdo XML não é o esperado", expectedXML.toString(), getTarget().toString()));
        }
        return this;
    }
}
