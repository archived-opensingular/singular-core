/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.exemplos.emec.credenciamentoescolagoverno.form;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.country.brazil.STypeCPF;
import br.net.mirante.singular.form.type.country.brazil.STypeTelefoneNacional;
import br.net.mirante.singular.form.type.util.STypeEMail;
import br.net.mirante.singular.form.view.SViewListByForm;
import br.net.mirante.singular.form.view.SViewListByTable;

@SInfoType(spackage = SPackageCredenciamentoEscolaGoverno.class)
public class STypeCorpoDirigente extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeList<STypeComposite<SIComposite>, SIComposite> corpoDirigenteMembrosCPA = this.addFieldListOfComposite("corpoDirigenteMembrosCPA", "membro");
        corpoDirigenteMembrosCPA
                .withMiniumSizeOf(1)
                .withView(SViewListByForm::new)
                .asAtr().label("Corpo Dirigente e/ou Membro CPA").itemLabel("Membro");

        final STypeComposite<SIComposite> membro = corpoDirigenteMembrosCPA.getElementsType();
        membro.addField("cpf", STypeCPF.class)
                .asAtr().required()
                .asAtrBootstrap().colPreference(2);
        membro.addFieldString("nome", true)
                .asAtr().label("Nome")
                .asAtrBootstrap().colPreference(7);
        membro.addField("sexo", STypeSexo.class)
                .asAtr().required()
                .asAtrBootstrap().colPreference(3);
        membro.addFieldInteger("numeroRG", true)
                .asAtr().label("RG")
                .asAtrBootstrap().colPreference(4);
        membro.addFieldString("orgaoExpedidorRG", true)
                .asAtr().label("Órgão Expedidor")
                .asAtrBootstrap().colPreference(4);
        membro.addField("ufRG", STypeEstado.class)
                .asAtr().required()
                .asAtrBootstrap().colPreference(4);
        membro.addFieldListOf("telefones", STypeTelefoneNacional.class)
                .withView(SViewListByTable::new)
                .asAtr().label("Telefones").itemLabel("Telefone").required()
                .asAtrBootstrap().colPreference(3).newRow();

        membro.addField("fax", STypeTelefoneNacional.class)
                .asAtr().label("Fax")
                .asAtrBootstrap().colPreference(3);
        membro.addField("email", STypeEMail.class)
                .asAtr().required();
        membro.addFieldString("cargo", true)
                .asAtr().label("Cargo")
                .asAtrBootstrap().colPreference(6);

        membro.addFieldBoolean("membroCPA", true)
                .asAtr().label("Membro CPA")
                .asAtrBootstrap().newRow();
        membro.addFieldBoolean("coordenadorCPA", true)
                .asAtr().label("Coordenador CPA")
                .asAtrBootstrap().newRow();
        membro.addFieldBoolean("dirigente", true)
                .asAtr().label("Dirigente")
                .asAtrBootstrap().newRow();
    }

}
