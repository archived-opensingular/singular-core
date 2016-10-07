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

package org.opensingular.form.exemplos.notificacaosimplificada.domain.geral;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import org.opensingular.form.exemplos.notificacaosimplificada.domain.generic.MedEntity;
import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.enums.SimNao;
import org.opensingular.lib.support.persistence.util.GenericEnumUserType;

@Entity
@Table(name = "TB_EMPRESA_INTERNACIONAL", schema = "DBGERAL")
public class EmpresaInternacional extends BaseEntity<Long> implements MedEntity<Long> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_SEQ_EMPRESA_INTERNACIONAL", nullable = false, precision = 6, scale = 0)
    private Long id;

    @Column(name = "NO_RAZAO_SOCIAL", nullable = false, length = 120)
    private String razaoSocial;

    @Column(name = "NO_FANTASIA", length = 120)
    private String nomeFantasia;

    @Column(name = "CO_TIPO_EMPRESA", precision = 3, scale = 0)
    private Short codigoTipoEmpresa;

    @Column(name = "ST_REGISTRO_ATIVO", length = 1)
    @Type(type = GenericEnumUserType.CLASS_NAME, parameters = {
            @Parameter(name = "enumClass", value = SimNao.ENUM_CLASS_NAME),
            @Parameter(name = "identifierMethod", value = "getCodigo"),
            @Parameter(name = "valueOfMethod", value = "valueOfEnum")})
    private SimNao ativo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "empresaInternacional")
    private List<EnderecoEmpresaInternacional> enderecos = new ArrayList<EnderecoEmpresaInternacional>(0);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public Short getCodigoTipoEmpresa() {
        return codigoTipoEmpresa;
    }

    public void setCodigoTipoEmpresa(Short codigoTipoEmpresa) {
        this.codigoTipoEmpresa = codigoTipoEmpresa;
    }

    @XmlTransient
    public List<EnderecoEmpresaInternacional> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<EnderecoEmpresaInternacional> enderecos) {
        this.enderecos = enderecos;
    }

    @Override
    public Long getCod() {
        return id;
    }

    public SimNao getAtivo() {
        return ativo;
    }

    public void setAtivo(SimNao ativo) {
        this.ativo = ativo;
    }
}