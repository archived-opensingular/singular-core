/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreSelectProviderPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        final STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        /*
         * Neste caso será utilizado o serviço de nome filesChoiceProvider
         * cadastrado através do Document.bindLocalService
         */
        final STypeComposite<SIComposite> arquivo  = tipoMyForm.addFieldComposite("arquivo");
        final STypeString                 id       = arquivo.addFieldString("id");
        final STypeString                 hashSha1 = arquivo.addFieldString("hashSha1");

        arquivo.asAtr().label("Seleção de Arquivos Persistidos");

        arquivo.selection()
                .id(id)
                .display(hashSha1)
                .simpleProvider("filesChoiceProvider");

    }

}
