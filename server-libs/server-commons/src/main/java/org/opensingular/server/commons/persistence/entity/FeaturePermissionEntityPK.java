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

package org.opensingular.server.commons.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class FeaturePermissionEntityPK implements Serializable {

    @Column(name = "CO_FUNCIONALIDADE")
    private String feature;

    @Column(name = "CO_PERMISSAO")
    private String permission;

    @Column(name = "CO_MODULO_SINGULAR")
    private String module;



    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeaturePermissionEntityPK that = (FeaturePermissionEntityPK) o;

        if (!feature.equals(that.feature)) return false;
        if (!permission.equals(that.permission)) return false;
        return module.equals(that.module);

    }

    @Override
    public int hashCode() {
        int result = feature.hashCode();
        result = 31 * result + permission.hashCode();
        result = 31 * result + module.hashCode();
        return result;
    }
}
