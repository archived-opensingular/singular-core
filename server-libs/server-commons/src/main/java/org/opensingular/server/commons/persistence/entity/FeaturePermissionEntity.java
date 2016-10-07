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

import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.Constants;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(schema = Constants.SCHEMA, name = "TB_FUNCIONALIDADE_PETICAO")
public class FeaturePermissionEntity extends BaseEntity<FeaturePermissionEntityPK> {

    @EmbeddedId
    private FeaturePermissionEntityPK cod;

    @Override
    public FeaturePermissionEntityPK getCod() {
        return cod;
    }

    public void setCod(FeaturePermissionEntityPK cod) {
        this.cod = cod;
    }
}
