package br.net.mirante.singular.server.commons.persistence.entity.form;

import br.net.mirante.singular.form.persistence.entity.FormAnnotationVersionEntity;
import br.net.mirante.singular.form.persistence.entity.FormVersionEntity;
import br.net.mirante.singular.persistence.entity.TaskInstanceEntity;
import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import br.net.mirante.singular.support.persistence.util.Constants;
import br.net.mirante.singular.support.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_VERSAO_FORMULARIO")
    private FormVersionEntity formVersionEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ANOTACAO_FORMULARIO")
    private FormAnnotationVersionEntity formAnnotationVersionEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_INSTANCIA_TAREFA")
    private TaskInstanceEntity taskInstanceEntity;

    @Column(name = "CO_AUTOR")
    private Long autor;

    @Column(name = "CO_PETICIONANTE")
    private Long peticionante;

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

    public FormVersionEntity getFormVersionEntity() {
        return formVersionEntity;
    }

    public void setFormVersionEntity(FormVersionEntity formVersionEntity) {
        this.formVersionEntity = formVersionEntity;
    }

    public FormAnnotationVersionEntity getFormAnnotationVersionEntity() {
        return formAnnotationVersionEntity;
    }

    public void setFormAnnotationVersionEntity(FormAnnotationVersionEntity formAnnotationVersionEntity) {
        this.formAnnotationVersionEntity = formAnnotationVersionEntity;
    }

    public TaskInstanceEntity getTaskInstanceEntity() {
        return taskInstanceEntity;
    }

    public void setTaskInstanceEntity(TaskInstanceEntity taskInstanceEntity) {
        this.taskInstanceEntity = taskInstanceEntity;
    }

    public Long getAutor() {
        return autor;
    }

    public void setAutor(Long autor) {
        this.autor = autor;
    }

    public Long getPeticionante() {
        return peticionante;
    }

    public void setPeticionante(Long peticionante) {
        this.peticionante = peticionante;
    }

}