/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.core.select;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;
import br.net.mirante.singular.showcase.component.Resource;
import br.net.mirante.singular.showcase.view.page.form.crud.services.MFileIdsOptionsProvider;

/**
 * É permitido alterar o provedor de dados de forma que estes sejam carregados de forma dinâmica ou de outras fontes de informação.
 */
@CaseItem(componentName = "Select", subCaseName = "Provedor Dinâmico", group = Group.INPUT,
            resources = @Resource(MFileIdsOptionsProvider.class))
public class CaseInputCoreSelectProviderPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
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
