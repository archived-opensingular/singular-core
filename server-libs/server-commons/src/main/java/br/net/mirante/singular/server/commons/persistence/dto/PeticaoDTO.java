package br.net.mirante.singular.server.commons.persistence.dto;

import java.io.Serializable;
import java.util.Date;

public class PeticaoDTO implements Serializable {

    private static final long serialVersionUID = -5971923724359749797L;

    private Long cod;
    private String description;
    private String situation;
    private String processName;
    private Date creationDate;
    private String type;
    private String processType;
    private String processNumber;
    private Date situationBeginDate;
    private Date processBeginDate;
    private Date editionDate;

    public PeticaoDTO() {
    }

    public PeticaoDTO(Long cod, String description, String situation,
                      String processName, Date creationDate, String type, String processType,
                      String processNumber, Date situationBeginDate, Date processBeginDate,
                      Date editionDate) {
        this.cod = cod;
        this.description = description;
        this.situation = situation;
        this.processName = processName;
        this.creationDate = creationDate;
        this.type = type;
        this.processType = processType;
        this.processNumber = processNumber;
        this.situationBeginDate = situationBeginDate;
        this.processBeginDate = processBeginDate;
        this.editionDate = editionDate;
    }

    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProcessNumber() {
        return processNumber;
    }

    public void setProcessNumber(String processNumber) {
        this.processNumber = processNumber;
    }

    public Date getSituationBeginDate() {
        return situationBeginDate;
    }

    public void setSituationBeginDate(Date situationBeginDate) {
        this.situationBeginDate = situationBeginDate;
    }

    public Date getProcessBeginDate() {
        return processBeginDate;
    }

    public void setProcessBeginDate(Date processBeginDate) {
        this.processBeginDate = processBeginDate;
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
}
