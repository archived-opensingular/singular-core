package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreSelectDefaultPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        STypeComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        addSelection(tipoMyForm, 3, true);
        addSelection(tipoMyForm, 3, false);
        addSelection(tipoMyForm, 10, false);

        /*
            Outra forma de definir suas opções é populando o provedor padrão
         */
        STypeString favvortiteFruit = tipoMyForm.addCampoString("favvortiteFruit");
        favvortiteFruit.as(AtrBasic::new).label("Fruta Favorita");
        favvortiteFruit.withSelection().add("Maçã").add("Laranja").add("Banana").add("Goiaba");

    }

    private static void addSelection(STypeComposto<?> tipoMyForm, int sizeOptions, boolean required) {
        STypeString tipoSelection = tipoMyForm.addCampoString("opcoes" + sizeOptions + required);

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
