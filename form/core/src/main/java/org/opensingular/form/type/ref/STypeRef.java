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

package org.opensingular.form.type.ref;

import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.document.SDocument;
import org.opensingular.form.provider.SSimpleProvider;
import org.opensingular.form.type.core.STypeString;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Permite a criação simples de uma referencia à determinado objeto. É aconselhavel utilizar esta classe
 * quando se deseja incluir uma versão mais simples de um objeto como tipo, no qual não se deseje ter acesso
 * a todos os atributos, somente chave e valor.
 *
 * @param <T> O valor original
 */
public abstract class STypeRef<T> extends STypeComposite<SIComposite> {

    /**
     * A Chave da referencia, pode ser tanto um filho do SType Original ou uma FormKey
     */
    public STypeString key;

    /**
     * O Valor de exibição da referencia, não deve ser necessariamente somente um campo, podendo ser composto por varios
     * do SType Orignal.
     */
    public STypeString display;

    @Override
    protected void onLoadType(@Nonnull TypeBuilder tb) {
        key = addField("key", STypeString.class);
        display = addField("display", STypeString.class);
        selection()
                .id(key)
                .display(display)
                .simpleProvider(simpleProvider());
    }

    /**
     * Constroi um provider simples, utilizados os metodos getKeyValue e getDisplayValue
     * para fazer o preenhcimento dos valores
     *
     * @return o provider
     */
    protected SSimpleProvider simpleProvider() {
        return (SSimpleProvider) builder -> {
            List<T> values = loadValues(builder.getCurrentInstance().getDocument());
            for (T val : values) {
                builder.add()
                        .set(key, getKeyValue(val))
                        .set(display, getDisplayValue(val));
            }
        };
    }

    /**
     * Recebe o valor original e deve retornar um valor para ser utilizado como chave da referencia
     *
     * @param value o valor original
     * @return o valor a ser utilizado como chave
     */
    protected abstract String getKeyValue(T value);

    /**
     * Recebe a instancia original e deve retornar o valor para exibição da referencia, é utilizado por default
     * como display de seleções
     *
     * @param value o valor original
     * @return o valor de exibição
     */
    protected abstract String getDisplayValue(T value);

    /**
     * Recebe a instancia original e deve retornar os valores para seleçao da referencia
     *
     * @return os valores possiveis
     */
    protected abstract List<T> loadValues(SDocument document);
}