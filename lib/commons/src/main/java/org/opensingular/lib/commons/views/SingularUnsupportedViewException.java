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

package org.opensingular.lib.commons.views;

import org.opensingular.lib.commons.base.SingularException;

/**
 * Indiecates that the {@link ViewGenerator} doesn't supports the particular type of {@link ViewOutput}.
 *
 * @author Daniel C. Bordin on 24/07/2017.
 */
public class SingularUnsupportedViewException extends SingularException {

    public SingularUnsupportedViewException() {
        this(null);
    }

    public SingularUnsupportedViewException(ViewOutput view) {
        super("There is no implemetation supporting this particular type of ViewOutput");
        add("viewClass", view == null ? null : view.getClass());
    }
}
