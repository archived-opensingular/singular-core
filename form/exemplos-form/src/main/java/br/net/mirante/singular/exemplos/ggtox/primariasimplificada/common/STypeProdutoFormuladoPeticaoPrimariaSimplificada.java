package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.persistence.STypePersistentComposite;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeProdutoFormuladoPeticaoPrimariaSimplificada extends STypePersistentComposite {


    public STypeFormuladorConformeMatriz formulador;
    public STypeList<STypeFormulador, SIComposite> formuladores;
    public STypeComposite<SIComposite> tipoFormulacao;


    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

        this
                .asAtrAnnotation()
                .setAnnotated();

        this
                .asAtr()
                .label("Produto Formulado");

        tipoFormulacao = addFieldComposite("tipoFormulacao");
        STypeInteger idTipoFormulacao = tipoFormulacao.addFieldInteger("idTipoFormulacao");
        STypeString siglaTipoFormulacao = tipoFormulacao.addFieldString("siglaTipoFormulacao");
        STypeString descricaoTipoFormulacao = tipoFormulacao.addFieldString("descricaoTipoFormulacao");

        tipoFormulacao
                .asAtr()
                .label("Tipo de Formulação")
                .asAtrBootstrap()
                .colPreference(4);

        tipoFormulacao
                .selection()
                .id(idTipoFormulacao)
                .display("${siglaTipoFormulacao} - ${descricaoTipoFormulacao}")
                .simpleProvider( sb -> {
                    sb.add().set(idTipoFormulacao, 1).set(siglaTipoFormulacao, "AE").set(descricaoTipoFormulacao, "Aerossol");
                    sb.add().set(idTipoFormulacao, 2).set(siglaTipoFormulacao, "FR").set(descricaoTipoFormulacao, "Bastonete fumigante");
                    sb.add().set(idTipoFormulacao, 3).set(siglaTipoFormulacao, "PR").set(descricaoTipoFormulacao, "Bastonete vegetal");
                    sb.add().set(idTipoFormulacao, 4).set(siglaTipoFormulacao, "BR").set(descricaoTipoFormulacao, "Bloco");
                    sb.add().set(idTipoFormulacao, 5).set(siglaTipoFormulacao, "FP").set(descricaoTipoFormulacao, "Cartucho fumigante");
                    sb.add().set(idTipoFormulacao, 6).set(siglaTipoFormulacao, "DC").set(descricaoTipoFormulacao, "Concentrado dispersível");
                    sb.add().set(idTipoFormulacao, 7).set(siglaTipoFormulacao, "PC").set(descricaoTipoFormulacao, "Concentrado em pasta");
                    sb.add().set(idTipoFormulacao, 8).set(siglaTipoFormulacao, "KN").set(descricaoTipoFormulacao, "Concentrado para nebulização a frio");
                    sb.add().set(idTipoFormulacao, 9).set(siglaTipoFormulacao, "HN").set(descricaoTipoFormulacao, "Concentrado para termonebulização");
                    sb.add().set(idTipoFormulacao, 10).set(siglaTipoFormulacao, "SL").set(descricaoTipoFormulacao, "Concentrado solúvel");
                    sb.add().set(idTipoFormulacao, 11).set(siglaTipoFormulacao, "EC").set(descricaoTipoFormulacao, "Concentrado emulsionável");
                    sb.add().set(idTipoFormulacao, 12).set(siglaTipoFormulacao, "EO").set(descricaoTipoFormulacao, "Emulsão de água em óleo");
                    sb.add().set(idTipoFormulacao, 13).set(siglaTipoFormulacao, "EW").set(descricaoTipoFormulacao, "Emulsão de óleo em água");
                    sb.add().set(idTipoFormulacao, 14).set(siglaTipoFormulacao, "ES").set(descricaoTipoFormulacao, "Emulsão para tratamento de sementes");
                    sb.add().set(idTipoFormulacao, 17).set(siglaTipoFormulacao, "FU").set(descricaoTipoFormulacao, "Fumigante");
                    sb.add().set(idTipoFormulacao, 18).set(siglaTipoFormulacao, "GA").set(descricaoTipoFormulacao, "Gás liquefeito sob ressão");
                    sb.add().set(idTipoFormulacao, 19).set(siglaTipoFormulacao, "GL").set(descricaoTipoFormulacao, "Gel emulsionavél");
                    sb.add().set(idTipoFormulacao, 20).set(siglaTipoFormulacao, "GW").set(descricaoTipoFormulacao, "Gel solúvel em água");
                    sb.add().set(idTipoFormulacao, 21).set(siglaTipoFormulacao, "GE").set(descricaoTipoFormulacao, "Gerador de gás");
                    sb.add().set(idTipoFormulacao, 22).set(siglaTipoFormulacao, "GR").set(descricaoTipoFormulacao, "Granulado");
                    sb.add().set(idTipoFormulacao, 23).set(siglaTipoFormulacao, "WG").set(descricaoTipoFormulacao, "Granulado dispersível");
                    sb.add().set(idTipoFormulacao, 24).set(siglaTipoFormulacao, "CG").set(descricaoTipoFormulacao, "Granulado encapsulado");
                    sb.add().set(idTipoFormulacao, 25).set(siglaTipoFormulacao, "FG").set(descricaoTipoFormulacao, "Granulado fino");
                    sb.add().set(idTipoFormulacao, 26).set(siglaTipoFormulacao, "FW").set(descricaoTipoFormulacao, "Granulado fumigante");
                    sb.add().set(idTipoFormulacao, 27).set(siglaTipoFormulacao, "SG").set(descricaoTipoFormulacao, "Granulado solúvel");
                    sb.add().set(idTipoFormulacao, 28).set(siglaTipoFormulacao, "RB").set(descricaoTipoFormulacao, "Isca");
                    sb.add().set(idTipoFormulacao, 29).set(siglaTipoFormulacao, "BB").set(descricaoTipoFormulacao, "Iscas de blocos");
                    sb.add().set(idTipoFormulacao, 30).set(siglaTipoFormulacao, "AB").set(descricaoTipoFormulacao, "Iscas de grãos");
                    sb.add().set(idTipoFormulacao, 31).set(siglaTipoFormulacao, "SB").set(descricaoTipoFormulacao, "Iscas em aparas");
                    sb.add().set(idTipoFormulacao, 32).set(siglaTipoFormulacao, "PB").set(descricaoTipoFormulacao, "Iscas em placas");
                    sb.add().set(idTipoFormulacao, 33).set(siglaTipoFormulacao, "GB").set(descricaoTipoFormulacao, "Iscas granuladas");
                    sb.add().set(idTipoFormulacao, 34).set(siglaTipoFormulacao, "LA").set(descricaoTipoFormulacao, "Laca");
                    sb.add().set(idTipoFormulacao, 35).set(siglaTipoFormulacao, "ED").set(descricaoTipoFormulacao, "Líquido para pulverização eletrostática/eletrodinâmica");
                    sb.add().set(idTipoFormulacao, 36).set(siglaTipoFormulacao, "GG").set(descricaoTipoFormulacao, "Macrogranulado");
                    sb.add().set(idTipoFormulacao, 37).set(siglaTipoFormulacao, "MEO").set(descricaoTipoFormulacao, "Microemulsão de água em óleo");
                    sb.add().set(idTipoFormulacao, 38).set(siglaTipoFormulacao, "MEW").set(descricaoTipoFormulacao, "Microemulsão de óleo em água");
                    sb.add().set(idTipoFormulacao, 39).set(siglaTipoFormulacao, "MG").set(descricaoTipoFormulacao, "Microgranulado");
                    sb.add().set(idTipoFormulacao, 40).set(siglaTipoFormulacao, "SO").set(descricaoTipoFormulacao, "Óleo para pulverização/espalhamento");
                    sb.add().set(idTipoFormulacao, 41).set(siglaTipoFormulacao, "AA").set(descricaoTipoFormulacao, "Outros pós");
                    sb.add().set(idTipoFormulacao, 42).set(siglaTipoFormulacao, "PA").set(descricaoTipoFormulacao, "Pasta");
                    sb.add().set(idTipoFormulacao, 43).set(siglaTipoFormulacao, "GS").set(descricaoTipoFormulacao, "Pasta oleosa");
                    sb.add().set(idTipoFormulacao, 44).set(siglaTipoFormulacao, "FD").set(descricaoTipoFormulacao, "Pastilha fumigante");
                    sb.add().set(idTipoFormulacao, 45).set(siglaTipoFormulacao, "OP").set(descricaoTipoFormulacao, "Pó dispersível em óleo");
                    sb.add().set(idTipoFormulacao, 46).set(siglaTipoFormulacao, "GP").set(descricaoTipoFormulacao, "Pó fino");
                    sb.add().set(idTipoFormulacao, 47).set(siglaTipoFormulacao, "WP").set(descricaoTipoFormulacao, "Pó molhável");
                    sb.add().set(idTipoFormulacao, 48).set(siglaTipoFormulacao, "TP").set(descricaoTipoFormulacao, "Pó para despistagem");
                    sb.add().set(idTipoFormulacao, 49).set(siglaTipoFormulacao, "WS").set(descricaoTipoFormulacao, "Pó para preparação de pasta em água");
                    sb.add().set(idTipoFormulacao, 50).set(siglaTipoFormulacao, "WO").set(descricaoTipoFormulacao, "Pó para preparação de pasta em óleo");
                    sb.add().set(idTipoFormulacao, 51).set(siglaTipoFormulacao, "DS").set(descricaoTipoFormulacao, "Pó para tratamento a seco de sementes");
                    sb.add().set(idTipoFormulacao, 52).set(siglaTipoFormulacao, "DP").set(descricaoTipoFormulacao, "Pó seco");
                    sb.add().set(idTipoFormulacao, 53).set(siglaTipoFormulacao, "SP").set(descricaoTipoFormulacao, "Pó solúvel");
                    sb.add().set(idTipoFormulacao, 54).set(siglaTipoFormulacao, "SS").set(descricaoTipoFormulacao, "Pó solúvel para tratamento de sementes");
                    sb.add().set(idTipoFormulacao, 55).set(siglaTipoFormulacao, "VP").set(descricaoTipoFormulacao, "Produtor de vapor");
                    sb.add().set(idTipoFormulacao, 56).set(siglaTipoFormulacao, "OL").set(descricaoTipoFormulacao, "Solução miscível em óleo");
                    sb.add().set(idTipoFormulacao, 57).set(siglaTipoFormulacao, "LS").set(descricaoTipoFormulacao, "Solução para tratamento de sementes");
                    sb.add().set(idTipoFormulacao, 58).set(siglaTipoFormulacao, "SU").set(descricaoTipoFormulacao, "Suspensão a ultrabaixo volume");
                    sb.add().set(idTipoFormulacao, 59).set(siglaTipoFormulacao, "SC").set(descricaoTipoFormulacao, "Suspensão concentrada");
                    sb.add().set(idTipoFormulacao, 60).set(siglaTipoFormulacao, "OF").set(descricaoTipoFormulacao, "Suspensão concentrada dispersível em óleo");
                    sb.add().set(idTipoFormulacao, 61).set(siglaTipoFormulacao, "FS").set(descricaoTipoFormulacao, "Suspensão concentrada para tratamento de sementes");
                    sb.add().set(idTipoFormulacao, 62).set(siglaTipoFormulacao, "CS").set(descricaoTipoFormulacao, "Suspensão de encapsulado");
                    sb.add().set(idTipoFormulacao, 63).set(siglaTipoFormulacao, "SE").set(descricaoTipoFormulacao, "Suspo/emulsão");
                    sb.add().set(idTipoFormulacao, 64).set(siglaTipoFormulacao, "SCS").set(descricaoTipoFormulacao, "Suspo/suspensão de encapsulados");
                    sb.add().set(idTipoFormulacao, 65).set(siglaTipoFormulacao, "TB").set(descricaoTipoFormulacao, "Tablete");
                    sb.add().set(idTipoFormulacao, 66).set(siglaTipoFormulacao, "FT").set(descricaoTipoFormulacao, "Tablete fumigante");
                    sb.add().set(idTipoFormulacao, 67).set(siglaTipoFormulacao, "UL").set(descricaoTipoFormulacao, "Ultrabaixo volume");
                    sb.add().set(idTipoFormulacao, 68).set(siglaTipoFormulacao, "FK").set(descricaoTipoFormulacao, "Vela fumigante");
                });


        formuladores = addFieldListOf("formuladores", STypeFormulador.class);

        formuladores
                .asAtr()
                .label("Formuladores");
        formuladores
                .withView(new SViewListByMasterDetail()
                                .col(formuladores.getElementsType().cnpj)
                                .col(formuladores.getElementsType().nome)
                                .col(formuladores.getElementsType().cidade)
                                .col(formuladores.getElementsType().estado)
                );
        formuladores
                .withMiniumSizeOf(1);


        formulador = addField("formulador", STypeFormuladorConformeMatriz.class);
        formulador
                .asAtrBootstrap()
                .colPreference(12)
                .newRow();

    }
}
