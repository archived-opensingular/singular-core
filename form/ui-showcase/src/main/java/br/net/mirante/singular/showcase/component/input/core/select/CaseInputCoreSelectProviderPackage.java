package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.MOptionsProvider;

public class CaseInputCoreSelectProviderPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createTipoComposto("testForm");

        /*
         * Neste caso será utilizado o serviço de nome filesChoiceProvider
         * cadastrado através do Document.bindLocalService
         */
        STypeString tipoArquivo = tipoMyForm.addCampoString("opcoesDeArquivo");
        tipoArquivo.withSelectionFromProvider("filesChoiceProvider");
        tipoArquivo.as(AtrBasic::new).label("Seleção de Arquivos Persistidos");


        STypeString tipoDeMedia = tipoMyForm.addCampoString("tipoDeMedia");
        tipoDeMedia.withRadioView();
        tipoDeMedia.withSelectionFromProvider(new MOptionsProvider() {
            @Override
            public SList<? extends SInstance> listOptions(SInstance optionsInstance) {
                STypeString type = getDictionary().getType(STypeString.class);
                SList<?> r = type.novaLista();
                r.addElement(newElement(type, "IMG", "Imagem"));
                r.addElement(newElement(type, "TXT", "Texto"));
                r.addElement(newElement(type, "BIN", "Binário"));
                return r;
            }

            private SIString newElement(STypeString type, String id, String label) {
                SIString e = type.novaInstancia();
                e.setValue(id);
                e.setSelectLabel(label);
                return e;
            }
        });
        tipoDeMedia.as(AtrBasic::new).label("Tipo do Arquivo");

    }

}
