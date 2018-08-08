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

package org.opensingular.form.flatview;

import org.opensingular.form.view.SView;

public class SViewQualifier {
    private Class<? extends SView> view;

    public SViewQualifier(Class<? extends SView> view) {
        this.view = view;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SViewQualifier that = (SViewQualifier) o;

        return view != null ? view.equals(that.view) : that.view == null;
    }

    @Override
    public int hashCode() {
        return view != null ? view.hashCode() : 0;
    }
}
