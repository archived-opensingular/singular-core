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

package org.opensingular.form.view;

import org.opensingular.form.SType;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SView implements Serializable {

    public static final SView DEFAULT = new SView();

    /**
     * Check if this view is applicable for the {@link SType}
     * passed as the parameter. One {@link SType} can be rendered
     * by several different views.
     *
     * @param type the type to be tested for applicability
     * @return true if this view can render the type, false otherwise
     */
    public boolean isApplicableFor(SType<?> type) {
        return true;
    }
}
