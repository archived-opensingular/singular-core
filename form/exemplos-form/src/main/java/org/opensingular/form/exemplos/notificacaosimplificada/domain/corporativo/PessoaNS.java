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

package org.opensingular.form.exemplos.notificacaosimplificada.domain.corporativo;


import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import org.opensingular.form.exemplos.notificacaosimplificada.domain.Schemas;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.geral.Cidade;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.corporativo.enums.TipoPessoaNS;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.geral.Pais;
import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.GenericEnumUserType;

@Entity
@Table(schema = Schemas.DBCORPORATIVO, name = "TB_PESSOA")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TP_PESSOA")
public abstract class PessoaNS extends BaseEntity<String> {

    @Id
    @Column(name = "ID_PESSOA")
    private String cod;

    @Type(type = GenericEnumUserType.CLASS_NAME, parameters = {
            @Parameter(name = "enumClass", value = TipoPessoaNS.CLASS_NAME),
            @Parameter(name = "identifierMethod", value = "getCod"),
            @Parameter(name = "valueOfMethod", value = "valueOfEnum")})
    @Column(name = "TP_PESSOA", insertable = false, updatable = false)
    private TipoPessoaNS tipoPessoa;

    @JoinColumn(name = "CO_CIDADE", nullable = false)
    @ManyToOne
    private Cidade cidade;

    @Column(name = "DS_QUALIFICACAO_ENDERECO", length = 60)
    private String qualificacaoEndereco;

    @Column(name = "DS_ENDERECO", length = 150)
    private String endereco;

    @Column(name = "NO_BAIRRO", length = 60)
    private String bairro;

    @Column(name = "NU_CEP", length = 12)
    private String cep;

    @JoinColumn(name = "CO_PAIS", nullable = false)
    @ManyToOne
    private Pais pais;

    @Override
    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public TipoPessoaNS getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoaNS tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public abstract String getNome();

    public abstract String getCpfCnpj();

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public String getQualificacaoEndereco() {
        return qualificacaoEndereco;
    }

    public void setQualificacaoEndereco(String qualificacaoEndereco) {
        this.qualificacaoEndereco = qualificacaoEndereco;
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

    public Pais getPais() {
        return pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
    }

    public String getEnderecoCompleto() {
        return String.format("%s, %s, %s - %s",
                getEndereco(),
                getBairro(),
                Optional.ofNullable(getCidade()).map(Cidade::getNome).orElse(""),
                Optional.ofNullable(getPais()).map(Pais::getNome).orElse(""));
    }
}
