package br.net.mirante.singular.showcase.component.input.core.select;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorModalBuscaView;
import br.net.mirante.singular.form.mform.core.STypeString;

public class CaseInputCoreSelectSearchPackage extends SPackage {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        STypeComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        final STypeString tipoContato = tipoMyForm.addCampoString("tipoContato", true)
                 .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        tipoContato.withView(MSelecaoPorModalBuscaView::new);
        tipoContato.as(AtrBasic::new).label("Contato");


        /*
            Neste caso vemos como tipos compostos podem ser usados na seleção por busca.
         */
        STypeString degreeType = tipoMyForm.addCampoString("degree");
        degreeType.as(AtrBasic::new).label("Escolaridade");
        degreeType.withSelection()
            .add("Alfabetizado","Alfabetização")
            .add("1º Grau","Ensino Fundamental")
            .add("2º Grau","Ensino Médio")
            .add("Técnico","Escola Técnica")
            .add("Graduado","Superior")
            .add("Pós","Pós Graduação")
            .add("MsC","Mestrado")
            .add("PhD","Doutorado");
        degreeType.withView(MSelecaoPorModalBuscaView::new);

        /*
            No tipo composto é possível expandir a seleção para exibir outros campos além
            do valor de descrição, fornecendo maior flexibilidade e abrangência.
         */
        STypeComposto<?> planetType = tipoMyForm.addCampoComposto("planet");
        planetType.as(AtrBasic::new).label("Planeta Favorito");
        STypeString id = planetType.addCampoString("id");
        STypeString nome = planetType.addCampoString("nome");
        planetType.addCampoDecimal("radius").as(AtrBasic::new).label("Raio");;
        planetType.addCampoString("atmosphericComposition").as(AtrBasic::new).label("Composição Atmosférica");;
        planetType.withSelectionFromProvider("nome", (inst, lb) ->{
                    lb
                            .add().set(id, "1").set(nome, "Mercury").set("radius", 2439.64).set("atmosphericComposition", "He, Na+, P+")
                            .add().set(id, "2").set(nome, "Venus").set("radius", 6051.59).set("atmosphericComposition", "CO2, N2")
                            .add().set(id, "3").set(nome, "Earth").set("radius", 6378.1).set("atmosphericComposition", "N2, O2, Ar")
                            .add().set(id, "4").set(nome, "Mars").set("radius", 3397.00).set("atmosphericComposition", "CO2, N2, Ar");
                }
        );
        planetType.setView(MSelecaoPorModalBuscaView::new)
                .setAdditionalFields("radius","atmosphericComposition");
    }

}
