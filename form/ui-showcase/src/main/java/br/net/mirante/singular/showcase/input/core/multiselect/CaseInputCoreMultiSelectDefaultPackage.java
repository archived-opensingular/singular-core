package br.net.mirante.singular.showcase.input.core.multiselect;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MISelectItem;
import br.net.mirante.singular.form.mform.options.MTipoSelectItem;

public class CaseInputCoreMultiSelectDefaultPackage extends MPacote {

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        addMultiSelection(pb, tipoMyForm, 3);
        
        MTipoSelectItem tipoSexo = pb.createTipo("sexo", MTipoSelectItem.class);
        tipoSexo.withSelectionOf(
            tipoSexo.create("M", "Masculino"),
            tipoSexo.create("F", "Feminino"),
            tipoSexo.create("X", "Não Declarar")
            );
        
        MTipoLista<MTipoSelectItem, MISelectItem> sexField = tipoMyForm.addCampoListaOf("sex", tipoSexo);
        sexField.as(AtrBasic::new).label("Sexo");
        
        addMultiSelection(pb, tipoMyForm, 15);
        addMultiSelection(pb, tipoMyForm, 25);
        
        MTipoSelectItem tipoIngrediente = pb.createTipo("tipoIngrediente", MTipoSelectItem.class);
        tipoIngrediente.withSelectionOf(
            tipoIngrediente.create("h2o", "Água"),
            tipoIngrediente.create("h2o2", "Água Oxigenada"),
            tipoIngrediente.create("o2", "Gás Oxigênio"),
            tipoIngrediente.create("C12H22O11", "Açúcar")
            );
        MTipoLista<MTipoSelectItem, MISelectItem> ingredienteQuimico = 
            tipoMyForm.addCampoListaOf("ingredientes", tipoIngrediente);
        ingredienteQuimico.as(AtrBasic::new).label("Seleção de Componentes Químicos");
        
    }

    private static void addMultiSelection(PacoteBuilder pb, MTipoComposto<?> tipoMyForm, int size) {
        MTipoString tipoSelection = pb.createTipo("opcoes" + size, MTipoString.class);
        tipoSelection.withSelectionOf(createOptions(size));

        MTipoLista<MTipoString, MIString> multiSelection = tipoMyForm.addCampoListaOf("multiSelection" + size, tipoSelection);
        multiSelection.as(AtrBasic::new).label("Seleção de " + size);
    }

    private static String[] createOptions(int sizeOptions) {
        String[] options = new String[sizeOptions];
        for(int i = 1; i <= sizeOptions; i++) {
            options[i - 1] = "Opção " + i;
        }
        return options;
    }
}
