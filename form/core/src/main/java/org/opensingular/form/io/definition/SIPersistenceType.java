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

package org.opensingular.form.io.definition;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;

public class SIPersistenceType extends SIComposite {

    public void setSimpleName(String simpleName) {
        setValue(STypePersistenceType.FIELD_NAME, simpleName);
    }

    public String getSimpleName() {
        return getValueString(STypePersistenceType.FIELD_NAME);
    }

    public void setSuperType(String superTypeName) {
        setValue(STypePersistenceType.FIELD_TYPE, superTypeName);
    }

    public String getSuperType() {
        return getValueString(STypePersistenceType.FIELD_TYPE);
    }

    public SIList<SIPersistenceType> getMembers() {
        return getFieldList(STypePersistenceType.FIELD_MEMBERS, SIPersistenceType.class);
    }

    public SIPersistenceType addMember(String simpleName) {
        SIPersistenceType type = getMembers().addNew();
        type.setSimpleName(simpleName);
        return type;
    }
}
