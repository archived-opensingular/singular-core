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

package org.opensingular.lib.commons.views;

import org.opensingular.lib.commons.base.SingularException;

/**
 * Represents a fail related to view manipulation.
 *
 * @author Daniel C. Bordin on 26/07/2017.
 */
public class SingularViewException extends SingularException {

    public SingularViewException(String msg) {
        super(msg);
    }

    public SingularViewException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public SingularViewException(Throwable e) {
        super(e);
    }
}
