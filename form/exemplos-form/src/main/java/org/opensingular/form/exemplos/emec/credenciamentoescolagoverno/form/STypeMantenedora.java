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
package org.opensingular.form.exemplos.emec.credenciamentoescolagoverno.form;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.country.brazil.STypeCEP;
import org.opensingular.form.type.country.brazil.STypeCNPJ;
import org.opensingular.form.type.country.brazil.STypeCPF;
import org.opensingular.form.type.country.brazil.STypeTelefoneNacional;
import org.opensingular.form.type.util.STypeEMail;
import org.opensingular.form.view.SViewByBlock;

@SInfoType(spackage = SPackageCredenciamentoEscolaGoverno.class)
public class STypeMantenedora extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        addDadosMantenedora();
        addRepresentanteLegal();

        // cria um bloco por campo
        setView(SViewByBlock::new)
                .newBlock("Dados da Mantenedora").add("dadosMantenedora")
                .newBlock("Representante Legal").add("representanteLegal");
    }

    private void addDadosMantenedora() {
        final STypeComposite<SIComposite> dadosMantenedora = this.addFieldComposite("dadosMantenedora");
        dadosMantenedora.addFieldString("nomeMantenedora", true)
                .asAtr().label("Nome da Mantenedora")
                .asAtrBootstrap().colPreference(9);
        dadosMantenedora.addField("cnpj", STypeCNPJ.class)
                .asAtr().required()
                .asAtrBootstrap().colPreference(3);
        dadosMantenedora.addFieldString("categoriaAdministrativa", true)
                .withSelectView().selectionOf("Pessoa Jurídica de Direito Privado - Sem fins lucrativos - Fundação")
                .asAtr().label("Categoria Administrativa")
                .asAtrBootstrap().colPreference(12);
        dadosMantenedora.addFieldString("endereco", true)
                .asAtr().label("Endereço")
                .asAtrBootstrap().colPreference(9);
        dadosMantenedora.addFieldInteger("enderecoNumero")
                .asAtr().label("Nº")
                .asAtrBootstrap().colPreference(3);
        dadosMantenedora.addFieldString("enderecoComplemento")
                .asAtr().label("Complemento")
                .asAtrBootstrap().colPreference(9);
        dadosMantenedora.addFieldString("enderecoBairro", true)
                .asAtr().label("Bairro")
                .asAtrBootstrap().colPreference(3);

        STypeEstado enderecoEstado = dadosMantenedora.addField("enderecoEstado", STypeEstado.class);
        enderecoEstado
                .asAtr().required()
                .asAtrBootstrap().colPreference(4);
        dadosMantenedora.addField("enderecoMunicipio", STypeMunicipio.class)
                .selectionByUF(enderecoEstado)
                .asAtr().required()
                .asAtrBootstrap().colPreference(5);
        dadosMantenedora.addField("enderecoCEP", STypeCEP.class)
                .asAtrBootstrap().colPreference(3);

        dadosMantenedora
                .addField("email", STypeEMail.class)
                .asAtr()
                .required()
                .asAtrBootstrap()
                .colPreference(6);

        dadosMantenedora
                .addFieldInteger("enderecoCaixaPostal", false)
                .asAtr()
                .label("Caixa Postal")
                .asAtrBootstrap()
                .colPreference(2);

        dadosMantenedora
                .addField("fax", STypeTelefoneNacional.class)
                .asAtr()
                .label("Fax")
                .asAtrBootstrap()
                .colPreference(2);

        dadosMantenedora
                .addField("telefone", STypeTelefoneNacional.class)
                .asAtr()
                .label("Telefone")
                .required()
                .asAtrBootstrap()
                .colPreference(2);

    }

    private void addRepresentanteLegal() {

        final STypeComposite<SIComposite> representanteLegal = this.addFieldComposite("representanteLegal");

        representanteLegal
                .addFieldString("nome", true)
                .asAtr()
                .label("Nome")
                .asAtrBootstrap()
                .colPreference(6);

        representanteLegal
                .addField("cpf", STypeCPF.class)
                .asAtr()
                .required()
                .asAtrBootstrap()
                .colPreference(3);

        representanteLegal
                .addField("sexo", STypeSexo.class)
                .asAtr()
                .required()
                .asAtrBootstrap()
                .colPreference(3);

        representanteLegal
                .addFieldInteger("numeroRG", true)
                .asAtr()
                .label("RG")
                .asAtrBootstrap()
                .colPreference(3);

        representanteLegal
                .addFieldString("orgaoExpedidorRG", true)
                .asAtr()
                .label("Órgão Expedidor")
                .asAtrBootstrap()
                .colPreference(3);

        representanteLegal
                .addField("ufRG", STypeEstado.class)
                .asAtr()
                .required()
                .asAtrBootstrap()
                .colPreference(3);

        representanteLegal
                .addField("email", STypeEMail.class)
                .asAtr()
                .required()
                .asAtrBootstrap()
                .colPreference(6)
                .newRow();

        representanteLegal
                .addField("fax", STypeTelefoneNacional.class)
                .asAtr()
                .label("Fax")
                .asAtrBootstrap()
                .colPreference(3);

        representanteLegal
                .addField("telefone", STypeTelefoneNacional.class)
                .asAtr()
                .label("Telefone")
                .required()
                .asAtrBootstrap()

                .colPreference(3);

        representanteLegal
                .addFieldString("cargo", true)
                .asAtr()
                .label("Cargo")
                .asAtrBootstrap()
                .colPreference(6);

    }
}
