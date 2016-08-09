package br.net.mirante.singular.server.commons.persistence.entity.form;

import br.net.mirante.singular.flow.core.entity.IEntityProcessDefinition;
import br.net.mirante.singular.form.persistence.entity.FormAnnotationVersionEntity;
import br.net.mirante.singular.form.persistence.entity.FormVersionEntity;
import br.net.mirante.singular.persistence.entity.ProcessDefinitionEntity;
import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;
import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import br.net.mirante.singular.support.persistence.util.Constants;
import br.net.mirante.singular.support.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

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
    @JoinColumn(name = "CO_VERSAO_FORMULARIO_ATUAL")
    private FormVersionEntity currentFormVersionEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_ANOTACAO_FORMULARIO_ATUAL")
    private FormAnnotationVersionEntity currentFormAnnotationVersionEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_DEFINICAO_PROCESSO")
    private IEntityProcessDefinition processDefinitionEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_RASCUNHO_ATUAL")
    private DraftEntity currentDraftEntity;

    @Column(name = "CO_PETICIONANTE")
    private Long peticionante;

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

    public FormVersionEntity getCurrentFormVersionEntity() {
        return currentFormVersionEntity;
    }

    public void setCurrentFormVersionEntity(FormVersionEntity currentFormVersionEntity) {
        this.currentFormVersionEntity = currentFormVersionEntity;
    }

    public FormAnnotationVersionEntity getCurrentFormAnnotationVersionEntity() {
        return currentFormAnnotationVersionEntity;
    }

    public void setCurrentFormAnnotationVersionEntity(FormAnnotationVersionEntity currentFormAnnotationVersionEntity) {
        this.currentFormAnnotationVersionEntity = currentFormAnnotationVersionEntity;
    }

    public IEntityProcessDefinition getProcessDefinitionEntity() {
        return processDefinitionEntity;
    }

    public void setProcessDefinitionEntity(IEntityProcessDefinition processDefinitionEntity) {
        this.processDefinitionEntity = processDefinitionEntity;
    }

    public DraftEntity getCurrentDraftEntity() {
        return currentDraftEntity;
    }

    public void setCurrentDraftEntity(DraftEntity currentDraftEntity) {
        this.currentDraftEntity = currentDraftEntity;
    }

    public Long getPeticionante() {
        return peticionante;
    }

    public void setPeticionante(Long peticionante) {
        this.peticionante = peticionante;
    }
}