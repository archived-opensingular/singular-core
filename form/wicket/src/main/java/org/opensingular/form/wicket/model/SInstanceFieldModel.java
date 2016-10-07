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

package org.opensingular.form.wicket.model;

import org.opensingular.form.SInstance;

public class SInstanceFieldModel<I extends SInstance>
    extends AbstractSInstanceCampoModel<I> {

    private String propertyExpression;

    public SInstanceFieldModel(Object rootTarget, String propertyExpression) {
        super(rootTarget);
        this.propertyExpression = propertyExpression;
    }

    @Override
    protected String propertyExpression() {
        return propertyExpression;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((propertyExpression == null) ? 0 : propertyExpression.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SInstanceFieldModel<?> other = (SInstanceFieldModel<?>) obj;
        if (propertyExpression == null) {
            if (other.propertyExpression != null)
                return false;
        } else if (!propertyExpression.equals(other.propertyExpression))
            return false;
        return super.equals(obj);
    }

}
