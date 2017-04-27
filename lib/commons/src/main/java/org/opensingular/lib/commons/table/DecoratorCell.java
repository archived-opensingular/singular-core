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

/**
 * Representa as configurações de exibição de uma celula do resultado. É
 * baseado em HTML, mas pode ser consumido por geradores não HTML.
 *
 *
 * @author Daniel C. Bordin on 15/04/2017.
 */
public class DecoratorCell extends Decorator {

    /** Indica que ocupará todas as colunas restantes. */
    private boolean colSpanAll;

    private int colSpan = 1;

    private int rowSpan = 1;

    public DecoratorCell(Decorator parent) {
        super(parent);
    }

    /**
     * Indica se a celula deverá ocupar todas as colunas remanescente, se for o decoreador se referir a uma celula. Se
     * houver coluna após essa celula, não serão exibidas. Se o elemento alvo nao for uma celula na tabela, então esse
     * comando é ignorado.
     */
    @Nonnull
    public Decorator setColSpanAll(boolean v) {
        colSpanAll = v;
        return this;
    }

    /**
     * Indica que a celula deverá ocupar todas as colunas remanescente, se for o decoreador se referir a uma celula. Se
     * houver coluna após essa celula, não serão exibidas. Se o elemento alvo nao for uma celula na tabela, então esse
     * comando é ignorado.
     */
    @Nonnull
    public Decorator setColSpanAll() {
        return setColSpanAll(true);
    }

    /**
     * Indica se a celula atual deve ocupar todas as colunas remanescente (as quais não serão exibidas). Se o elemento
     * atual não for do tipo celula da tabela, então não possui efeito prático.
     */
    public boolean isColSpanAll() {
        return colSpanAll;
    }

    /**
     * Retorna a quantidade de colunas a serem ocupadas pela celula atual. Não possui efeito prático se o elemento
     * alvo não for uma celula. Default é 1.
     */
    public int getColSpan() {
        return colSpan;
    }

    /**
     * Define a quantidade de colunas a serem ocupadas pela celula atual. Não possui efeito prático se o elemento
     * alvo não for uma celula. {@link #setColSpanAll(boolean)} tem precedência sobre esse valor.
     *
     * @param colSpan Deve ser >= 1
     */
    @Nonnull
    public Decorator setColSpan(int colSpan) {
        if (colSpan < 1) {
            throw new IllegalArgumentException("qtd must be >= 1");
        }
        this.colSpan = colSpan;
        return this;
    }

    /**
     * Indica se possui configuração de colSpan diferente do default. Ou seja, se a celula deverá ocuparar mais de uma
     * posição.
     */
    public boolean hasColSpan() {
        return colSpanAll || colSpan > 1;
    }

    /**
     * Retorna quantas linhas a celula atual deve ocupar. Não possui efeito prático se o elemento atual não for uma
     * célula.
     */
    public int getRowSpan() {
        return rowSpan;
    }

    /**
     * Define quantas linhas a celula atual deve ocupar, se o elemento atual for uma celula. Não possui efeito prático
     * se o elemento atual não for uma célula.
     */
    @Nonnull
    public Decorator setRowSpan(int rowSpan) {
        if (rowSpan < 1) {
            throw new IllegalArgumentException("rowSpam must be >= 1");
        }
        this.rowSpan = rowSpan;
        return this;
    }
}
