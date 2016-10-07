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

package org.opensingular.form.exemplos.notificacaosimplificada.domain.generic;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import org.opensingular.form.exemplos.notificacaosimplificada.domain.TipoTermo;
import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.enums.SimNao;
import org.opensingular.lib.support.persistence.util.GenericEnumUserType;

/**
 * Classe marcadora para todos os tipos de vocabulario controlado Todo
 * vocabulario controlado deve herdar desta classeConformeMatriz, direta ou indiretamente, por
 * questões de compatibilidade com os serviços que serão expostos
 */
@XmlRootElement(name = "vocabulario-controlado", namespace = "http://www.anvisa.gov.br/reg-med/schema/domains")
@XmlType(name = "vocabulario-controlado", namespace = "http://www.anvisa.gov.br/reg-med/schema/domains")
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "TB_VOCABULARIO_CONTROLADO", schema = "DBMEDICAMENTO")
@Entity
@Filter(name = "VocabulariosAtivos", condition = "ativa = \"S\"")
public abstract class VocabularioControlado extends BaseEntity<Long> implements MedEntity<Long> {

    private static final long serialVersionUID = 496526748207612785L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_VOCABULARIOCONTROLADO")
    @SequenceGenerator(sequenceName = "DBMEDICAMENTO.SQ_COSEQVOCABULARIOCONTROLADO", name = "SEQ_VOCABULARIOCONTROLADO", initialValue = 1, allocationSize = 1)
    @Column(name = "CO_SEQ_VOCABULARIO_CONTROLADO", unique = true, nullable = false, precision = 8, scale = 0)
    protected Long id;

    @Column(name = "DS_DESCRICAO", unique = true, nullable = false, length = 200)
    protected String descricao;

    @Column(name = "ST_REGISTRO_ATIVO", nullable = false, length = 1)
    @Type(type = GenericEnumUserType.CLASS_NAME, parameters = {
            @Parameter(name = "enumClass", value = SimNao.ENUM_CLASS_NAME),
            @Parameter(name = "identifierMethod", value = "getCodigo"),
            @Parameter(name = "valueOfMethod", value = "valueOfEnum")
    })
    protected SimNao ativa;

    @ManyToOne
    @JoinColumn(name = "CO_TIPO_TERMO")
    protected TipoTermo tipoTermo;

    @Column(name = "DS_JUSTIFICATIVA_EXCLUSAO", length = 1000)
    protected String justificativaExclusao;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CRIACAO", columnDefinition = "date")
    protected Date dataCriacao;

    public VocabularioControlado() {
    }

    public VocabularioControlado(Long id, String descricao, SimNao ativa,
                                 TipoTermo tipoTermo) {
        super();
        this.id = id;
        this.descricao = descricao;
        this.ativa = ativa;
        this.tipoTermo = tipoTermo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @XmlTransient
    public SimNao getAtiva() {
        return ativa;
    }

    public void setAtiva(SimNao ativa) {
        this.ativa = ativa;
    }

    @XmlTransient
    public TipoTermo getTipoTermo() {
        return tipoTermo;
    }

    public void setTipoTermo(TipoTermo tipoTermo) {
        this.tipoTermo = tipoTermo;
    }

    public String getJustificativaExclusao() {
        return justificativaExclusao;
    }

    public void setJustificativaExclusao(String justificativaExclusao) {
        this.justificativaExclusao = justificativaExclusao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    @Override
    public int hashCode() {
        final int prime  = 31;
        int       result = 1;
        result = prime * result
                + ((descricao == null) ? 0 : descricao.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        VocabularioControlado other = (VocabularioControlado) obj;
        if (descricao == null) {
            if (other.descricao != null) {
                return false;
            }
        } else if (!descricao.equals(other.descricao)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @Override
    public Long getCod() {
        return id;
    }
}
