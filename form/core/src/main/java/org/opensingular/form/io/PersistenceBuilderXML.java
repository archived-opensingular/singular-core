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

package org.opensingular.form.io;

import org.opensingular.form.SInstance;
import org.opensingular.internal.lib.commons.xml.MElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PersistenceBuilderXML {

    private boolean persistId = true;
    private boolean persistNull = false;
    private boolean persistAttributes = false;
    private boolean returnNullXML = true;
    private MElement parent;
    private String parentName;

    @Nonnull
    public PersistenceBuilderXML withPersistId(boolean v) {
        persistId = v;
        return this;
    }

    @Nonnull
    public PersistenceBuilderXML withPersistNull(boolean v) {
        persistNull = v;
        return this;
    }

    @Nonnull
    public PersistenceBuilderXML withPersistAttributes(boolean v) {
        persistAttributes = v;
        return this;
    }

    @Nonnull
    public PersistenceBuilderXML withParent(@Nullable MElement parent) {
        return withParent(parent, null);
    }

    @Nonnull
    public PersistenceBuilderXML withParent(@Nullable MElement parent, @Nullable String parentName) {
        this.parent = parent;
        this.parentName = parentName;
        return this;
    }

    /**
     * No caso do XML resultante não conter nenhum nó, se true implica em retorna um MELement null. Se false, retorna um
     * XML apenas com o nó raiz.
     */
    @Nonnull
    public PersistenceBuilderXML withReturnNullXML(boolean v) {
        returnNullXML = v;
        return this;
    }

    boolean isPersistId() {
        return persistId;
    }

    boolean isPersistNull() {
        return persistNull;
    }

    boolean isPersistAttributes() {
        return persistAttributes;
    }

    MElement getParent() {
        return parent;
    }

    String getParentName() {
        return parentName;
    }

    /**
     * Se true, indica que o resultado pode gerar um XML null. Se false, indica que minimamente retornara ao menos um
     * Elment com conteudo vazio.
     */
    boolean isReturnNullXML() { return returnNullXML; }

    @Nullable
    public MElement toXML(@Nonnull SInstance instance) {
        return SFormXMLUtil.toXML(instance, this);
    }

}
