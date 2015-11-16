package br.net.mirante.singular.showcase.input.core;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorRadioView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorSelectBSView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorSelectView;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class CaseInputCoreSelectComboRadioPackage extends MPacote {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        //View por Select do Bootstrap
        MTipoString tipoContato1 = tipoMyForm.addCampoString("tipoContato1")
                 .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        tipoContato1
            .withView(MSelecaoPorSelectBSView::new)
            .as(AtrBasic::new).label("Tipo Contato (Combo BS)");

       //View por Select do Browser
       MTipoString tipoContato2 = tipoMyForm.addCampoString("tipoContato2")
               .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

      tipoContato2
          .withView(MSelecaoPorSelectView::new)
          .as(AtrBasic::new).label("Tipo Contato (Combo Browser)");

      //View por Radio
      MTipoString tipoContato3 = tipoMyForm.addCampoString("tipoContato3")
              .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

     tipoContato3
         .withView(MSelecaoPorRadioView::new)
         .as(AtrBasic::new).label("Tipo Contato (Radio)");
    }
}
