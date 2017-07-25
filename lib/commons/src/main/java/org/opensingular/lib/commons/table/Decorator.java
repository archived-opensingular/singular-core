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

package org.opensingular.lib.commons.table;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Representa as configurações de exibição de um elemento do resultado (toda tabela, linha, coluna ou celula). É
 * baseado em HTML, mas pode ser consumido por geradores não HTML.
 *
 * @author Daniel C. Bordin on 20/07/2006
 */
public class Decorator implements Serializable {

    private final Decorator parent;

    private String cssClass;

    /** Representa atributos HTML a serem colocados na Tag HTML. */
    private Map<String, String> attributes;

    /** Representa as configurações CSS a serem colocadas na Tag HTML. */
    private Map<String, String> styles;

    private Integer maxTextLength;//NOSONAR

    public Decorator() {
        this.parent = null;
    }

    public Decorator(Decorator parent) {
        this.parent = parent;
    }

    public Decorator newDerivedDecorator() {
        return new Decorator(this);
    }

    /** Define a cor da fonte a ser utilizada. */
    @Nonnull
    public Decorator setFontColor(@Nullable String cor) {
        return addStyle("color", cor);
    }

    /** Define a cor de fundo do elemento. */
    @Nonnull
    public Decorator setBackground(@Nullable String cor) {
        return addStyle("background-color", cor);
    }

    //** Define que a não deve ter quebra de linha no texto. */
    public Decorator setNoWrap() {
        return addStyle("white-space", "nowrap");
    }

    /** Define a classe CSS a ser utilizada no elemento. */
    @Nonnull
    public Decorator setCssClass(@Nullable String cssClass) {
        this.cssClass = cssClass;
        return this;
    }

    /** Classe CSS a ser utilziada no elemento. */
    @Nullable
    public String getCssClass() {
        return cssClass != null ? cssClass : parent == null ? null : parent.getCssClass();
    }

    public Integer getMaxTextLength() {
        return maxTextLength != null ? maxTextLength : parent == null ? null : parent.getMaxTextLength();
    }

    /** Define que a fonte seja bold ou não. */
    @Nonnull
    public final Decorator setBold(boolean bold) {
        return addStyle("font-weight", bold ? "bold" : "inherit");
    }

    /** Define o valor de uma entrada CSS do elemento atual. */
    @Nonnull
    public Decorator addStyle(@Nonnull String name, @Nullable String value) {
        Objects.requireNonNull(name);
        if (styles == null) {
            styles = new HashMap<>();
        }
        if (value == null) {
            styles.remove(value);
        } else {
            styles.put(name, value);
        }
        return this;
    }

    /** Define o valor do atributo 'title' para o elemento atual. */
    @Nonnull
    public Decorator addTitle(@Nullable String valor) {
        return addAttribute("title", valor);
    }

    /** Define o valor de um atributo para o elemento atual. */
    @Nonnull
    public Decorator addAttribute(@Nonnull String name, @Nullable String value) {
        Objects.requireNonNull(name);
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        if (value == null) {
            attributes.remove(name);
        } else {
            attributes.put(name, value);
        }
        return this;
    }

    /** Verifica se o atirbuto informado possui um valor configurado. */
    public boolean containsAttribute(@Nonnull String attributeName) {
        return (attributes != null && attributes.containsKey(Objects.requireNonNull(attributeName))) ||
                (parent != null && parent.containsAttribute(attributeName));
    }

    /** Retorna todos os atributos configurados. */
    @Nonnull
    public Map<String, String> getAttributes() {
        return mergeMaps(this, d -> d.attributes);
    }

    /** Retorna todas as entradas CSS para o elemento atual. */
    @Nonnull
    public Map<String, String> getStyles() {
        return mergeMaps(this, d -> styles);
    }

    @Nonnull
    private static Map<String,String> mergeMaps(Decorator decorator, Function<Decorator, Map<String,String>> reader) {
        //A lógica a seguir foi construida a fim de minimizar a criaçao de Map
        Map<String,String> result = reader.apply(decorator);
        if (decorator.parent != null) {
            if (result == null) {
                return mergeMaps(decorator.parent, reader);
            }
            Map<String,String> merged = createNewMapForMerge(decorator.parent, reader);
            if (merged != null) {
                merged.putAll(result);
                return merged;
            }
        }
        return result == null ? Collections.emptyMap() : result;
    }

    private static Map<String, String> createNewMapForMerge(Decorator decorator,
            Function<Decorator, Map<String, String>> reader) {
        if (decorator == null) {
            return null;
        }
        Map<String,String> resultParent = createNewMapForMerge(decorator.parent, reader);
        Map<String,String> resultCurrent = reader.apply(decorator);
        if (resultParent == null) {
            if (resultCurrent != null) {
                return new LinkedHashMap<>(resultCurrent);
            }
        } else if (resultCurrent != null) {
            resultParent.putAll(resultCurrent);
        }
        return resultParent;
    }
}
