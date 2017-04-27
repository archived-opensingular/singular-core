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


import org.opensingular.lib.commons.net.Lnk;
import org.opensingular.lib.commons.net.WebRef;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class InfoCelula implements Serializable {

    private final Column column;

    private Object valor_;

    private Comparable<?> valorReal;

    private WebRef link;

    private String linkTitle;

    private String linkTarget;

    private List<WebRef> acoes_;

    private DecoratorCell decorator;

    InfoCelula(Column column) {
        this.column = column;
    }

    @SuppressWarnings("unchecked")
    public <K> K getValue() {
        return (K) valor_;
    }

    final Number getValueAsNumberOrNull() {
        return valor_ instanceof Number ? (Number) valor_ : null;
    }

    public InfoCelula setValor(Object valor) {
        valor_ = valor;
        return this;
    }

    public final Comparable<?> getValorReal() {
        return valorReal;
    }

    final void setValorReal(Comparable<?> valorReal) {
        this.valorReal = valorReal;
    }

    public void addAcao(WebRef acao) {
        if (acoes_ == null) {
            acoes_ = new ArrayList<>(4);
        }
        acoes_.add(acao);
    }

    public boolean isAcaoEmpty() {
        return acoes_ == null || acoes_.isEmpty();
    }

    public List<WebRef> getAcoes() {
        return acoes_ == null ? Collections.emptyList() : acoes_;
    }

    public InfoCelula setLink(Lnk link) {
        throw new RuntimeException("Implementar");
        //setLink(link != null ? WebActionRefImpl.of("", link) : null);
        //return this;
    }

    public InfoCelula setLink(String link) {
        return setLink(Lnk.of(link));
    }

    public InfoCelula setLink(Lnk link, String target) {
        setLink(link);
        setLinkTarget(target);
        return this;
    }

    public InfoCelula setLink(WebRef actionRef) {
        if (actionRef != null && actionRef.isPossuiDireitoAcesso() && actionRef.isSeAplicaAoContexto()) {
            this.link = actionRef;
            setLinkTitle(actionRef.getNome());
        }
        return this;
    }

    public WebRef getLink() {
        return link;
    }

    public InfoCelula setLinkTitle(String hiperLinkTitle) {
        this.linkTitle = hiperLinkTitle;
        return this;
    }

    public String getLinkTitle() {
        return linkTitle;
    }

    public String getLinkTarget() {
        return linkTarget;
    }

    public void setLinkTarget(String hiperLinkTarget) {
        this.linkTarget = hiperLinkTarget;
    }

    public boolean temDecorador() {
        return decorator != null;
    }

    public DecoratorCell getDecorator() {
        if (decorator == null) {
            decorator = createTempDecorator();
        }
        return decorator;
    }

    public DecoratorCell createTempDecorator() {
        if (decorator == null) {
            return new DecoratorCell(column.getDecoratorValues());
        }
        return new DecoratorCell(decorator);
    }

    public Column getColumn() {
        return column;
    }
}
