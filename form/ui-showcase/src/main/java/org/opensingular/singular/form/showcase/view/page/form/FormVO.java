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

package org.opensingular.singular.form.showcase.view.page.form;

import org.opensingular.form.SType;
import org.opensingular.singular.form.showcase.dao.form.ShowcaseTypeLoader;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FormVO implements Serializable, IModel<String> {
    private String key;
    private String typeName;
    private transient SType<?> value;

    public FormVO(String key, SType<?> value) {
        this.key = key;
        this.value = value;
        if(value != null) this.typeName = value.getName();
    }

    public FormVO(ShowcaseTypeLoader.TemplateEntry t) {
        this(t.getDisplayName(), t.getType());
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTypeName() {
        return typeName;
    }

    public SType<?> getType() {
        return value;
    }

    public void setType(SType<?> value) {
        this.value = value;
    }

    @Override
    public void detach() {
    }

    @Override
    public String getObject() {
        return getKey();
    }

    @Override
    public void setObject(String o) {
        setKey(o);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)    return true;
        if (obj == null)    return false;
        if (getClass() != obj.getClass())   return false;
        FormVO other = (FormVO) obj;
        if (key == null) {
            if (other.key != null)  return false;
        } else if (!key.equals(other.key))  return false;
        if (typeName == null) {
            if (other.typeName != null) return false;
        } else if (!typeName.equals(other.typeName))    return false;
        return true;
    }
    
    
}