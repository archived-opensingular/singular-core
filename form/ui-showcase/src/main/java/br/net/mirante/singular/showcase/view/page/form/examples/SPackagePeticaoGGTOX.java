/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.view.page.form.examples;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.SMultiSelectionByCheckboxView;
import br.net.mirante.singular.form.mform.basic.view.SMultiSelectionByPicklistView;
import br.net.mirante.singular.form.mform.basic.view.SMultiSelectionBySelectView;
import br.net.mirante.singular.form.mform.basic.view.SViewListByForm;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.basic.view.SViewListByTable;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionByRadio;
import br.net.mirante.singular.form.mform.basic.view.SViewSelectionBySelect;
import br.net.mirante.singular.form.mform.basic.view.SViewTab;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeDate;
import br.net.mirante.singular.form.mform.core.STypeDecimal;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.mform.util.brasil.STypeCNPJ;

public class SPackagePeticaoGGTOX extends SPackage {

    public static final String PACOTE = "mform.peticao";
    public static final String TIPO = "PeticionamentoGGTOX";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;
    private DadosResponsavel dadosResponsavel;
    private Componente componentes;


    public SPackagePeticaoGGTOX() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        final STypeComposite<?> peticionamento = pb.createCompositeType(TIPO);

        //TODO deveria ser possivel passar uma coleção para o withSelectionOf

        //TODO solicitar criacao de validacao para esse exemplo:
        /*
        MTipoComposto<MIComposto> sinonimiaComponente = peticionamento.addCampoComposto("sinonimiaComponente");

        sinonimiaComponente.asAtrBasic().label("Sinonímia");

        sinonimiaComponente.addCampoString("sinonimiaAssociada", true)
                .withSelectionOf("Sinonímia teste",
                        "Sinonímia teste 2",
                        "Sinonímia teste 3")
                .withView(MSelecaoMultiplaPorSelectView::new)
                .asAtrBasic()
                .label("Sinonímias já associadas a esta substância/mistura")
                .enabled(false);
        */

        dadosResponsavel = new DadosResponsavel(pb, peticionamento);
        componentes = new Componente(pb, peticionamento);

