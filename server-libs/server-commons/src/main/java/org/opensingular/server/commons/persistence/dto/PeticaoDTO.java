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

package org.opensingular.server.commons.persistence.dto;

import java.io.Serializable;
import java.util.Date;

public class PeticaoDTO implements Serializable {

    private static final long serialVersionUID = -5971923724359749797L;

    private Long codPeticao;
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

    public PeticaoDTO(Long codPeticao, String description, String situation,
                      String processName, Date creationDate, String type, String processType,
                      Date situationBeginDate, Date processBeginDate,
                      Date editionDate) {
        this.codPeticao = codPeticao;
        this.description = description;
        this.situation = situation;
        this.processName = processName;
        this.creationDate = creationDate;
        this.type = type;
        this.processType = processType;
        this.situationBeginDate = situationBeginDate;
        this.processBeginDate = processBeginDate;
        this.editionDate = editionDate;
    }

    public Long getCodPeticao() {
        return codPeticao;
    }

    public void setCodPeticao(Long codPeticao) {
        this.codPeticao = codPeticao;
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
