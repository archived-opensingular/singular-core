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

package org.opensingular.form.type.core.annotation;

import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.SPackageCore;

/**
 * This type represents an Annotation of a field.
 * For now only composite fields can be anotated but this type does not enforce such rule.
 *
 * @author Fabricio Buzeto
 */
@SInfoType(name = "Annotation", spackage = SPackageCore.class)
public class STypeAnnotation extends STypeComposite<SIAnnotation> {

    public static final String          FIELD_TEXT          = "text",
                                        FIELD_TARGET_ID     = "targetId",
                                        FIELD_APPROVED      = "isApproved",
                                        FIELD_CLASSIFIER     = "classifier"
                                        ;

    public STypeAnnotation() {
        super(SIAnnotation.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        addFieldString(FIELD_TEXT);
        addFieldString(FIELD_CLASSIFIER);
        addFieldBoolean(FIELD_APPROVED);
        addFieldInteger(FIELD_TARGET_ID);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Object> T convert(Object valor, Class<T> classeDestino) {
        return (T) valor;
    }
}
