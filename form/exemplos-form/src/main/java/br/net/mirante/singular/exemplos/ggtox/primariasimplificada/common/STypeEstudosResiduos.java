package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;

import static br.net.mirante.singular.form.util.SingularPredicates.*;

import br.net.mirante.singular.exemplos.SelectBuilder;
import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.STypeList;
import br.net.mirante.singular.form.STypeSimple;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.STypeBoolean;
import br.net.mirante.singular.form.type.core.STypeDecimal;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.type.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.form.view.SViewListByMasterDetail;


@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeEstudosResiduos extends STypeComposite<SIComposite> {

    private Cultura cultura;
    private STypeBoolean culturaConformeMatriz;
    private Amostra amostra;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        this.asAtr()
                .label("Estudo de Resíduos");

        culturaConformeMatriz = addFieldBoolean("culturaConformeMatriz");
        cultura = new Cultura();
        amostra = new Amostra();

        culturaConformeMatriz
                .asAtr().label("Cultura conforme a matriz")
                .asAtrBootstrap().newRow();

        amostra.root
                .asAtr().dependsOn(cultura.culturaConformeEstudoPublicado)
                .exists(typeValIsNotEqualsTo(cultura.culturaConformeEstudoPublicado, Boolean.TRUE));

    }

    class Cultura {

        private final STypeList<STypeComposite<SIComposite>, SIComposite> root;
        private final STypeComposite<SIComposite> rootType;
        private final STypeBoolean culturaConformeEstudoPublicado;

        public Cultura() {
            root = addFieldListOfComposite("culturas", "cultura");
            rootType = root.getElementsType();
            culturaConformeEstudoPublicado = rootType.addFieldBoolean("culturaConformeEstudoPublicado");
            final STypeString estudoPublicado = rootType.addFieldString("estudoPublicado");
            final STypeString nomeCultura = rootType.addFieldString("nomeCultura");
            final STypeString nomeOutraCultura = rootType.addFieldString("nomeOutraCultura");
            final STypeBoolean outraCultura = rootType.addFieldBoolean("outraCultura");
            final STypeString emprego = rootType.addFieldString("emprego");
            final STypeString numeroEstudo = rootType.addFieldString("numeroEstudo");
            final STypeBoolean adjuvante = rootType.addFieldBoolean("adjuvante");
            final STypeInteger valorConcentracao = rootType.addFieldInteger("valorConcentracao");
            final STypeString unidadeConcentracao = rootType.addFieldString("unidadeConcentracao");
            final STypeAttachment estudoResiduo = rootType.addFieldAttachment("estudoResiduo");

            root
                    .withView(new SViewListByMasterDetail()
                            .col(nomeCultura).col(emprego))
                    .asAtr().exists(typeValIsNotEqualsTo(culturaConformeMatriz, Boolean.TRUE));

            culturaConformeEstudoPublicado
                    .asAtr().label("Cultura conforme estudo publicado")
                    .asAtrBootstrap().newRow();

            estudoPublicado
                    .asAtr().label("Código do estudo publicado")
                    .dependsOn(culturaConformeEstudoPublicado)
                    .exists(typeValIsTrue(culturaConformeEstudoPublicado))
                    .asAtrBootstrap().newRow();

//            existsWhenTrue(culturaConformeEstudoPublicado, nomeCultura, outraCultura, emprego, numeroEstudo, adjuvante, estudoResiduo);

            nomeCultura
                    .selectionOf(culturas())
                    .asAtr().label("Nome da cultura")
                    .required()
                    .dependsOn(outraCultura, culturaConformeEstudoPublicado)
                    .exists(allMatches(typeValIsNotEqualsTo(culturaConformeEstudoPublicado, Boolean.TRUE), typeValIsNotEqualsTo(outraCultura, Boolean.TRUE)))
                    .asAtrBootstrap().newRow();

            outraCultura
                    .asAtr().label("Outra cultura")
                    .dependsOn(culturaConformeEstudoPublicado)
                    .exists(typeValIsNotEqualsTo(culturaConformeEstudoPublicado, Boolean.TRUE))
                    .asAtrBootstrap().newRow();

            nomeOutraCultura
                    .asAtr().label("Nome da cultura")
                    .required()
                    .dependsOn(outraCultura)
                    .exists(typeValIsTrue(outraCultura))
                    .asAtrBootstrap().newRow();

            numeroEstudo
                    .asAtr().label("Número do estudo")
                    .dependsOn(culturaConformeEstudoPublicado)
                    .exists(typeValIsNotEqualsTo(culturaConformeEstudoPublicado, Boolean.TRUE))
                    .asAtrBootstrap().newRow();

            adjuvante
                    .withRadioView()
                    .asAtr().label("Adjuvante")
                    .required()
                    .dependsOn(culturaConformeEstudoPublicado)
                    .exists(typeValIsNotEqualsTo(culturaConformeEstudoPublicado, Boolean.TRUE))
                    .asAtrBootstrap().newRow();

            valorConcentracao
                    .asAtr().label("Concentração")
                    .dependsOn(adjuvante)
                    .exists(typeValIsTrue(adjuvante))
                    .asAtrBootstrap().newRow();

            unidadeConcentracao
                    .asAtr().label("Unidade de medida")
                    .dependsOn(adjuvante)
                    .exists(typeValIsTrue(adjuvante));

            emprego
                    .selectionOf(empregos())
                    .asAtr().label("Emprego")
                    .dependsOn(culturaConformeEstudoPublicado)
                    .exists(typeValIsNotEqualsTo(culturaConformeEstudoPublicado, Boolean.TRUE))
                    .asAtrBootstrap().newRow();

            estudoResiduo
                    .asAtr().label("Estudo de resíduo")
                    .dependsOn(culturaConformeEstudoPublicado)
                    .exists(typeValIsNotEqualsTo(culturaConformeEstudoPublicado, Boolean.TRUE));
        }

    }

    class Amostra {

        private final STypeList<STypeComposite<SIComposite>, SIComposite> root;
        private final STypeComposite<SIComposite> rootType;

        public Amostra() {
            root = cultura.rootType.addFieldListOfComposite("amostras", "amostra");
            rootType = root.getElementsType();
            final STypeInteger dose = rootType.addFieldInteger("dose");
            final STypeInteger aplicacoes = rootType.addFieldInteger("aplicacoes");
            final STypeInteger id = rootType.addFieldInteger("id");
            final STypeInteger dat = rootType.addFieldInteger("dat");
            final STypeDecimal lod = rootType.addFieldDecimal("lod");
            final STypeDecimal loq = rootType.addFieldDecimal("loq");
            final STypeDecimal residuo = rootType.addFieldDecimal("residuo");
            final STypeComposite<?> estado = rootType.addFieldComposite("estado");
            final STypeString siglaUF = estado.addFieldString("sigla");
            final STypeString nome = estado.addFieldString("nome");
            final STypeComposite<?> cidade = rootType.addFieldComposite("cidade");
            final STypeInteger id1 = cidade.addFieldInteger("id");
            final STypeString nome1 = cidade.addFieldString("nome");
            final STypeString uf = cidade.addFieldString("UF");
            final STypeBoolean tempoMaior30Dias = rootType.addFieldBoolean("tempoMaior30Dias");
            final STypeAttachment estudoEstabilidade = rootType.addFieldAttachment("estudoEstabilidade");
            final STypeBoolean metabolito = rootType.addFieldBoolean("metabolito");
            final STypeDecimal lodMetabolito = rootType.addFieldDecimal("lodMetabolito");
            final STypeDecimal loqMetabolito = rootType.addFieldDecimal("loqMetabolito");
            final STypeDecimal residuoMetabolito = rootType.addFieldDecimal("residuoMetabolito");

            root
                    .withView(new SViewListByMasterDetail()
                            .col(dose))
                    .asAtr().label("Amostras");

            dose
                    .asAtr().label("Dose")
                    .required();

            aplicacoes
                    .asAtr().label("Número de aplicações")
                    .required();

            id
                    .asAtr().label("ID da amostra")
                    .required();

            dat
                    .asAtr().label("DAT");

            lod
                    .asAtr().label("LoD (mg/KG)")
                    .fractionalMaxLength(4);

            loq
                    .asAtr().label("LoQ (mg/KG)")
                    .fractionalMaxLength(4);

            residuo
                    .asAtr().label("Resíduo")
                    .fractionalMaxLength(4);

            estado
                    .asAtr()
                    .required()
                    .asAtr()
                    .label("Estado")
                    .asAtrBootstrap()
                    .colPreference(3)
                    .newRow();

            estado.selectionOf(SelectBuilder.EstadoDTO.class)
                    .id(SelectBuilder.EstadoDTO::getSigla)
                    .display("${nome} - ${sigla}")
                    .autoConverterOf(SelectBuilder.EstadoDTO.class)
                    .simpleProvider(ins ->  SelectBuilder.buildEstados());

            cidade
                    .asAtr()
                    .required(inst -> Value.notNull(inst, (STypeSimple) estado.getField(siglaUF)))
                    .asAtr()
                    .label("Cidade")
                    .enabled(inst -> Value.notNull(inst, (STypeSimple) estado.getField(siglaUF)))
                    .dependsOn(estado)
                    .asAtrBootstrap()
                    .colPreference(3);

            cidade.selectionOf(SelectBuilder.CidadeDTO.class)
                    .id(SelectBuilder.CidadeDTO::getId)
                    .display(SelectBuilder.CidadeDTO::getNome)
                    .autoConverterOf(SelectBuilder.CidadeDTO.class)
                    .simpleProvider(i -> SelectBuilder.buildMunicipiosFiltrado((String) Value.of(i, (STypeSimple) estado.getField(siglaUF.getNameSimple()))));

            tempoMaior30Dias
                    .asAtr().label("Tempo entre análise e colheita maior que 30 dias")
                    .asAtrBootstrap().colPreference(6)
                    .newRow();

            estudoEstabilidade
                    .asAtr().label("Estudo de estabilidade")
                    .dependsOn(tempoMaior30Dias)
                    .exists(typeValIsTrue(tempoMaior30Dias));

            metabolito
                    .withRadioView()
                    .asAtr().label("Metabólito")
                    .required();

            lodMetabolito
                    .asAtr().label("LoD (mg/KG)")
                    .dependsOn(metabolito)
                    .exists(typeValIsTrue(metabolito))
                    .fractionalMaxLength(4)
                    .asAtrBootstrap()
                    .newRow();

            loqMetabolito
                    .asAtr().label("LoQ (mg/KG)")
                    .dependsOn(metabolito)
                    .exists(typeValIsTrue(metabolito))
                    .fractionalMaxLength(4);

            residuoMetabolito
                    .asAtr().label("Resíduo")
                    .dependsOn(metabolito)
                    .exists(typeValIsTrue(metabolito))
                    .fractionalMaxLength(4);
        }
    }

    private String[] culturas() {
        return new String[] {
                "Algodão herbáceo",
                "Amendoim",
                "Arroz",
                "Aveia",
                "Centeio",
                "Cevada",
                "Feijão",
                "Girassol",
                "Mamona",
                "Milho",
                "Soja",
                "Sorgo",
                "Trigo",
                "Triticale",
                "Abacaxi",
                "Alho",
                "Banana",
                "Cacau",
                "Café arábica",
                "Café canephora",
                "Cana-de-açúcar",
                "Castanha-de-caju",
                "Cebola",
                "Coco-da-baía",
                "Fumo",
                "Guaraná",
                "Juta",
                "Laranja",
                "Maçã",
                "Malva",
                "Mandioca",
                "Pimenta-do-reino",
                "Sisal ou agave",
                "Tomate",
                "Uva"
        };
    }

    private String[] empregos() {
        return new String[] {
                "Caule",
                "Foliar",
                "Solo",
                "Tronco"
        };
    }
}
