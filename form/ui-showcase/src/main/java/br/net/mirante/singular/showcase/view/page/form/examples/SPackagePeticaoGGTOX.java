package br.net.mirante.singular.showcase.view.page.form.examples;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.basic.view.MListMasterDetailView;
import br.net.mirante.singular.form.mform.basic.view.MPanelListaView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorCheckView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorPicklistView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorSelectView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorRadioView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorSelectView;
import br.net.mirante.singular.form.mform.basic.view.MTabView;
import br.net.mirante.singular.form.mform.basic.view.MTableListaView;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.mform.core.STypeDecimal;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.mform.util.comuns.STypeCNPJ;

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

        final STypeComposite<?> peticionamento = pb.createTipoComposto(TIPO);

        //TODO deveria ser possivel passar uma coleção para o withSelectionOf

        //TODO solicitar criacao de validacao para esse exemplo:
        /*
        MTipoComposto<MIComposto> sinonimiaComponente = peticionamento.addCampoComposto("sinonimiaComponente");
        
        sinonimiaComponente.as(AtrBasic::new).label("Sinonímia");
        
        sinonimiaComponente.addCampoString("sinonimiaAssociada", true)
                .withSelectionOf("Sinonímia teste",
                        "Sinonímia teste 2",
                        "Sinonímia teste 3")
                .withView(MSelecaoMultiplaPorSelectView::new)
                .as(AtrBasic::new)
                .label("Sinonímias já associadas a esta substância/mistura")
                .enabled(false);
        */

        dadosResponsavel = new DadosResponsavel(pb, peticionamento);
        componentes = new Componente(pb, peticionamento);

        MTabView tabbed = new MTabView();
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
            root = peticionamento.addCampoComposto("dadosResponsavel");

            root.as(AtrBasic::new).label("Dados do Responsável");
            //TODO Como fazer a seleção para um objeto composto/enum ?
            //TODO a recuperação de valores deve ser dinamica

            responsavelTecnico = addPersonField("responsavelTecnico", "Responsável Técnico", 3);
            representanteLegal = addPersonField("representanteLegal", "Representante Legal", 3);
            concordo = createConcordoField();
        }

        private STypeString addPersonField(String fieldname, String label, int colPreference) {
            STypeString f = root.addCampoString(fieldname, true);
            f.withSelectionOf(responsaveis)
                    .withView(MSelecaoPorSelectView::new)
                    .as(AtrBasic::new).label(label)
                    .as(AtrBootstrap::new).colPreference(colPreference);
            return f;
        }

        private STypeString createConcordoField() {
            // TODO preciso de um campo boolean mas as labels devem ser as descritas abaixo
            //TODO deve ser possivel alinhar o texto: text-left text-right text-justify text-nowrap
            STypeString field = root.addCampoString("concordo", true);
            field.withSelectionOf("Concordo", "Não Concordo").withView(MSelecaoPorRadioView::new);
            return field;
        }

    }


    class Componente {
        final STypeLista<STypeComposite<SIComposite>, SIComposite> root;
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
            root = peticionamento.addCampoListaOfComposto("componentes", "componente");
            root.as(AtrBasic::new).label("Componente");
            rootType = root.getTipoElementos();
            rootType.as(AtrBasic::new).label("Registro de Componente");

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

            root.withView(new MListMasterDetailView()
                            .col(identificacao.tipoComponente)
//                    .col(sinonimia.sugerida)
            );
        }

        class Identificacao {
            final STypeComposite<SIComposite> root;
            final STypeString tipoComponente;

            Identificacao(PackageBuilder pb) {
                root = rootType.addCampoComposto("identificacaoComponente");
                root.as(AtrBasic::new).label("Identificação de Componente")
                        .as(AtrBootstrap::new).colPreference(4);

                tipoComponente = root.addCampoString("tipoComponente", true);
                tipoComponente.withSelectionOf("Substância", "Mistura")
                        .withView(MSelecaoPorRadioView::new)
                        .as(AtrBasic::new).label("Tipo componente");
            }
        }

        class Restricao {
            final STypeComposite<SIComposite> root;

            Restricao(PackageBuilder pb) {
                root = rootType.addCampoComposto("restricoesComponente");
                root.as(AtrBasic::new).label("Restrições")
                        .as(AtrBootstrap::new).colPreference(4);

                //TODO caso eu marque sem restrições os outros campos devem ser desabilitados
                STypeString restricao = pb.createTipo("restricao", STypeString.class)
                        .withSelectionOf("Impureza relevante presente",
                                "Controle de impureza determinado",
                                "Restrição de uso em algum país",
                                "Restrição de uso em alimentos",
                                "Sem restrições");

                root.addCampoListaOf("restricoes", restricao)
                        .withView(MSelecaoMultiplaPorCheckView::new)
                        .as(AtrBasic::new).label("Restrições");

            }
        }

        class Sinonimia {
            final STypeComposite<SIComposite> root;
            final STypeLista<STypeString, SIString> lista;
            final STypeString sugerida;

            Sinonimia(PackageBuilder pb) {
                root = rootType.addCampoComposto("sinonimiaComponente");
                root.as(AtrBasic::new).label("Sinonímia").as(AtrBootstrap::new).colPreference(4);

                lista = createListaField(pb);
                sugerida = createSugeridaField();

            }

            private STypeLista<STypeString, SIString> createListaField(PackageBuilder pb) {
                STypeString sinonimia = pb.createTipo("sinonimia", STypeString.class)
                        .withSelectionOf("Sinonímia teste", "Sinonímia teste 2", "Sinonímia teste 3");
                STypeLista<STypeString, SIString> field = root.addCampoListaOf("sinonimiaAssociada",
                        sinonimia);
                field.withView(MSelecaoMultiplaPorSelectView::new)
                        .as(AtrBasic::new)
                        .label("Sinonímias já associadas a esta substância/mistura")
                        .enabled(false);
                return field;
            }

            private STypeString createSugeridaField() {
                final STypeLista<STypeComposite<SIComposite>, SIComposite> sinonimias = root.addCampoListaOfComposto("sinonimias", "sinonimia");
                final STypeComposite<?> sinonimia = sinonimias.getTipoElementos();

                sinonimias.withView(MTableListaView::new);
                sinonimias.as(AtrBasic::new)
                        .label("Lista de sinonímias sugeridas para esta substância/mistura");

                STypeString field = sinonimia.addCampoString("nomeSinonimia", true);

                field.as(AtrBasic::new).label("Sinonímia sugerida").tamanhoMaximo(100);

                return field;
            }
        }

        class Finalidade {
            final STypeComposite<SIComposite> root;

            Finalidade(PackageBuilder pb) {
                root = rootType.addCampoComposto("finalidadesComponente");

                root.as(AtrBasic::new).label("Finalidades")
                        .as(AtrBootstrap::new).colPreference(4);

                STypeString finalidade = pb.createTipo("finalidade", STypeString.class)
                        .withSelectionOf("Produção", "Importação", "Exportação", "Comercialização", "Utilização");
                root.addCampoListaOf("finalidades", finalidade)
                        .withView(MSelecaoMultiplaPorCheckView::new);
            }
        }

        class UsoPretendido {
            final STypeComposite<SIComposite> root;

            UsoPretendido(PackageBuilder pb) {
                //TODO falta criar modal para cadastrar novo uso pretendido
                root = rootType.addCampoComposto("usosPretendidosComponente");

                root.as(AtrBasic::new).label("Uso pretendido").as(AtrBootstrap::new).colPreference(4);

                STypeString usoPretendido = pb.createTipo("usoPretendido", STypeString.class)
                        .withSelectionOf("Uso 1", "Uso 2", "Uso 3");
                root.addCampoListaOf("usosPretendidos",
                        usoPretendido)
                        .withView(MSelecaoMultiplaPorPicklistView::new)
                        .as(AtrBasic::new).label("Lista de uso pretendido/mistura");
            }
        }

        class NomeComercial {
            final STypeLista<STypeComposite<SIComposite>, SIComposite> root;
            final STypeComposite<SIComposite> type;
            final STypeString nome;
            final Fabricante fabricante;

            NomeComercial(PackageBuilder pb) {
                root = rootType.addCampoListaOfComposto("nomesComerciais", "nomeComercial");
                root.withView(MPanelListaView::new).as(AtrBasic::new).label("Nome comercial");
                type = root.getTipoElementos();

                nome = type.addCampoString("nome", true);
                nome.as(AtrBasic::new).label("Nome comercial").tamanhoMaximo(80);

                fabricante = new Fabricante(pb);
            }

            class Fabricante {
                final STypeLista<STypeComposite<SIComposite>, SIComposite> root;
                final STypeComposite<SIComposite> type;
                final STypeCNPJ cnpj;
                final STypeString razaoSocial, cidade, pais;

                Fabricante(PackageBuilder pb) {
                    root = NomeComercial.this.type.addCampoListaOfComposto("fabricantes", "fabricante");
                    root.withView(MListMasterDetailView::new).as(AtrBasic::new).label("Fabricante(s)");

                    //TODO Fabricante deve ser uma pesquisa
                    type = root.getTipoElementos();
                    //TODO como usar o tipo cnpj
                    cnpj = type.addCampo("cnpj", STypeCNPJ.class);
                    cnpj.as(AtrBasic::new).label("CNPJ").as(AtrBootstrap::new).colPreference(4);
                    razaoSocial = createStringField(type, "razaoSocial", "Razão social", 4);
                    cidade = createStringField(type, "cidade", "Cidade", 2);
                    pais = createStringField(type, "pais", "País", 2);

                }

                private STypeString createStringField(STypeComposite<SIComposite> fabricante, String fieldname, String label, int colPreference) {
                    STypeString f = fabricante.addCampoString(fieldname);
                    f.as(AtrBasic::new).label(label).as(AtrBootstrap::new).colPreference(colPreference);
                    return f;
                }
            }
        }

        class Embalagem {
            final STypeLista<STypeComposite<SIComposite>, SIComposite> root;
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
                root = rootType.addCampoListaOfComposto("embalagens", "embalagem");
                root.withView(MTableListaView::new).as(AtrBasic::new).label("Embalagem");
                type = root.getTipoElementos();
                produtoExterior = createFieldProdutoExterior();
                tipo = createFieldTipo();
                material = createFieldMaterial();
                capacidade = createFieldCapacidade();
                unidadeMedida = createFieldUnidadeDeMedida();
            }

            private STypeString createFieldProdutoExterior() {
                //TODO converter sim nao para true false
                STypeString field = type.addCampoString("produtoExterior", true);
                field.withSelectionOf("Sim", "Não")
                        .withView(MSelecaoPorRadioView::new)
                        .as(AtrBasic::new).label("Produto formulado no exterior?")
                        .as(AtrBootstrap::new).colPreference(12);
                return field;
            }

            private STypeString createFieldTipo() {
                STypeString field = type.addCampoString("tipo", true);
                field.withSelectionOf(tiposDisponiveis)
                        .withView(MSelecaoPorSelectView::new)
                        .as(AtrBasic::new).label("Tipo")
                        .as(AtrBootstrap::new).colPreference(4);
                return field;
            }

            private STypeString createFieldMaterial() {
                STypeString field = type.addCampoString("material", true);
                field.withSelectionOf(materiaisDisponiveis)
                        .withView(MSelecaoPorSelectView::new)
                        .as(AtrBasic::new).label("Material")
                        .as(AtrBootstrap::new).colPreference(4);
                return field;
            }

            private STypeInteger createFieldCapacidade() {
                STypeInteger field = type.addCampoInteger("capacidade", true);
                field.as(AtrBasic::new).label("Capacidade")
                        .tamanhoMaximo(15)
                        .as(AtrBootstrap::new).colPreference(4);
                return field;
            }

            private STypeString createFieldUnidadeDeMedida() {
                //TODO caso o array tenha uma string vazia, ocorre um NPE
                STypeString field = type.addCampoString("unidadeMedida", true);
                field.withSelectionOf(new String[]{"cm"}).withView(MSelecaoPorSelectView::new)
                        .as(AtrBasic::new).label("Unidade medida")
                        .as(AtrBootstrap::new).colPreference(1);
                return field;
            }

        }

        class Anexo {
            final STypeLista<STypeComposite<SIComposite>, SIComposite> root;
            final STypeComposite<SIComposite> type;
            final STypeAttachment arquivo;
            final STypeString tipo;

            Anexo(PackageBuilder pb) {
                root = rootType.addCampoListaOfComposto("anexos", "anexo");
                root.as(AtrBasic::new).label("Anexos");
                type = root.getTipoElementos();

                arquivo = createArquivoField();
                tipo = createTipoField();

                SType<?> nomeArquivo = (STypeSimple) arquivo.getCampo(arquivo.FIELD_NAME);
                nomeArquivo.as(AtrBasic::new).label("Nome do Arquivo");
                root.withView(new MListMasterDetailView()
                                .col((STypeSimple) nomeArquivo)
                                .col(tipo)
                );
            }

            private STypeAttachment createArquivoField() {
                STypeAttachment f = type.addCampo("arquivo", STypeAttachment.class);
                f.as(AtrBasic.class).label("Informe o caminho do arquivo para o anexo")
                        .as(AtrBootstrap::new).colPreference(9);
                return f;
            }

            private STypeString createTipoField() {
                STypeString t = type.addCampoString("tipoArquivo");
                t.withSelectionOf("Ficha de emergência", "Ficha de segurança", "Outros")
                        .withView(MSelecaoPorSelectView::new)
                        .as(AtrBasic::new).label("Tipo do arquivo a ser anexado")
                        .as(AtrBootstrap::new).colPreference(3);
                return t;
            }
        }

        class TesteCaracteristicasFisicoQuimicas {
            final STypeLista<STypeComposite<SIComposite>, SIComposite> root;
            final STypeComposite<SIComposite> type;
            final STypeString estadoFisico, aspecto, cor, odor, hidrolise, estabilidade, observacoes;
            final STypeDecimal pontoFulgor, constanteDissociacao, coeficienteParticao, densidade;
            final Faixa fusao, ebulicao;
            final PressaoDeValor pressaoDeVapor;
            final Solubilidade solubilidade;
            final PotenciaDeHidrogenio ph;

            private TesteCaracteristicasFisicoQuimicas(PackageBuilder pb) {
                root = rootType.addCampoListaOfComposto("testesCaracteristicasFisicoQuimicas", "caracteristicasFisicoQuimicas");
                root.as(AtrBasic::new).label("Testes Características fisíco-químicas");
                type = root.getTipoElementos();
                type.as(AtrBasic::new).label("Características fisíco-químicas");

                estadoFisico = createEstadoFísicoField();
                aspecto = createAspectoField();
                cor = createCorField();
                odor = createOdorField();

                root.withView(new MListMasterDetailView()
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

                observacoes = type.addCampoString("observacoes");
                observacoes.withTextAreaView().as(AtrBasic::new).label("Observações");
            }

            private STypeDecimal createDecimalField(String fieldname, String label, String subtitle, int colPreference) {
                STypeDecimal f = type.addCampoDecimal(fieldname);
                f.as(AtrBasic::new).label(label).subtitle(subtitle)
                        .as(AtrBootstrap::new).colPreference(colPreference);
                return f;
            }

            private STypeString createEstadoFísicoField() {
                STypeString f = type.addCampoString("estadoFisico", true);
                f.withSelectionOf("Líquido", "Sólido", "Gasoso")
                        .withView(MSelecaoPorSelectView::new)
                        .as(AtrBasic::new).label("Estado físico")
                        .as(AtrBootstrap::new).colPreference(2);
                return f;
            }

            private STypeString createAspectoField() {
                STypeString f = type.addCampoString("aspecto", true);
                f.as(AtrBasic::new).label("Aspecto")
                        .tamanhoMaximo(50)
                        .as(AtrBootstrap::new).colPreference(4);
                return f;
            }

            private STypeString createCorField() {
                STypeString f = type.addCampoString("cor", true);
                f.as(AtrBasic::new).label("Cor")
                        .tamanhoMaximo(40)
                        .as(AtrBootstrap::new).colPreference(3);
                return f;
            }

            private STypeString createOdorField() {
                STypeString f = type.addCampoString("odor");
                f.as(AtrBasic::new).label("Odor")
                        .tamanhoMaximo(40)
                        .as(AtrBootstrap::new).colPreference(3);
                return f;
            }

            private STypeString createHidroliseField() {
                STypeString f = type.addCampoString("hidrolise");
                f.as(AtrBasic::new).label("Hidrólise")
                        .as(AtrBootstrap::new).colPreference(6);
                return f;
            }

            private STypeString createEstabilidadeField() {
                STypeString f = type.addCampoString("estabilidade");
                f.as(AtrBasic::new).label("Estabilidade às temperaturas normal e elevada")
                        .as(AtrBootstrap::new).colPreference(6);
                return f;
            }

            private STypeDecimal createPontoFulgorField() {
                STypeDecimal f = createDecimalField("pontoFulgor", "Ponto de fulgor", "ºC", 3);
                return f;
            }

            private STypeDecimal createConstanteDissociacaoField() {
                STypeDecimal f = type.addCampoDecimal("constanteDissociacao");
                f.as(AtrBasic::new).label("Constante de dissociação")
                        .as(AtrBootstrap::new).colPreference(3);
                return f;
            }

            class Faixa {
                final STypeComposite<SIComposite> root;
                final STypeDecimal pontoFusao, faixaFusaoDe, faixaFusaoA;

                Faixa(PackageBuilder pb, String prefix, String nome) {
                    root = type.addCampoComposto("faixa" + prefix);
                    root.as(AtrBootstrap::new).colPreference(6).as(AtrBasic::new).label(nome);

                    pontoFusao = createDecimalField("ponto" + prefix, "Ponto de " + nome, "ºC");
                    //TODO o campo faixa de fusao precisa de um tipo intervalo
                    // Exemplo: Faixa De 10 a 20
                    faixaFusaoDe = createDecimalField("faixa" + prefix + "De", "Início", "da Faixa");
                    faixaFusaoA = createDecimalField("faixa" + prefix + "A", "Fim", "da Faixa");
                }

                private STypeDecimal createDecimalField(String fieldname, String label, String subtitle) {
                    SType<?> f = root.addCampoDecimal(fieldname);
                    f.as(AtrBasic::new).label(label).subtitle(subtitle)
                            .as(AtrBootstrap::new).colPreference(4);
                    return (STypeDecimal) f;
                }
            }

            class PressaoDeValor {
                final STypeComposite<SIComposite> root;
                final STypeDecimal valor;
                final STypeString unidade;

                PressaoDeValor(PackageBuilder pb) {
                    root = type.addCampoComposto("pressaoVapor");
                    root.as(AtrBasic::new).label("Pressão do vapor")
                            .as(AtrBootstrap::new).colPreference(6);

                    valor = createValorField();
                    unidade = createUnidadeField();

                }

                private STypeDecimal createValorField() {
                    STypeDecimal f = root.addCampoDecimal("valor");
                    f.as(AtrBasic::new).label("Valor")
                            .as(AtrBootstrap::new).colPreference(6);
                    return f;
                }

                private STypeString createUnidadeField() {
                    STypeString f = root.addCampoString("unidade");
                    f.withSelectionOf("mmHg", "Pa", "mPa")
                            .withView(MSelecaoPorSelectView::new)
                            .as(AtrBasic::new).label("Unidade")
                            .as(AtrBootstrap::new).colPreference(6);
                    return f;
                }
            }

            class Solubilidade {
                final STypeComposite<SIComposite> root;
                final STypeDecimal agua, outrosSolventes;

                Solubilidade(PackageBuilder pb) {
                    root = type.addCampoComposto("solubilidade");
                    root.as(AtrBasic::new).label("Solubilidade")
                            .as(AtrBootstrap::new).colPreference(6);

                    agua = createDecimalField("solubilidadeAgua", "em água", "mg/L a 20 ou 25 ºC");
                    outrosSolventes = createDecimalField("solubilidadeOutrosSolventes",
                            "em outros solventes", "mg/L a 20 ou 25 ºC");
                }

                private STypeDecimal createDecimalField(String fieldName, String label, String subtitle) {
                    STypeDecimal f = root.addCampoDecimal(fieldName);
                    f.as(AtrBasic::new).label(label).subtitle(subtitle)
                            .as(AtrBootstrap::new).colPreference(6);
                    return f;
                }
            }

            class PotenciaDeHidrogenio {
                final STypeLista<STypeComposite<SIComposite>, SIComposite> root;
                final STypeComposite<SIComposite> type;
                final STypeDecimal valorPh, solucao, temperatura;

                PotenciaDeHidrogenio(PackageBuilder pb) {
                    root = TesteCaracteristicasFisicoQuimicas.this.type.addCampoListaOfComposto("phs", "ph");
                    root.withView(MPanelListaView::new).as(AtrBasic::new).label("Lista de pH");
                    type = root.getTipoElementos();

                    valorPh = createDecimalField("valorPh", "pH", ".");
                    solucao = createDecimalField("solucao", "Solução", "%");
                    temperatura = createDecimalField("temperatura", "Temperatura", "ºC");
                }

                private STypeDecimal createDecimalField(String fieldname, String label, String subtitle) {
                    STypeDecimal valorPh = type.addCampoDecimal(fieldname, true);
                    valorPh.as(AtrBasic::new).label(label).subtitle(subtitle)
                            .as(AtrBootstrap::new).colPreference(4);
                    return valorPh;
                }
            }
        }

        class TesteIrritacaoOcular {
            final STypeLista<STypeComposite<SIComposite>, SIComposite> root;
            final STypeComposite<SIComposite> type;
            final STypeString laboratorio, protocolo, purezaProdutoTestado, unidadeMedida, especies,
                    linhagem, veiculo, fluoresceina, testeRealizado;
            final STypeData inicio, fim;
            final Alteracao alteracao;

            class Alteracao {
                final STypeComposite<SIComposite> root;
                final STypeString cornea, tempoReversibilidadeCornea, conjuntiva, tempoReversibilidadeConjuntiva,
                        iris, tempoReversibilidadeIris;

                Alteracao(PackageBuilder pb) {
                    root = type.addCampoComposto("alteracoes");
                    root.as(AtrBasic::new).label("Alterações");

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
                    STypeString f = root.addCampoString(fieldname);
                    f.as(AtrBasic::new).label(label)
                            .as(AtrBootstrap::new).colPreference(colPreference);
                    return f;
                }
            }

            private TesteIrritacaoOcular(PackageBuilder pb) {
                //TODO criar regra para pelo menos um campo preenchido
                root = rootType.addCampoListaOfComposto("testesIrritacaoOcular", "irritacaoOcular");
                root.as(AtrBasic::new).label("Testes Irritação / Corrosão ocular");
                type = root.getTipoElementos();
                type.as(AtrBasic::new).label("Irritação / Corrosão ocular")
                        .as(AtrBootstrap::new).colPreference(4);

                laboratorio = createStringField("laboratorio", "Laboratório", 50, 12);
                protocolo = createStringField("protocoloReferencia", "Protocolo de referência", 50, 12);
                inicio = createDateField("dataInicioEstudo", "Data de início do estudo", 3);
                fim = createDateField("dataFimEstudo", "Data final do estudo", 3);

                root.withView(new MListMasterDetailView()
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

                type.addCampoDecimal("numeroAnimais")
                        .as(AtrBasic::new).label("Número de animais")
                        .as(AtrBootstrap::new).colPreference(3);

                veiculo = createStringField("veiculo", "Veículo", null, 3);

                fluoresceina = createStringField("fluoresceina", "Fluoresceína", null, 3);
                fluoresceina.withView(new MSelecaoPorRadioView());
                fluoresceina.withSelectionOf("Sim", "Não");

                testeRealizado = createStringField("testeRealizado", "Teste realizado", null, 3);
                testeRealizado.withSelectionOf("Com lavagem", "Sem lavagem");

                alteracao = new Alteracao(pb);

                type.addCampoString("observacoes")
                        .withTextAreaView()
                        .as(AtrBasic::new).label("Observações");
            }

            private STypeString createStringField(String fieldname, String label, Integer maxSize, Integer colPreference) {
                STypeString f = type.addCampoString(fieldname);
                f.as(AtrBasic::new).label(label).tamanhoMaximo(maxSize)
                        .as(AtrBootstrap::new).colPreference(colPreference);
                return f;
            }

            private STypeData createDateField(String fieldName, String label, int colPreference) {
                STypeData f = type.addCampoData(fieldName);
                f.as(AtrBasic::new).label(label).as(AtrBootstrap::new).colPreference(colPreference);
                return f;
            }
        }
    }
}

