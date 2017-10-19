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


import org.apache.commons.lang3.ClassUtils;
import org.opensingular.form.SIComposite;

import static org.opensingular.form.io.definition.SIPersistenceAttribute.Value.LAMBDA_VALUE;

public class SIPersistenceAttribute extends SIComposite {

    public boolean isLambdaValue() {
        return LAMBDA_VALUE.name().equals(getAttrValue());
    }

    public enum Value {
        LAMBDA_VALUE
    }

    private STypePersistenceAttribute getSType() {
        return (STypePersistenceAttribute) super.getType();
    }

    public String getAttrValue() {
        return this.getField(getSType().attrValue).getValue();
    }


    public String getAttrType() {
        return this.getField(getSType().attrType).getValue();
    }

    public void setAttrValue(Object value) {
        Object newValue = value;
        if (newValue != null && (ClassUtils.isPrimitiveOrWrapper(newValue.getClass()) || newValue instanceof String)) {
            newValue = String.valueOf(newValue);
        } else if (newValue != null) {
            newValue = LAMBDA_VALUE;//NOSONAR
        }
        this.setValue(getSType().attrValue, newValue);
    }

    public void setAttrType(String type) {
        this.setValue(getSType().attrType, type);
    }

}
