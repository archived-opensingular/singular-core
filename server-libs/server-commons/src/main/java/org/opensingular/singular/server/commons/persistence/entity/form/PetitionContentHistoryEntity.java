package org.opensingular.singular.server.commons.persistence.entity.form;

import org.opensingular.form.persistence.entity.FormAnnotationVersionEntity;
import org.opensingular.flow.persistence.entity.Actor;
import org.opensingular.flow.persistence.entity.TaskInstanceEntity;
import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(schema = Constants.SCHEMA, name = "TB_HISTORICO_CONTEUDO_PETICAO")
@GenericGenerator(name = PetitionContentHistoryEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
public class PetitionContentHistoryEntity extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_HISTORICO";

    @Id
    @Column(name = "CO_HISTORICO")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Long cod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_PETICAO")
    private PetitionEntity petitionEntity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_HISTORICO")
    private Date historyDate;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "RL_HIST_CONT_PET_VER_ANOTACAO", schema = Constants.SCHEMA,
            joinColumns = @JoinColumn(name = "CO_HISTORICO"),
            inverseJoinColumns = @JoinColumn(name = "CO_VERSAO_ANOTACAO"))
    private List<FormAnnotationVersionEntity> formAnnotationsVersions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCIA_TAREFA")
    private TaskInstanceEntity taskInstanceEntity;

    @ManyToOne
    @JoinColumn(name = "CO_AUTOR")
    private Actor actor;

    @ManyToOne
    @JoinColumn(name = "CO_PETICIONANTE")
    private PetitionerEntity petitionerEntity;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "petitionContentHistory")
    private List<FormVersionHistoryEntity> formVersionHistoryEntities;

    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public PetitionEntity getPetitionEntity() {
        return petitionEntity;
    }

    public void setPetitionEntity(PetitionEntity petitionEntity) {
        this.petitionEntity = petitionEntity;
    }

    public Date getHistoryDate() {
        return historyDate;
    }

    public void setHistoryDate(Date historyDate) {
        this.historyDate = historyDate;
    }

    public List<FormAnnotationVersionEntity> getFormAnnotationsVersions() {
        return formAnnotationsVersions;
    }

    public void setFormAnnotationsVersions(List<FormAnnotationVersionEntity> formAnnotationsVersions) {
        this.formAnnotationsVersions = formAnnotationsVersions;
    }

    public TaskInstanceEntity getTaskInstanceEntity() {
        return taskInstanceEntity;
    }

    public void setTaskInstanceEntity(TaskInstanceEntity taskInstanceEntity) {
        this.taskInstanceEntity = taskInstanceEntity;
    }

    public Actor getActor() {
        return actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public PetitionerEntity getPetitionerEntity() {
        return petitionerEntity;
    }

    public void setPetitionerEntity(PetitionerEntity petitionerEntity) {
        this.petitionerEntity = petitionerEntity;
    }

    public List<FormVersionHistoryEntity> getFormVersionHistoryEntities() {
        return formVersionHistoryEntities;
    }

    public void setFormVersionHistoryEntities(List<FormVersionHistoryEntity> formVersionHistoryEntities) {
        this.formVersionHistoryEntities = formVersionHistoryEntities;
    }

}