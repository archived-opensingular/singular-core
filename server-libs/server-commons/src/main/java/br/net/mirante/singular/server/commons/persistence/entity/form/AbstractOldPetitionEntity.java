package br.net.mirante.singular.server.commons.persistence.entity.form;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import br.net.mirante.singular.persistence.entity.ProcessInstanceEntity;
import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import br.net.mirante.singular.support.persistence.util.Constants;

@Deprecated
@MappedSuperclass
@Table(schema = Constants.SCHEMA, name = "TB_PETICAO_OLD")
public class AbstractOldPetitionEntity extends BaseEntity<Long> {

    @Id
    @Column(name = "CO_PETICAO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cod;

    @Column(name = "TP_PETICAO")
    private String type;

    @Column(name = "TP_PROCESSO_PETICAO")
    private String processType;


    @Column(name = "NO_PROCESSO")
    private String processName;

    @Column(name = "CO_FORMULARIO")
    private Long codForm;
    
    @Column(name = "DS_PETICAO")
    private String description;

    @Column(name = "DT_CRIACAO")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @ManyToOne
    @JoinColumn(name = "CO_INSTANCIA_PROCESSO")
    private ProcessInstanceEntity processInstanceEntity;

    @Version
    @Column(name = "DT_EDICAO")
    @Temporal(TemporalType.TIMESTAMP)
    private Date editionDate;

    public AbstractOldPetitionEntity() {
    }

    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }


    public ProcessInstanceEntity getProcessInstanceEntity() {
        return processInstanceEntity;
    }

    public void setProcessInstanceEntity(ProcessInstanceEntity processInstanceEntity) {
        this.processInstanceEntity = processInstanceEntity;
    }

    public Date getEditionDate() {
        return editionDate;
    }

    public void setEditionDate(Date editionDate) {
        this.editionDate = editionDate;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public Long getCodForm() {
        return codForm;
    }

    public void setCodForm(Long codForm) {
        this.codForm = codForm;
    }
    
    
}
