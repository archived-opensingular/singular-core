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
import org.opensingular.form.internal.xml.MElement;

public class PersistenceBuilderXML {

    private boolean persistId = true;
    private boolean persistNull = false;
    private boolean persistAttributes = false;

    public PersistenceBuilderXML withPersistId(boolean v) {
        persistId = v;
        return this;
    }

    public PersistenceBuilderXML withPersistNull(boolean v) {
        persistNull = v;
        return this;
    }

    public PersistenceBuilderXML withPersistAttributes(boolean v) {
        persistAttributes = v;
        return this;
    }

    public boolean isPersistId() {
        return persistId;
    }

    public boolean isPersistNull() {
        return persistNull;
    }

    public boolean isPersistAttributes() {
        return persistAttributes;
    }

    public MElement toXML(SInstance instancia) {
        return MformPersistenciaXML.toXML(null, null, instancia, this);
    }

}
