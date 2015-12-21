package br.net.mirante.singular.showcase.input.core.select;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorModalBuscaView;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class CaseInputCoreSelectSearchPackage extends MPacote {

    //@formatter:off
    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        MTipoComposto<?> tipoMyForm = pb.createTipoComposto("testForm");

        final MTipoString tipoContato = tipoMyForm.addCampoString("tipoContato", true)
                 .withSelectionOf("Endereço", "Email", "Telefone", "Celular", "Fax");

        tipoContato.withView(MSelecaoPorModalBuscaView::new);
        tipoContato.as(AtrBasic::new).label("Contato");


        /*
            Neste caso vemos como tipos compostos podem ser usados na seleção por busca.
         */
        MTipoComposto<?> degreeType = tipoMyForm.addCampoComposto("degree");
        degreeType.as(AtrBasic::new).label("Escolaridade");
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

        /*
            No tipo composto é possível expandir a seleção para exibir outros campos além
            do valor de descrição, fornecendo maior flexibilidade e abrangência.
         */
        MTipoComposto<?> planetType = tipoMyForm.addCampoComposto("planet");
        planetType.as(AtrBasic::new).label("Planeta Favorito");
        planetType.addCampoDecimal("radius").as(AtrBasic::new).label("Raio");;
        planetType.addCampoString("atmosphericComposition").as(AtrBasic::new).label("Composição Atmosférica");;
        planetType.withSelectionOf(
                createPlanet(planetType, "1", "Mercury", 2439.64, "He, Na+, P+"),
                createPlanet(planetType, "2", "Venus", 6051.59, "CO2, N2"),
                createPlanet(planetType, "3", "Earth", 6378.1, "N2, O2, Ar"),
                createPlanet(planetType, "4", "Mars", 3397.00, "CO2, N2, Ar")
        );
        planetType.setView(MSelecaoPorModalBuscaView::new)
                .setAdditionalFields("radius","atmosphericComposition");
    }

    private MIComposto createPlanet(MTipoComposto<?> planetType, String position, String name,
                                      double radius, String composition) {
        MIComposto item = planetType.create(position, name);
        item.setValor("radius", radius);
        item.setValor("atmosphericComposition", composition);
        return item;
    }
}
