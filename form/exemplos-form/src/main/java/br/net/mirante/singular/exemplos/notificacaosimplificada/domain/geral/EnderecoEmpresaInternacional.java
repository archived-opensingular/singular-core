/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.geral;

import java.io.Serializable;
import java.util.Date;

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

import br.net.mirante.singular.persistence.entity.BaseEntity;

@Entity
@Table(name = "TB_ENDERECO_EMP_INTERNACIONAL", schema = "DBGERAL")
@XmlRootElement(name = "enderecoEmpresaInternacional", namespace = "http://www.anvisa.gov.br/geral/schema/domains")
public class EnderecoEmpresaInternacional extends BaseEntity {

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

    @Override
    public Serializable getCod() {
        return id;
    }
}
