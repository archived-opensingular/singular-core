package br.net.mirante.singular.showcase.input.core;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MTipoSelectItem;

public class CaseInputCoreSelectDefaultPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        addSelection(tipoMyForm, 3, true);
        
        MTipoSelectItem sexo = tipoMyForm.addCampo("sexo",
            MTipoSelectItem.class,true);
        sexo.withSelectionOf(
            sexo.create("M", "Masculino"),
            sexo.create("F", "Feminino"),
            sexo.create("X", "Não Declarar")
            );
        
        addSelection(tipoMyForm, 3, false);
        addSelection(tipoMyForm, 10, false);
        
        MTipoString tipoSelection = tipoMyForm.addCampoString("opcoesDeArquivo");
        tipoSelection.withSelectionFromProvider("filesChoiceProvider");
        tipoSelection.as(AtrBasic::new).label("Seleção de Arquivos Persistidos");
        
        MTipoSelectItem ingredienteQuimico = tipoMyForm.addCampo("ingredienteQuimico",
            MTipoSelectItem.class);
        ingredienteQuimico.withSelectionOf(
            ingredienteQuimico.create("h2o", "Água"),
            ingredienteQuimico.create("h2o2", "Água Oxigenada"),
            ingredienteQuimico.create("o2", "Gás Oxigênio"),
            ingredienteQuimico.create("C12H22O11", "Açúcar")
            );
        ingredienteQuimico.as(AtrBasic::new).label("Seleção de Componentes Químicos");
        
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
