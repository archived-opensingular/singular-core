/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.exemplos.canabidiol;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.util.brasil.STypeTelefoneNacional;
import br.net.mirante.singular.form.mform.util.comuns.STypeEMail;

@SInfoType(spackage = SPackagePeticaoCanabidiol.class)
public class STypeContato extends STypeComposite<SIComposite> {


    public STypeEMail email;
    public STypeTelefoneNacional telefoneFixo;
    public STypeTelefoneNacional celular;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this.as(AtrBasic::new)
                .label("Contato");

        telefoneFixo = this.addField("telefonefixo", STypeTelefoneNacional.class);
        telefoneFixo
                .as(AtrBasic::new)
                .label("Telefone Fixo")
                .as(AtrBootstrap::new)
                .colPreference(2);

        celular = this.addField("celular", STypeTelefoneNacional.class);
        celular
                .as(AtrBasic::new)
                .label("Celular")
                .as(AtrBootstrap::new)
                .colPreference(2);

        email = addFieldEmail("email");
        email
                .as(AtrCore::new)
                .obrigatorio()
                .as(AtrBasic::new)
                .label("E-mail")
                .as(AtrBootstrap::new)
                .colPreference(4);

    }

}
