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

package org.opensingular.server.commons.persistence.entity.form;

import org.hibernate.annotations.GenericGenerator;
import org.opensingular.flow.persistence.entity.ProcessDefinitionEntity;
import org.opensingular.flow.persistence.entity.ProcessInstanceEntity;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.enums.SimNao;
import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;

import javax.persistence.*;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

@Entity
@Table(schema = Constants.SCHEMA, name = "TB_REQUISICAO")
@GenericGenerator(name = PetitionEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
public class PetitionEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_REQUISICAO";

    @Id
    @Column(name = "CO_REQUISICAO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @ManyToOne
    @JoinColumn(name = "CO_INSTANCIA_PROCESSO")
    private ProcessInstanceEntity processInstanceEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_DEFINICAO_PROCESSO")
    private ProcessDefinitionEntity processDefinitionEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_REQUISITANTE")
    private PetitionerEntity petitioner;

    @Column(name = "DS_REQUISICAO")
    private String description;

    @OneToMany(mappedBy = "petition", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderBy(" CO_FORMULARIO_REQUISICAO ASC ")
    private SortedSet<FormPetitionEntity> formPetitionEntities;

    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public ProcessInstanceEntity getProcessInstanceEntity() {
        return processInstanceEntity;
    }

    public void setProcessInstanceEntity(ProcessInstanceEntity processInstanceEntity) {
        this.processInstanceEntity = processInstanceEntity;
    }

    public ProcessDefinitionEntity getProcessDefinitionEntity() {
        return processDefinitionEntity;
    }

    public void setProcessDefinitionEntity(ProcessDefinitionEntity processDefinitionEntity) {
        this.processDefinitionEntity = processDefinitionEntity;
    }

    public PetitionerEntity getPetitioner() {
        return petitioner;
    }

    public void setPetitioner(PetitionerEntity petitioner) {
        this.petitioner = petitioner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SortedSet<FormPetitionEntity> getFormPetitionEntities() {
        if (formPetitionEntities == null) {
            formPetitionEntities = new TreeSet<>();
        }
        return formPetitionEntities;
    }

    public void setFormPetitionEntities(SortedSet<FormPetitionEntity> formPetitionEntities) {
        this.formPetitionEntities = formPetitionEntities;
    }

    public FormEntity getMainForm() {
        if (formPetitionEntities == null) {
            return null;
        } else {
            return formPetitionEntities.stream()
                    .filter(f -> SimNao.SIM.equals(f.getMainForm()))
                    .map(f -> {
                        if (f.getForm() != null) {
                            return f.getForm();
                        }
                        if (f.getCurrentDraftEntity() != null) {
                            return f.getCurrentDraftEntity().getForm();
                        }
                        return null;
                    })
                    .findFirst()
                    .orElse(null);
        }
    }

    public DraftEntity currentEntityDraftByType(String typeName) {
        return Optional
                .ofNullable(formPetitionEntities)
                .orElse(new TreeSet<>())
                .stream()
                .filter(f -> f.getCurrentDraftEntity() != null && f.getCurrentDraftEntity().getForm().getFormType().getAbbreviation().equals(typeName))
                .findFirst()
                .map(FormPetitionEntity::getCurrentDraftEntity)
                .orElse(null);

    }

}