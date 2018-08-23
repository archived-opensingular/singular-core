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

package org.opensingular.form.context;

import org.opensingular.form.SInstance;
import org.opensingular.form.view.SView;
import org.opensingular.lib.commons.lambda.ISupplier;

import java.io.Serializable;

public interface IFormBuildContext extends Serializable {

    <T extends SInstance> T getCurrentInstance();

    IFormBuildContext getParent();

    SView getView();

    default <V extends SView> ISupplier<V> getViewSupplier(Class<V> viewType) {
        return new ViewSupplier<V>(this, viewType);
    }

    default boolean isRootContext() {
        return (this.getParent() == null);
    }
}

/*
 * [SGL-802] Refactoring for a class, because when use lambda with 'this' was causing a serialization fails.
 */
class ViewSupplier<V> implements ISupplier<V> {
    private final IFormBuildContext ctx;
    private final Class<V> viewType;

    ViewSupplier(IFormBuildContext ctx, Class<V> viewType) {
        this.ctx = ctx;
        this.viewType = viewType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get() {
        SView view = ctx.getView();
        if (view != null && viewType.isAssignableFrom(view.getClass()))
            return (V) view;
        return null;
    }
}
