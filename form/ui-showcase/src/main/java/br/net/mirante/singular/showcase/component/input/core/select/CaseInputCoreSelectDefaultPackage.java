package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreSelectDefaultPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        STypeComposite<?> tipoMyForm = pb.createCompositeType("testForm");

        addSelection(tipoMyForm, 3, true);
        addSelection(tipoMyForm, 3, false);
        addSelection(tipoMyForm, 10, false);

        /*
            Outra forma de definir suas opções é populando o provedor padrão
         */
        STypeString favvortiteFruit = tipoMyForm.addFieldString("favvortiteFruit");
        favvortiteFruit.as(AtrBasic::new).label("Fruta Favorita");
        favvortiteFruit.withSelection().add("Maçã").add("Laranja").add("Banana").add("Goiaba");

    }

    private static void addSelection(STypeComposite<?> tipoMyForm, int sizeOptions, boolean required) {
        STypeString tipoSelection = tipoMyForm.addFieldString("opcoes" + sizeOptions + required);

        tipoSelection.withSelectionOf(createOptions(sizeOptions));
        tipoSelection.withRequired(required);

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
