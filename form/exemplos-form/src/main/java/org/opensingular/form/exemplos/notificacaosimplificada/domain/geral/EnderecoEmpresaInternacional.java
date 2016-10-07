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

import java.util.Date;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import org.opensingular.lib.support.persistence.entity.BaseEntity;

@Entity
@Table(name = "TB_ENDERECO_EMP_INTERNACIONAL", schema = "DBGERAL")
@XmlRootElement(name = "enderecoEmpresaInternacional", namespace = "http://www.anvisa.gov.br/geral/schema/domains")
public class EnderecoEmpresaInternacional extends BaseEntity<EnderecoEmpresaInternacionalId> {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private EnderecoEmpresaInternacionalId id;
   
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CO_EMPRESA_INTERNACIONAL", nullable = false, insertable = false, updatable = false)
    private EmpresaInternacional empresaInternacional;
   
    @Column(name = "DS_RUA_EMPRESA", length = 100)
    private String rua;
   
    @Column(name = "NU_ENDERECO_EMPRESA", length = 10)
    private String endereco;
   
    @Column(name = "NO_BAIRRO_EMPRESA", length = 30)
    private String bairro;
   
    @Column(name = "NU_CEP_EMPRESA", length = 15)
    private String cep;
   
    @Column(name = "NU_TELEFONE_EMPRESA", length = 15)
    private String telefone;
   
    @Column(name = "NU_FAX_EMPRESA", length = 15)
    private String fax;
   
    @Column(name = "DS_EMAIL_EMPRESA", length = 70)
    private String email;
   
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CADASTRAMENTO", length = 7, columnDefinition="date")
    private Date dataCadastramento;
   
    @JoinColumn(name = "CO_CIDADE" )
    @ManyToOne
    private Cidade cidade;
   
    @JoinColumn(name = "CO_PAIS" )
    @ManyToOne
    private Pais pais;
   
    @Column(name = "DS_CIDADE_ESTRANGEIRA" )
    private String cidadeEstrangeira;

    public EnderecoEmpresaInternacionalId getId() {
        return id;
    }

    public void setId(EnderecoEmpresaInternacionalId id) {
        this.id = id;
    }

    public EmpresaInternacional getEmpresaInternacional() {
        return empresaInternacional;
    }

    public void setEmpresaInternacional(EmpresaInternacional empresaInternacional) {
        this.empresaInternacional = empresaInternacional;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDataCadastramento() {
        return dataCadastramento;
    }

    public void setDataCadastramento(Date dataCadastramento) {
        this.dataCadastramento = dataCadastramento;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public String getCidadeEstrangeira() {
        return cidadeEstrangeira;
    }

    public void setCidadeEstrangeira(String cidadeEstrangeira) {
        this.cidadeEstrangeira = cidadeEstrangeira;
    }

    public String getEnderecoCompleto() {
        return String.format("%s, %s, %s - %s",
                getRua(),
                getBairro(),
                Optional.ofNullable(getCidade()).map(Cidade::getNome).orElse(""),
                Optional.ofNullable(getPais()).map(Pais::getNome).orElse(""));
    }

    @Override
    public EnderecoEmpresaInternacionalId getCod() {
        return id;
    }
}
