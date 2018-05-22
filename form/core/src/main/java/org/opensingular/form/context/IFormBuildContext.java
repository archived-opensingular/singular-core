/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.context;

import java.io.Serializable;

import org.opensingular.form.SInstance;
import org.opensingular.form.view.SView;
import org.opensingular.lib.commons.lambda.ISupplier;

public interface IFormBuildContext extends Serializable {

    <T extends SInstance> T  getCurrentInstance();
    
    IFormBuildContext getParent();
    
    SView getView();
    
    @SuppressWarnings("unchecked")
    default <V extends SView> ISupplier<V> getViewSupplier(Class<V> viewType) {
        return () -> {
            SView view = this.getView();
            if (view != null && viewType.isAssignableFrom(view.getClass()))
                return (V) view;
            return (V) null;
        };
    }
    
    default boolean isRootContext() {
        return (this.getParent() == null);
    }
}
