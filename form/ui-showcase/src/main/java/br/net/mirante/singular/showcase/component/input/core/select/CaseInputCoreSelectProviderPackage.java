/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;

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
        tipoArquivo.as(AtrBasic::new).label("Seleção de Arquivos Persistidos");


        STypeString tipoDeMedia = tipoMyForm.addFieldString("tipoDeMedia");
        tipoDeMedia.withRadioView();
        tipoDeMedia.withSelectionFromProvider(new SOptionsProvider() {
            @Override
            public SIList<? extends SInstance> listOptions(SInstance optionsInstance) {
                STypeString type = getDictionary().getType(STypeString.class);
                SIList<?> r = type.newList();
                r.addElement(newElement(type, "IMG", "Imagem"));
                r.addElement(newElement(type, "TXT", "Texto"));
                r.addElement(newElement(type, "BIN", "Binário"));
                return r;
            }

            private SIString newElement(STypeString type, String id, String label) {
                SIString e = type.newInstance();
                e.setValue(id);
                e.setSelectLabel(label);
                return e;
            }
        });
        tipoDeMedia.as(AtrBasic::new).label("Tipo do Arquivo");

    }

}
