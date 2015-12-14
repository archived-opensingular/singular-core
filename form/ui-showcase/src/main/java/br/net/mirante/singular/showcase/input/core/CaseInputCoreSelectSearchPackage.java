package br.net.mirante.singular.showcase.input.core;

import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorModalBuscaView;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.options.MTipoSelectItem;

public class CaseInputCoreSelectSearchPackage extends MPacote {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        final MTipoString tipoContato = tipoMyForm.addCampoString("tipoContato", true)
                 .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        tipoContato
                .withView(MSelecaoPorModalBuscaView::new);


        //Select with key values
        MTipoSelectItem degreeType = tipoMyForm.addCampo("degree",
                                                        MTipoSelectItem.class);
        degreeType.withSelectionOf(
            degreeType.create("Alfabetizado","Alfabetização"),
            degreeType.create("1º Grau","Ensino Fundamental"),
            degreeType.create("2º Grau","Ensino Médio"),
            degreeType.create("Técnico","Escola Técnica"),
            degreeType.create("Graduado","Superior"),
            degreeType.create("Pós","Pós Graduação"),
            degreeType.create("MsC","Mestrado"),
            degreeType.create("PhD","Doutorado")
            );
        degreeType.withView(MSelecaoPorModalBuscaView::new);
    }
}