        SViewTab tabbed = new SViewTab();
        tabbed.addTab("tudo", "Tudo").add(dadosResponsavel.root).add(componentes.root);
        tabbed.addTab(dadosResponsavel.root);
        tabbed.addTab(componentes.root);
        peticionamento.withView(tabbed);

    }

    class DadosResponsavel {
        final String[] responsaveis = new String[]{"Daniel", "Delfino", "Fabrício", "Lucas", "Tetsuo", "Vinícius"};

        final STypeComposite<SIComposite> root;
        final STypeString responsavelTecnico, representanteLegal, concordo;

        DadosResponsavel(PackageBuilder pb, STypeComposite<?> peticionamento) {
            root = peticionamento.addFieldComposite("dadosResponsavel");

            root.asAtrBasic().label("Dados do Responsável");
            //TODO Como fazer a seleção para um objeto composto/enum ?
            //TODO a recuperação de valores deve ser dinamica

            responsavelTecnico = addPersonField("responsavelTecnico", "Responsável Técnico", 3);
            representanteLegal = addPersonField("representanteLegal", "Representante Legal", 3);
            concordo = createConcordoField();
        }

        private STypeString addPersonField(String fieldname, String label, int colPreference) {
            STypeString f = root.addFieldString(fieldname, true);
            f.withSelectionOf(responsaveis)
                    .withView(SViewSelectionBySelect::new)
                    .asAtrBasic().label(label)
                    .asAtrBootstrap().colPreference(colPreference);
            return f;
        }

        private STypeString createConcordoField() {
            // TODO preciso de um campo boolean mas as labels devem ser as descritas abaixo
            //TODO deve ser possivel alinhar o texto: text-left text-right text-justify text-nowrap
            STypeString field = root.addFieldString("concordo", true);
            field.withSelectionOf("Concordo", "Não Concordo").withView(SViewSelectionByRadio::new);
            return field;
        }

    }


    class Componente {
        final STypeList<STypeComposite<SIComposite>, SIComposite> root;
        final STypeComposite<SIComposite> rootType;
        final Identificacao identificacao;
        final Restricao restricao;
        final Sinonimia sinonimia;
        final Finalidade finalidade;
        final UsoPretendido usoPretendido;
        final NomeComercial nomeComercial;
        final Embalagem embalagem;
        final Anexo anexo;
        final TesteCaracteristicasFisicoQuimicas caracteristicasFisicoQuimicas;
        final TesteIrritacaoOcular irritacaoOcular;

        Componente(PackageBuilder pb, STypeComposite<?> peticionamento) {
            root = peticionamento.addFieldListOfComposite("componentes", "componente");
            root.asAtrBasic().label("Componente");
            rootType = root.getElementsType();
            rootType.asAtrBasic().label("Registro de Componente");

            identificacao = new Identificacao(pb);
            restricao = new Restricao(pb);
            sinonimia = new Sinonimia(pb);
            finalidade = new Finalidade(pb);
            usoPretendido = new UsoPretendido(pb);
            nomeComercial = new NomeComercial(pb);
            embalagem = new Embalagem(pb);
            anexo = new Anexo(pb);

            caracteristicasFisicoQuimicas = new TesteCaracteristicasFisicoQuimicas(pb);
            irritacaoOcular = new TesteIrritacaoOcular(pb);

            root.withView(new SViewListByMasterDetail()
                            .col(identificacao.tipoComponente)
//                    .col(sinonimia.sugerida)
            );
        }

        class Identificacao {
            final STypeComposite<SIComposite> root;
            final STypeString tipoComponente;

            Identificacao(PackageBuilder pb) {
                root = rootType.addFieldComposite("identificacaoComponente");
                root.asAtrBasic().label("Identificação de Componente")
                        .asAtrBootstrap().colPreference(4);

                tipoComponente = root.addFieldString("tipoComponente", true);
                tipoComponente.withSelectionOf("Substância", "Mistura")
                        .withView(SViewSelectionByRadio::new)
                        .asAtrBasic().label("Tipo componente");
            }
        }

        class Restricao {
            final STypeComposite<SIComposite> root;

            Restricao(PackageBuilder pb) {
                root = rootType.addFieldComposite("restricoesComponente");
                root.asAtrBasic().label("Restrições")
                        .asAtrBootstrap().colPreference(4);

                //TODO caso eu marque sem restrições os outros campos devem ser desabilitados
                STypeString restricao = pb.createType("restricao", STypeString.class)
                        .withSelectionOf("Impureza relevante presente",
                                "Controle de impureza determinado",
                                "Restrição de uso em algum país",
                                "Restrição de uso em alimentos",
                                "Sem restrições").cast();

                root.addFieldListOf("restricoes", restricao)
                        .withView(SMultiSelectionByCheckboxView::new)
                        .asAtrBasic().label("Restrições");

            }
        }

        class Sinonimia {
            final STypeComposite<SIComposite> root;
            final STypeList<STypeString, SIString> lista;
            final STypeString sugerida;

            Sinonimia(PackageBuilder pb) {
                root = rootType.addFieldComposite("sinonimiaComponente");
                root.asAtrBasic().label("Sinonímia").asAtrBootstrap().colPreference(4);

                lista = createListaField(pb);
                sugerida = createSugeridaField();

            }

            private STypeList<STypeString, SIString> createListaField(PackageBuilder pb) {
                STypeString sinonimia = pb.createType("sinonimia", STypeString.class)
                        .withSelectionOf("Sinonímia teste", "Sinonímia teste 2", "Sinonímia teste 3").cast();
                STypeList<STypeString, SIString> field = root.addFieldListOf("sinonimiaAssociada",
                        sinonimia);
                field.withView(SMultiSelectionBySelectView::new)
                        .asAtrBasic()
                        .label("Sinonímias já associadas a esta substância/mistura")
                        .enabled(false);
                return field;
            }

            private STypeString createSugeridaField() {
                final STypeList<STypeComposite<SIComposite>, SIComposite> sinonimias = root.addFieldListOfComposite("sinonimias", "sinonimia");
                final STypeComposite<?> sinonimia = sinonimias.getElementsType();

                sinonimias.withView(SViewListByTable::new);
                sinonimias.asAtrBasic()
                        .label("Lista de sinonímias sugeridas para esta substância/mistura");

                STypeString field = sinonimia.addFieldString("nomeSinonimia", true);

                field.asAtrBasic().label("Sinonímia sugerida").tamanhoMaximo(100);

                return field;
            }
        }

        class Finalidade {
            final STypeComposite<SIComposite> root;

            Finalidade(PackageBuilder pb) {
                root = rootType.addFieldComposite("finalidadesComponente");

                root.asAtrBasic().label("Finalidades")
                        .asAtrBootstrap().colPreference(4);

                STypeString finalidade = pb.createType("finalidade", STypeString.class)
                        .withSelectionOf("Produção", "Importação", "Exportação", "Comercialização", "Utilização").cast();
                root.addFieldListOf("finalidades", finalidade)
                        .withView(SMultiSelectionByCheckboxView::new);
            }
        }

        class UsoPretendido {
            final STypeComposite<SIComposite> root;

            UsoPretendido(PackageBuilder pb) {
                //TODO falta criar modal para cadastrar novo uso pretendido
                root = rootType.addFieldComposite("usosPretendidosComponente");

                root.asAtrBasic().label("Uso pretendido").asAtrBootstrap().colPreference(4);

                STypeString usoPretendido = pb.createType("usoPretendido", STypeString.class)
                        .withSelectionOf("Uso 1", "Uso 2", "Uso 3").cast();
                root.addFieldListOf("usosPretendidos",
                        usoPretendido)
                        .withView(SMultiSelectionByPicklistView::new)
                        .asAtrBasic().label("Lista de uso pretendido/mistura");
            }
        }

        class NomeComercial {
            final STypeList<STypeComposite<SIComposite>, SIComposite> root;
            final STypeComposite<SIComposite> type;
            final STypeString nome;
            final Fabricante fabricante;

            NomeComercial(PackageBuilder pb) {
                root = rootType.addFieldListOfComposite("nomesComerciais", "nomeComercial");
                root.withView(SViewListByForm::new).asAtrBasic().label("Nome comercial");
                type = root.getElementsType();

                nome = type.addFieldString("nome", true);
                nome.asAtrBasic().label("Nome comercial").tamanhoMaximo(80);

                fabricante = new Fabricante(pb);
            }

            class Fabricante {
                final STypeList<STypeComposite<SIComposite>, SIComposite> root;
                final STypeComposite<SIComposite> type;
                final STypeCNPJ cnpj;
                final STypeString razaoSocial, cidade, pais;

                Fabricante(PackageBuilder pb) {
                    root = NomeComercial.this.type.addFieldListOfComposite("fabricantes", "fabricante");
                    root.withView(SViewListByMasterDetail::new).asAtrBasic().label("Fabricante(s)");

                    //TODO Fabricante deve ser uma pesquisa
                    type = root.getElementsType();
                    //TODO como usar o tipo cnpj
                    cnpj = type.addField("cnpj", STypeCNPJ.class);
                    cnpj.asAtrBasic().label("CNPJ").asAtrBootstrap().colPreference(4);
                    razaoSocial = createStringField(type, "razaoSocial", "Razão social", 4);
                    cidade = createStringField(type, "cidade", "Cidade", 2);
                    pais = createStringField(type, "pais", "País", 2);

                }

                private STypeString createStringField(STypeComposite<SIComposite> fabricante, String fieldname, String label, int colPreference) {
                    STypeString f = fabricante.addFieldString(fieldname);
                    f.asAtrBasic().label(label).asAtrBootstrap().colPreference(colPreference);
                    return f;
                }
            }
        }

        class Embalagem {
            final STypeList<STypeComposite<SIComposite>, SIComposite> root;
            final STypeComposite<SIComposite> type;
            final STypeString produtoExterior;
            final STypeString tipo;
            STypeString material;
            final STypeString unidadeMedida;
            final STypeInteger capacidade;
            private final String[]
                    tiposDisponiveis = new String[]{
                    "Balde", "Barrica", "Bombona", "Caixa", "Carro tanque", "Cilindro",
                    "Container", "Frasco", "Galão", "Garrafa", "Lata", "Saco", "Tambor"
            },
                    materiaisDisponiveis = new String[]{"Papel", "Alumínio", "Ferro", "Madeira"
                    };

            Embalagem(PackageBuilder pb) {
                root = rootType.addFieldListOfComposite("embalagens", "embalagem");
                root.withView(SViewListByTable::new).asAtrBasic().label("Embalagem");
                type = root.getElementsType();
                produtoExterior = createFieldProdutoExterior();
                tipo = createFieldTipo();
                material = createFieldMaterial();
                capacidade = createFieldCapacidade();
                unidadeMedida = createFieldUnidadeDeMedida();
            }

            private STypeString createFieldProdutoExterior() {
                //TODO converter sim nao para true false
                STypeString field = type.addFieldString("produtoExterior", true);
                field.withSelectionOf("Sim", "Não")
                        .withView(SViewSelectionByRadio::new)
                        .asAtrBasic().label("Produto formulado no exterior?")
                        .asAtrBootstrap().colPreference(12);
                return field;
            }

            private STypeString createFieldTipo() {
                STypeString field = type.addFieldString("tipo", true);
                field.withSelectionOf(tiposDisponiveis)
                        .withView(SViewSelectionBySelect::new)
                        .asAtrBasic().label("Tipo")
                        .asAtrBootstrap().colPreference(4);
                return field;
            }

            private STypeString createFieldMaterial() {
                STypeString field = type.addFieldString("material", true);
                field.withSelectionOf(materiaisDisponiveis)
                        .withView(SViewSelectionBySelect::new)
                        .asAtrBasic().label("Material")
                        .asAtrBootstrap().colPreference(4);
                return field;
            }

            private STypeInteger createFieldCapacidade() {
                STypeInteger field = type.addFieldInteger("capacidade", true);
                field.asAtrBasic().label("Capacidade")
                        .tamanhoMaximo(15)
                        .asAtrBootstrap().colPreference(4);
                return field;
            }

            private STypeString createFieldUnidadeDeMedida() {
                //TODO caso o array tenha uma string vazia, ocorre um NPE
                STypeString field = type.addFieldString("unidadeMedida", true);
                field.withSelectionOf(new String[]{"cm"}).withView(SViewSelectionBySelect::new)
                        .asAtrBasic().label("Unidade medida")
                        .asAtrBootstrap().colPreference(1);
                return field;
            }

        }

        class Anexo {
            final STypeList<STypeComposite<SIComposite>, SIComposite> root;
            final STypeComposite<SIComposite> type;
            final STypeAttachment arquivo;
            final STypeString tipo;

            Anexo(PackageBuilder pb) {
                root = rootType.addFieldListOfComposite("anexos", "anexo");
                root.asAtrBasic().label("Anexos");
                type = root.getElementsType();

                arquivo = createArquivoField();
                tipo = createTipoField();

                SType<?> nomeArquivo = (STypeSimple) arquivo.getField(arquivo.FIELD_NAME);
                nomeArquivo.asAtrBasic().label("Nome do Arquivo");
                root.withView(new SViewListByMasterDetail()
                                .col((STypeSimple) nomeArquivo)
                                .col(tipo)
                );
            }

            private STypeAttachment createArquivoField() {
                STypeAttachment f = type.addField("arquivo", STypeAttachment.class);
                f.as(AtrBasic.class).label("Informe o caminho do arquivo para o anexo")
                        .asAtrBootstrap().colPreference(9);
                return f;
            }

            private STypeString createTipoField() {
                STypeString t = type.addFieldString("tipoArquivo");
                t.withSelectionOf("Ficha de emergência", "Ficha de segurança", "Outros")
                        .withView(SViewSelectionBySelect::new)
                        .asAtrBasic().label("Tipo do arquivo a ser anexado")
                        .asAtrBootstrap().colPreference(3);
                return t;
            }
        }

        class TesteCaracteristicasFisicoQuimicas {
            final STypeList<STypeComposite<SIComposite>, SIComposite> root;
            final STypeComposite<SIComposite> type;
            final STypeString estadoFisico, aspecto, cor, odor, hidrolise, estabilidade, observacoes;
            final STypeDecimal pontoFulgor, constanteDissociacao, coeficienteParticao, densidade;
            final Faixa fusao, ebulicao;
            final PressaoDeValor pressaoDeVapor;
            final Solubilidade solubilidade;
            final PotenciaDeHidrogenio ph;

            private TesteCaracteristicasFisicoQuimicas(PackageBuilder pb) {
                root = rootType.addFieldListOfComposite("testesCaracteristicasFisicoQuimicas", "caracteristicasFisicoQuimicas");
                root.asAtrBasic().label("Testes Características fisíco-químicas");
                type = root.getElementsType();
                type.asAtrBasic().label("Características fisíco-químicas");

                estadoFisico = createEstadoFísicoField();
                aspecto = createAspectoField();
                cor = createCorField();
                odor = createOdorField();

                root.withView(new SViewListByMasterDetail()
                                .col(estadoFisico)
                                .col(aspecto)
                                .col(cor)
                                .col(odor)
                );

                fusao = new Faixa(pb, "Fusao", "Fusão");
                ebulicao = new Faixa(pb, "Ebulicao", "Ebulição");
                pressaoDeVapor = new PressaoDeValor(pb);
                solubilidade = new Solubilidade(pb);

                hidrolise = createHidroliseField();
                estabilidade = createEstabilidadeField();
                pontoFulgor = createPontoFulgorField();
                constanteDissociacao = createConstanteDissociacaoField();
                ph = new PotenciaDeHidrogenio(pb);

                coeficienteParticao = createDecimalField("coeficienteParticao", "Coeficiente de partição octanol/Água", "a 20-25 ºC", 4);
                densidade = createDecimalField("densidade", "Densidade", "g/cm³ a 20ºC", 4);

                observacoes = type.addFieldString("observacoes");
                observacoes.withTextAreaView().asAtrBasic().label("Observações");
            }

            private STypeDecimal createDecimalField(String fieldname, String label, String subtitle, int colPreference) {
                STypeDecimal f = type.addFieldDecimal(fieldname);
                f.asAtrBasic().label(label).subtitle(subtitle)
                        .asAtrBootstrap().colPreference(colPreference);
                return f;
            }

            private STypeString createEstadoFísicoField() {
                STypeString f = type.addFieldString("estadoFisico", true);
                f.withSelectionOf("Líquido", "Sólido", "Gasoso")
                        .withView(SViewSelectionBySelect::new)
                        .asAtrBasic().label("Estado físico")
                        .asAtrBootstrap().colPreference(2);
                return f;
            }

            private STypeString createAspectoField() {
                STypeString f = type.addFieldString("aspecto", true);
                f.asAtrBasic().label("Aspecto")
                        .tamanhoMaximo(50)
                        .asAtrBootstrap().colPreference(4);
                return f;
            }

            private STypeString createCorField() {
                STypeString f = type.addFieldString("cor", true);
                f.asAtrBasic().label("Cor")
                        .tamanhoMaximo(40)
                        .asAtrBootstrap().colPreference(3);
                return f;
            }

            private STypeString createOdorField() {
                STypeString f = type.addFieldString("odor");
                f.asAtrBasic().label("Odor")
                        .tamanhoMaximo(40)
                        .asAtrBootstrap().colPreference(3);
                return f;
            }

            private STypeString createHidroliseField() {
                STypeString f = type.addFieldString("hidrolise");
                f.asAtrBasic().label("Hidrólise")
                        .asAtrBootstrap().colPreference(6);
                return f;
            }

            private STypeString createEstabilidadeField() {
                STypeString f = type.addFieldString("estabilidade");
                f.asAtrBasic().label("Estabilidade às temperaturas normal e elevada")
                        .asAtrBootstrap().colPreference(6);
                return f;
            }

            private STypeDecimal createPontoFulgorField() {
                STypeDecimal f = createDecimalField("pontoFulgor", "Ponto de fulgor", "ºC", 3);
                return f;
            }

            private STypeDecimal createConstanteDissociacaoField() {
                STypeDecimal f = type.addFieldDecimal("constanteDissociacao");
                f.asAtrBasic().label("Constante de dissociação")
                        .asAtrBootstrap().colPreference(3);
                return f;
            }

            class Faixa {
                final STypeComposite<SIComposite> root;
                final STypeDecimal pontoFusao, faixaFusaoDe, faixaFusaoA;

                Faixa(PackageBuilder pb, String prefix, String nome) {
                    root = type.addFieldComposite("faixa" + prefix);
                    root.asAtrBootstrap().colPreference(6).asAtrBasic().label(nome);

                    pontoFusao = createDecimalField("ponto" + prefix, "Ponto de " + nome, "ºC");
                    //TODO o campo faixa de fusao precisa de um tipo intervalo
                    // Exemplo: Faixa De 10 a 20
                    faixaFusaoDe = createDecimalField("faixa" + prefix + "De", "Início", "da Faixa");
                    faixaFusaoA = createDecimalField("faixa" + prefix + "A", "Fim", "da Faixa");
                }

                private STypeDecimal createDecimalField(String fieldname, String label, String subtitle) {
                    SType<?> f = root.addFieldDecimal(fieldname);
                    f.asAtrBasic().label(label).subtitle(subtitle)
                            .asAtrBootstrap().colPreference(4);
                    return (STypeDecimal) f;
                }
            }

            class PressaoDeValor {
                final STypeComposite<SIComposite> root;
                final STypeDecimal valor;
                final STypeString unidade;

                PressaoDeValor(PackageBuilder pb) {
                    root = type.addFieldComposite("pressaoVapor");
                    root.asAtrBasic().label("Pressão do vapor")
                            .asAtrBootstrap().colPreference(6);

                    valor = createValorField();
                    unidade = createUnidadeField();

                }

                private STypeDecimal createValorField() {
                    STypeDecimal f = root.addFieldDecimal("valor");
                    f.asAtrBasic().label("Valor")
                            .asAtrBootstrap().colPreference(6);
                    return f;
                }

                private STypeString createUnidadeField() {
                    STypeString f = root.addFieldString("unidade");
                    f.withSelectionOf("mmHg", "Pa", "mPa")
                            .withView(SViewSelectionBySelect::new)
                            .asAtrBasic().label("Unidade")
                            .asAtrBootstrap().colPreference(6);
                    return f;
                }
            }

            class Solubilidade {
                final STypeComposite<SIComposite> root;
                final STypeDecimal agua, outrosSolventes;

                Solubilidade(PackageBuilder pb) {
                    root = type.addFieldComposite("solubilidade");
                    root.asAtrBasic().label("Solubilidade")
                            .asAtrBootstrap().colPreference(6);

                    agua = createDecimalField("solubilidadeAgua", "em água", "mg/L a 20 ou 25 ºC");
                    outrosSolventes = createDecimalField("solubilidadeOutrosSolventes",
                            "em outros solventes", "mg/L a 20 ou 25 ºC");
                }

                private STypeDecimal createDecimalField(String fieldName, String label, String subtitle) {
                    STypeDecimal f = root.addFieldDecimal(fieldName);
                    f.asAtrBasic().label(label).subtitle(subtitle)
                            .asAtrBootstrap().colPreference(6);
                    return f;
                }
            }

            class PotenciaDeHidrogenio {
                final STypeList<STypeComposite<SIComposite>, SIComposite> root;
                final STypeComposite<SIComposite> type;
                final STypeDecimal valorPh, solucao, temperatura;

                PotenciaDeHidrogenio(PackageBuilder pb) {
                    root = TesteCaracteristicasFisicoQuimicas.this.type.addFieldListOfComposite("phs", "ph");
                    root.withView(SViewListByForm::new).asAtrBasic().label("Lista de pH");
                    type = root.getElementsType();

                    valorPh = createDecimalField("valorPh", "pH", ".");
                    solucao = createDecimalField("solucao", "Solução", "%");
                    temperatura = createDecimalField("temperatura", "Temperatura", "ºC");
                }

                private STypeDecimal createDecimalField(String fieldname, String label, String subtitle) {
                    STypeDecimal valorPh = type.addFieldDecimal(fieldname, true);
                    valorPh.asAtrBasic().label(label).subtitle(subtitle)
                            .asAtrBootstrap().colPreference(4);
                    return valorPh;
                }
            }
        }

        class TesteIrritacaoOcular {
            final STypeList<STypeComposite<SIComposite>, SIComposite> root;
            final STypeComposite<SIComposite> type;
            final STypeString laboratorio, protocolo, purezaProdutoTestado, unidadeMedida, especies,
                    linhagem, veiculo, fluoresceina, testeRealizado;
            final STypeDate inicio, fim;
            final Alteracao alteracao;

            class Alteracao {
                final STypeComposite<SIComposite> root;
                final STypeString cornea, tempoReversibilidadeCornea, conjuntiva, tempoReversibilidadeConjuntiva,
                        iris, tempoReversibilidadeIris;

                Alteracao(PackageBuilder pb) {
                    root = type.addFieldComposite("alteracoes");
                    root.asAtrBasic().label("Alterações");

                    cornea = createStringField("cornea", "Córnea", 6);
                    cornea.withSelectionOf("Sem alterações", "Opacidade persistente", "Opacidade reversível em...");

                    tempoReversibilidadeCornea = createStringField("tempoReversibilidadeCornea", "Tempo de reversibilidade", 6);

                    conjuntiva = createStringField("conjuntiva", "Conjuntiva", 6);
                    conjuntiva.withSelectionOf("Sem alterações", "Opacidade persistente",
                            "Opacidade reversível em...");

                    tempoReversibilidadeConjuntiva = createStringField("tempoReversibilidadeConjuntiva", "Tempo de reversibilidade", 6);

                    iris = createStringField("iris", "Íris", 6);
                    iris.withSelectionOf("Sem alterações", "Opacidade persistente", "Opacidade reversível em...");

                    tempoReversibilidadeIris = createStringField("tempoReversibilidadeIris", "Tempo de reversibilidade", 6);
                }

                private STypeString createStringField(String fieldname, String label,
                                                      int colPreference) {
                    STypeString f = root.addFieldString(fieldname);
                    f.asAtrBasic().label(label)
                            .asAtrBootstrap().colPreference(colPreference);
                    return f;
                }
            }

            private TesteIrritacaoOcular(PackageBuilder pb) {
                //TODO criar regra para pelo menos um campo preenchido
                root = rootType.addFieldListOfComposite("testesIrritacaoOcular", "irritacaoOcular");
                root.asAtrBasic().label("Testes Irritação / Corrosão ocular");
                type = root.getElementsType();
                type.asAtrBasic().label("Irritação / Corrosão ocular")
                        .asAtrBootstrap().colPreference(4);

                laboratorio = createStringField("laboratorio", "Laboratório", 50, 12);
                protocolo = createStringField("protocoloReferencia", "Protocolo de referência", 50, 12);
                inicio = createDateField("dataInicioEstudo", "Data de início do estudo", 3);
                fim = createDateField("dataFimEstudo", "Data final do estudo", 3);

                root.withView(new SViewListByMasterDetail()
                                .col(laboratorio)
                                .col(protocolo)
                                .col(inicio)
                                .col(fim)
                );

                purezaProdutoTestado = createStringField("purezaProdutoTestado", "Pureza do produto testado", null, 6);

                unidadeMedida = createStringField("unidadeMedida", "Unidade de medida", null, 2);
                unidadeMedida.withSelectionOf("g/Kg", "g/L");

                especies = createStringField("especies", "Espécies", null, 4);
                especies.withSelectionOf("Càes", "Camundongos", "Cobaia", "Coelho", "Galinha",
                        "Informação não disponível", "Peixe", "Primatas", "Rato");

                linhagem = createStringField("linhagem", "Linhagem", null, 6);

                type.addFieldDecimal("numeroAnimais")
                        .asAtrBasic().label("Número de animais")
                        .asAtrBootstrap().colPreference(3);

                veiculo = createStringField("veiculo", "Veículo", null, 3);

                fluoresceina = createStringField("fluoresceina", "Fluoresceína", null, 3);
                fluoresceina.withView(new SViewSelectionByRadio());
                fluoresceina.withSelectionOf("Sim", "Não");

                testeRealizado = createStringField("testeRealizado", "Teste realizado", null, 3);
                testeRealizado.withSelectionOf("Com lavagem", "Sem lavagem");

                alteracao = new Alteracao(pb);

                type.addFieldString("observacoes")
                        .withTextAreaView()
                        .asAtrBasic().label("Observações");
            }

            private STypeString createStringField(String fieldname, String label, Integer maxSize, Integer colPreference) {
                STypeString f = type.addFieldString(fieldname);
                f.asAtrBasic().label(label).tamanhoMaximo(maxSize)
                        .asAtrBootstrap().colPreference(colPreference);
                return f;
            }

            private STypeDate createDateField(String fieldName, String label, int colPreference) {
                STypeDate f = type.addFieldDate(fieldName);
                f.asAtrBasic().label(label).asAtrBootstrap().colPreference(colPreference);
                return f;
            }
        }
    }
}

