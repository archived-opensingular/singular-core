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

package org.opensingular.lib.commons.table;


import org.opensingular.lib.commons.base.SingularException;
import org.opensingular.lib.commons.net.Lnk;
import org.opensingular.lib.commons.net.WebRef;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class InfoCell implements Serializable {

    private final Column column;

    private transient Object value;

    private transient Comparable<?> valueReal;

    private WebRef link;

    private String linkTitle;

    private String linkTarget;

    private List<WebRef> actions;

    private DecoratorCell decorator;

    InfoCell(Column column) {
        this.column = column;
    }

    @SuppressWarnings("unchecked")
    public <K> K getValue() {
        return (K) value;
    }

    final Number getValueAsNumberOrNull() {
        return value instanceof Number ? (Number) value : null;
    }

    public InfoCell setValue(Object value) {
        this.value = value;
        return this;
    }

    public final Comparable<?> getValueReal() {
        return valueReal;
    }

    final void setValueReal(Comparable<?> valueReal) {
        this.valueReal = valueReal;
    }

    public void addAction(WebRef action) {
        if (actions == null) {
            actions = new ArrayList<>(4);
        }
        actions.add(action);
    }

    public boolean isActionsEmpty() {
        return actions == null || actions.isEmpty();
    }

    public List<WebRef> getActions() {
        return actions == null ? Collections.emptyList() : actions;
    }

    public InfoCell setLink(Lnk link) {
        throw new SingularException("Implementar");
        //setLink(link != null ? WebActionRefImpl.of("", link) : null);
        //return this;
    }

    public InfoCell setLink(String link) {
        return setLink(Lnk.of(link));
    }

    public InfoCell setLink(Lnk link, String target) {
        setLink(link);
        setLinkTarget(target);
        return this;
    }

    public InfoCell setLink(WebRef actionRef) {
        if (actionRef != null && actionRef.hasPermission() && actionRef.appliesToContext()) {
            this.link = actionRef;
            setLinkTitle(actionRef.getName());
        }
        return this;
    }

    public WebRef getLink() {
        return link;
    }

    public InfoCell setLinkTitle(String hiperLinkTitle) {
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
