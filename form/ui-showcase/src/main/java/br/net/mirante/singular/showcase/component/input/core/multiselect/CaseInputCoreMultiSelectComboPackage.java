package br.net.mirante.singular.showcase.component.input.core.multiselect;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorSelectView;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreMultiSelectComboPackage extends SPackage {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        STypeComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        STypeString tipoContato = pb.createTipo("tipoContato", STypeString.class)
                 .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        STypeString tipoArquivo = pb.createTipo("tipoArquivo", STypeString.class);
            tipoArquivo.withSelectionFromProvider("filesChoiceProvider");
        
        STypeLista<STypeString, SIString> infoPub1 = tipoMyForm
                .addCampoListaOf("infoPub1", tipoContato);

        infoPub1
            .withView(MSelecaoMultiplaPorSelectView::new)
            .as(AtrBasic::new).label("Informações Públicas");
        
        STypeLista<STypeString, SIString> infoArq = tipoMyForm
                .addCampoListaOf("infoArq", tipoArquivo);
        infoArq
            .withView(MSelecaoMultiplaPorSelectView::new)
            .as(AtrBasic::new).label("Arquivos Persistidos");
    }
}
