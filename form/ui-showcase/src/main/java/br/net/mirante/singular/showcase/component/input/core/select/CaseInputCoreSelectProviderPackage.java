/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreSelectProviderPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        /*
         * Neste caso será utilizado o serviço de nome filesChoiceProvider
         * cadastrado através do Document.bindLocalService
         */
        STypeString tipoArquivo = tipoMyForm.addFieldString("opcoesDeArquivo");
        tipoArquivo.withSelectionFromProvider("filesChoiceProvider");
        tipoArquivo.asAtrBasic().label("Seleção de Arquivos Persistidos");


        STypeString tipoDeMedia = tipoMyForm.addFieldString("tipoDeMedia");
        tipoDeMedia.withRadioView();

        //TODO DANILO
//        tipoDeMedia.withSelectionFromProvider(new SOptionsProvider() {
//            @Override
//            public SIList<? extends SInstance> listOptions(SInstance optionsInstance, String filter) {
//                STypeString type = (STypeString) optionsInstance.getType();
//                SIList<?> r = type.newList();
//                r.addElement(newElement(type, "IMG", "Imagem"));
//                r.addElement(newElement(type, "TXT", "Texto"));
//                r.addElement(newElement(type, "BIN", "Binário"));
//                return r;
//            }
//
//            private SIString newElement(STypeString type, String id, String label) {
//                SIString e = type.newInstance();
//                e.setValue(id);
//                e.setSelectLabel(label);
//                return e;
//            }
//        });
        tipoDeMedia.asAtrBasic().label("Tipo do Arquivo");

    }

}
