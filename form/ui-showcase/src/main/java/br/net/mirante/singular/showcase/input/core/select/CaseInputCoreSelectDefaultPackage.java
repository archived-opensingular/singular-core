package br.net.mirante.singular.showcase.input.core.select;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class CaseInputCoreSelectDefaultPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        addSelection(tipoMyForm, 3, true);
        addSelection(tipoMyForm, 3, false);
        addSelection(tipoMyForm, 10, false);

        /*
            Outra forma de definir suas opções é populando o provedor padrão
         */
        MTipoString favvortiteFruit = tipoMyForm.addCampoString("favvortiteFruit");
        favvortiteFruit.as(AtrBasic::new).label("Fruta Favorita");
        favvortiteFruit.withSelection().add("Maçã").add("Laranja").add("Banana").add("Goiaba");

    }

    private static void addSelection(MTipoComposto<?> tipoMyForm, int sizeOptions, boolean required) {
        MTipoString tipoSelection = tipoMyForm.addCampoString("opcoes" + sizeOptions + required);

        tipoSelection.withSelectionOf(createOptions(sizeOptions));
        tipoSelection.withObrigatorio(required);

        tipoSelection.as(AtrBasic::new).label("Seleção de " + sizeOptions);
    }

    private static String[] createOptions(int sizeOptions) {
        String[] options = new String[sizeOptions];
        for(int i = 1; i <= sizeOptions; i++) {
            options[i - 1] = "Opção " + i;
        }
        return options;
    }
}
