/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.component.input.core.multiselect;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SMultiSelectionByPicklistView;

public class CaseInputCoreMultiSelectProviderPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        final STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        final STypeList<STypeComposite<SIComposite>, SIComposite> arquivos = tipoMyForm.addFieldListOfComposite("arquivos", "arquivo");

        /*
         * Neste caso será utilizado o serviço de nome filesChoiceProvider
         * cadastrado através do Document.bindLocalService
         */
        final STypeComposite<SIComposite> arquivo  = arquivos.getElementsType();
        final STypeString                 id       = arquivo.addFieldString("id");
        final STypeString                 hashSha1 = arquivo.addFieldString("hashSha1");

        arquivos.asAtr().label("Seleção de Arquivos Persistidos");

        arquivos.selection()
                .id(id)
                .display(hashSha1)
                .simpleProvider("filesChoiceProvider");
        arquivos.withView(SMultiSelectionByPicklistView::new);

    }

}