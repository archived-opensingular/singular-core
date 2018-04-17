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

package org.opensingular.form.type.core;

import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.builder.selection.FieldRefSelectionBuilder;

/**
 * Type used to refer to another instance in the same document. The {@code refId} field holds
 * the @{code id} of the referred instance, and @{code description} holds the display string.
 */
@SInfoType(name = "FieldRef", spackage = SPackageCore.class)
public class STypeFieldRef<SI extends SInstance> extends STypeComposite<SIFieldRef<SI>> {

    public static final String FIELD_REF_ID      = "refId";
    public static final String FIELD_DESCRIPTION = "description";

    public STypeInteger        refId;
    public STypeString         description;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public STypeFieldRef() {
        super((Class) SIFieldRef.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        this.refId = addFieldInteger(FIELD_REF_ID);
        this.description = addFieldString(FIELD_DESCRIPTION);
    }

    public <STL extends STypeList<ST, SI>, ST extends SType<SI>> FieldRefSelectionBuilder<STL, ST, SI> selectFrom(STL listField) {
        return new FieldRefSelectionBuilder<>(this, listField);
    }
}
