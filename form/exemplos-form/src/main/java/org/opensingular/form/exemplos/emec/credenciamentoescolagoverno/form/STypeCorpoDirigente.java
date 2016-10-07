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
import org.opensingular.form.STypeList;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeBoolean;
import org.opensingular.form.type.country.brazil.STypeCPF;
import org.opensingular.form.type.country.brazil.STypeTelefoneNacional;
import org.opensingular.form.type.util.STypeEMail;
import org.opensingular.form.util.transformer.Value;
import org.opensingular.form.view.SViewByBlock;
import org.opensingular.form.view.SViewListByForm;

@SInfoType(spackage = SPackageCredenciamentoEscolaGoverno.class)
public class STypeCorpoDirigente extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeList<STypeComposite<SIComposite>, SIComposite> corpoDirigenteMembrosCPA = this.addFieldListOfComposite("corpoDirigenteMembrosCPA", "membro");

        corpoDirigenteMembrosCPA
                .withMiniumSizeOf(3);

        corpoDirigenteMembrosCPA
                .withView(SViewListByForm::new)
                .asAtr()
                .label("Corpo Dirigente e/ou Membro CPA")
                .itemLabel("Membro")
                .required();

        final STypeComposite<SIComposite> membro = corpoDirigenteMembrosCPA.getElementsType();

        membro
                .addFieldString("nome", true)
                .asAtr()
                .label("Nome")
                .asAtrBootstrap()
                .colPreference(6);

        membro.
                addField("cpf", STypeCPF.class)
                .asAtr()
                .required()
                .asAtrBootstrap()
                .colPreference(3);

        membro
                .addField("sexo", STypeSexo.class)
                .asAtr()
                .required()
                .asAtrBootstrap()
                .colPreference(3);

        membro
                .addFieldInteger("numeroRG", true)
                .asAtr()
                .label("RG")
                .asAtrBootstrap()
                .colPreference(3);

        membro
                .addFieldString("orgaoExpedidorRG", true)
                .asAtr()
                .label("Órgão Expedidor")
                .asAtrBootstrap()
                .colPreference(3);

        membro
                .addField("ufRG", STypeEstado.class)
                .asAtr()
                .required()
                .asAtrBootstrap()
                .colPreference(3);

        membro
                .addField("email", STypeEMail.class)
                .asAtr()
                .required()
                .asAtrBootstrap()
                .newRow();

        membro
                .addField("fax", STypeTelefoneNacional.class)
                .asAtr()
                .label("Fax")
                .asAtrBootstrap()
                .colPreference(3);

        membro
                .addField("telefone", STypeTelefoneNacional.class)
                .asAtr()
                .label("Telefones")
                .required()
                .asAtrBootstrap()
                .colPreference(3);

        membro
                .addFieldString("cargo", true)
                .asAtr()
                .label("Cargo")
                .asAtrBootstrap()
                .colPreference(6);

        final STypeBoolean membroCPA      = membro.addFieldBoolean("membroCPA", true);
        final STypeBoolean coordenadorCPA = membro.addFieldBoolean("coordenadorCPA", true);
        final STypeBoolean dirigente      = membro.addFieldBoolean("dirigente", true);

        membroCPA
                .asAtr()
                .label("Membro CPA")
                .asAtrBootstrap()
                .colPreference(2)
                .newRow();

        coordenadorCPA
                .asAtr()
                .label("Coordenador CPA")
                .asAtrBootstrap()
                .colPreference(2);

        dirigente
                .asAtr()
                .label("Dirigente")
                .asAtrBootstrap()
                .colPreference(2);

        corpoDirigenteMembrosCPA.addInstanceValidator(validatable -> {
            if (validatable.getInstance().stream().filter(membro_ -> Value.of(membro_, membroCPA).booleanValue()).count() >= 3
                    && validatable.getInstance().stream().filter(membro_ -> Value.of(membro_, coordenadorCPA).booleanValue()).count() >= 1) {
                validatable.error("É necessário ter no mínimo três membros e um coordenador do CPA definidos");
            }
        });

        this.withView(new SViewByBlock(), v -> {
            v.newBlock().add(corpoDirigenteMembrosCPA);
        });
    }

}
