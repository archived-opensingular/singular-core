/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.multiselect;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;

public class CaseInputCoreMultiSelectProviderPackage extends SPackage {
    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");
//
//
//        /*
//         * Neste caso será utilizado o serviço de nome filesChoiceProvider
//         * cadastrado através do Document.bindLocalService
//         */
//
//        STypeString tipoArquivo = pb.createType("opcoesDeArquivo", STypeString.class);
//        tipoArquivo.withSelectionFromProvider("filesChoiceProvider");
//        tipoArquivo.asAtrBasic().label("Seleção de Arquivos Persistidos");
//
//
//        STypeList<STypeString, SIString> arquivosSelecionados =
//                tipoMyForm.addFieldListOf("arquivos", tipoArquivo);
//        arquivosSelecionados.asAtrBasic().label("Seleção de Arquivos Persistidos");

    }
}
