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

package org.opensingular.flow.persistence.entity;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.opensingular.lib.support.persistence.util.Constants;

/**
 * The persistent class for the flow definition database table.
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@SequenceGenerator(name = AbstractFlowDefinitionEntity.PK_GENERATOR_NAME, sequenceName = "SQ_CO_DEFINICAO_PROCESSO", schema = Constants.SCHEMA)
@Table(name = "TB_DEFINICAO_PROCESSO", schema = Constants.SCHEMA,
        indexes = {
                @Index(columnList = "NO_CLASSE_JAVA ASC", name = "IX_CLASSE_DEFINICAO")
        })
public class FlowDefinitionEntity extends
        AbstractFlowDefinitionEntity<ModuleEntity, CategoryEntity, TaskDefinitionEntity, RoleDefinitionEntity, FlowVersionEntity> {

    private static final long serialVersionUID = 1L;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flowDefinition")
    private List<FlowVersionEntity> flowVersions;

    public List<FlowVersionEntity> getFlowVersions() {
        return flowVersions;
    }

    public void setFlowVersions(List<FlowVersionEntity> flowVersions) {
        this.flowVersions = flowVersions;
    }
}
