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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;

/**
 * The persistent class for the TB_DEFINICAO_PROCESSO database table.
 */
@Entity
@GenericGenerator(name = AbstractProcessDefinitionEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_DEFINICAO_PROCESSO", schema = Constants.SCHEMA)
public class ProcessDefinitionEntity extends AbstractProcessDefinitionEntity<ProcessGroupEntity,CategoryEntity, TaskDefinitionEntity, RoleDefinitionEntity, ProcessVersionEntity> {

    private static final long serialVersionUID = 1L;
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(schema = Constants.SCHEMA, name = "TB_VERSAO_PROCESSO", 
        joinColumns = @JoinColumn(name = "CO_DEFINICAO_PROCESSO", referencedColumnName = "CO_DEFINICAO_PROCESSO"),
        inverseJoinColumns = @JoinColumn(name = "CO_VERSAO_PROCESSO", referencedColumnName = "CO_VERSAO_PROCESSO"))
    private List<ProcessInstanceEntity> processInstances;

    public List<ProcessInstanceEntity> getProcessInstances() {
        return processInstances;
    }

    public void setProcessInstances(List<ProcessInstanceEntity> processInstances) {
        this.processInstances = processInstances;
    }
}
