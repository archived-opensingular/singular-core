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


import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.opensingular.form.exemplos.notificacaosimplificada.domain.Schemas;


@Entity
@Table(schema = Schemas.DBCORPORATIVO, name = "TB_PESSOA_JURIDICA")
@DiscriminatorValue("J")
@PrimaryKeyJoinColumn(name = "ID_PESSOA_JURIDICA", referencedColumnName = "ID_PESSOA")
public class PessoaJuridicaNS extends PessoaNS {

    @Column(name = "NO_RAZAO_SOCIAL")
    private String razaoSocial;

    @Column(name = "NO_FANTASIA")
    private String nomeFantasia;

    @Column(name = "NU_CNPJ")
    private String cnpj;

    @Override
    public String getNome() {
        return razaoSocial;
    }

    @Override
    public String getCpfCnpj() {
        return cnpj;
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

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
}
