/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.geral;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums.SimNao;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.generic.MedEntity;
import br.net.mirante.singular.persistence.entity.BaseEntity;
import br.net.mirante.singular.support.persistence.util.GenericEnumUserType;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_EMPRESA_INTERNACIONAL", schema = "DBGERAL")
public class EmpresaInternacional extends BaseEntity implements MedEntity<Long> {

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
    public Serializable getCod() {
        return id;
    }

    public SimNao getAtivo() {
        return ativo;
    }

    public void setAtivo(SimNao ativo) {
        this.ativo = ativo;
    }
}