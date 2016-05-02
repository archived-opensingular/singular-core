/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.showcase.dao.form.ExampleFile;

public class CaseInputCoreSelectProviderPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        /*
         * Neste caso será utilizado o serviço de nome filesChoiceProvider
         * cadastrado através do Document.bindLocalService
         */
        STypeString tipoArquivo = tipoMyForm.addFieldString("opcoesDeArquivo");
        tipoArquivo.asAtr().label("Seleção de Arquivos Persistidos");
        tipoArquivo.selectionOf(ExampleFile.class)
                .id(ExampleFile::getId)
                .display(ExampleFile::getId)
                .simpleConverter()
                .provider("filesChoiceProvider");

    }

}
