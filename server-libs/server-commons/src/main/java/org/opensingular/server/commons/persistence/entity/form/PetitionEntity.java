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

import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.flow.persistence.entity.ProcessDefinitionEntity;
import org.opensingular.flow.persistence.entity.ProcessInstanceEntity;
import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.enums.SimNao;
import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

@Entity
@Table(schema = Constants.SCHEMA, name = "TB_PETICAO")
@GenericGenerator(name = PetitionEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
public class PetitionEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_PETICAO";

    @Id
    @Column(name = "CO_PETICAO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @ManyToOne
    @JoinColumn(name = "CO_INSTANCIA_PROCESSO")
    private ProcessInstanceEntity processInstanceEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_DEFINICAO_PROCESSO")
    private ProcessDefinitionEntity processDefinitionEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_RASCUNHO_ATUAL")
    private DraftEntity currentDraftEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_PETICIONANTE")
    private PetitionerEntity petitioner;

    @Column(name = "DS_PETICAO")
    private String description;

    @OneToMany(mappedBy = "petition", fetch = FetchType.EAGER)
    @OrderBy(" CO_FORMULARIO_PETICAO ASC ")
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

    public DraftEntity getCurrentDraftEntity() {
        return currentDraftEntity;
    }

    public void setCurrentDraftEntity(DraftEntity currentDraftEntity) {
        this.currentDraftEntity = currentDraftEntity;
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
        return formPetitionEntities;
    }

    public void setFormPetitionEntities(SortedSet<FormPetitionEntity> formPetitionEntities) {
        this.formPetitionEntities = formPetitionEntities;
    }

    public FormEntity getMainForm() {
        return Optional
                .ofNullable(formPetitionEntities)
                .orElse(new TreeSet<>())
                .stream()
                .filter(f -> SimNao.SIM.equals(f.getMainForm()))
                .map(FormPetitionEntity::getForm)
                .findFirst()
                .orElse(null);
    }

}