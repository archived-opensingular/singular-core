package br.net.mirante.singular.showcase.view.page.peticao;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.*;
import br.net.mirante.singular.form.mform.core.*;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCNPJ;
import br.net.mirante.singular.form.wicket.AtrBootstrap;

public class MPacotePeticaoGGTOX extends MPacote {

    public static final String PACOTE        = "mform.peticao";
    public static final String TIPO          = "PeticionamentoGGTOX";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;
    private DadosResponsavel dadosResponsavel;
    private Componente componentes;


    public MPacotePeticaoGGTOX() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        final MTipoComposto<?> peticionamento = pb.createTipoComposto(TIPO);

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
        public final MTipoComposto<MIComposto> root;
        public final MTipoString responsavelTecnico, representanteLegal, concordo;

        DadosResponsavel(PacoteBuilder pb, MTipoComposto<?> peticionamento) {
            root = peticionamento.addCampoComposto("dadosResponsavel");

            root.as(AtrBasic::new).label("Dados do Responsável");
            responsavelTecnico = createResponsavelTecnicoField();
            representanteLegal = createRepresentanteLegalField();
            concordo = createConcordoField();


        }

        private MTipoString createResponsavelTecnicoField() {
            //TODO Como fazer a seleção para um objeto composto/enum ?
            //TODO a recuperação de valores deve ser dinamica
            MTipoString field = root.addCampoString("responsavelTecnico", true);
            field.withSelectionOf(getResponsaveis())
                    .withView(MSelecaoPorSelectView::new)
                    .as(AtrBasic::new).label("Responsável Técnico")
                    .as(AtrBootstrap::new).colPreference(3);
            return field;
        }

        private MTipoString createRepresentanteLegalField() {
            MTipoString field = root.addCampoString("representanteLegal", true);
            field.withSelectionOf(getResponsaveis())
                    .withView(MSelecaoPorSelectView::new)
                    .as(AtrBasic::new).label("Representante Legal")
                    .as(AtrBootstrap::new).colPreference(3);
            return field;
        }

        private MTipoString createConcordoField() {
            // TODO preciso de um campo boolean mas as labels devem ser as descritas abaixo
            //TODO deve ser possivel alinhar o texto: text-left text-right text-justify text-nowrap
            MTipoString field = root.addCampoString("concordo", true);
            field.withSelectionOf("Concordo", "Não Concordo")
                    .withView(MSelecaoPorRadioView::new);
            return field;
        }

        private String[] getResponsaveis() {
            return new String[] { "Daniel", "Delfino", "Fabrício", "Lucas", "Tetsuo", "Vinícius" };
        }
    }


    class Componente {
        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> root;
        final MTipoComposto<MIComposto> rootType;
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

        Componente(PacoteBuilder pb, MTipoComposto<?> peticionamento) {
            root = peticionamento.addCampoListaOfComposto("componentes", "componente");
            root.as(AtrBasic::new).label("Componente");
            rootType = root.getTipoElementos();
            rootType.as(AtrBasic::new).label("Registro de Componente");

            identificacao = new Identificacao(pb);
            restricao = new Restricao(pb);
            sinonimia = new Sinonimia(pb);
            finalidade = new Finalidade(pb);
            usoPretendido =  new UsoPretendido(pb);
            nomeComercial = new NomeComercial(pb);
            embalagem = new Embalagem(pb);
            anexo = new Anexo(pb);

            caracteristicasFisicoQuimicas = new TesteCaracteristicasFisicoQuimicas(pb);
            irritacaoOcular = new TesteIrritacaoOcular(pb);

            root.withView(new MListMasterDetailView()
                    .col(identificacao.tipoComponente)
                    .col(sinonimia.sugerida)
            );
        }

        class Identificacao {
            final MTipoComposto<MIComposto> root;
            final MTipoString tipoComponente;
            Identificacao(PacoteBuilder pb){
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
            final MTipoComposto<MIComposto> root;
            Restricao(PacoteBuilder pb){
                root = rootType.addCampoComposto("restricoesComponente");
                root.as(AtrBasic::new).label("Restrições")
                        .as(AtrBootstrap::new).colPreference(4);

                //TODO caso eu marque sem restrições os outros campos devem ser desabilitados
                root.addCampoListaOf("restricoes", pb.createTipo("restricao", MTipoString.class)
                        .withSelectionOf("Impureza relevante presente",
                                "Controle de impureza determinado",
                                "Restrição de uso em algum país",
                                "Restrição de uso em alimentos",
                                "Sem restrições"))
                        .withView(MSelecaoMultiplaPorCheckView::new)
                        .as(AtrBasic::new).label("Restrições");

            }
        }

        class Sinonimia {
            final MTipoComposto<MIComposto> root;
            final MTipoLista<MTipoString, MIString> lista;
            final MTipoString sugerida;

            Sinonimia(PacoteBuilder pb){
                root = rootType.addCampoComposto("sinonimiaComponente");
                root.as(AtrBasic::new).label("Sinonímia").as(AtrBootstrap::new).colPreference(4);

                lista = createListaField(pb);
                sugerida = createSugeridaField();

            }

            private MTipoLista<MTipoString, MIString> createListaField(PacoteBuilder pb) {
                MTipoLista<MTipoString, MIString> field = root.addCampoListaOf("sinonimiaAssociada",
                        pb.createTipo("sinonimia", MTipoString.class)
                                .withSelectionOf("Sinonímia teste", "Sinonímia teste 2", "Sinonímia teste 3"));
                field.withView(MSelecaoMultiplaPorSelectView::new)
                        .as(AtrBasic::new)
                        .label("Sinonímias já associadas a esta substância/mistura")
                        .enabled(false);
                return field;
            }

            private MTipoString createSugeridaField() {
                final MTipoLista<MTipoComposto<MIComposto>, MIComposto> sinonimias = root.addCampoListaOfComposto("sinonimias", "sinonimia");
                final MTipoComposto<?> sinonimia = sinonimias.getTipoElementos();
                MTipoString field = sinonimia.addCampoString("nomeSinonimia", true);

                field.as(AtrBasic::new).label("Sinonímia sugerida").tamanhoMaximo(100);

                field.withView(MTableListaView::new)
                        .as(AtrBasic::new)
                        .label("Lista de sinonímias sugeridas para esta substância/mistura");
                return field;
            }
        }

        class Finalidade{
            final MTipoComposto<MIComposto> root;
            Finalidade(PacoteBuilder pb){
                root = rootType.addCampoComposto("finalidadesComponente");

                root.as(AtrBasic::new).label("Finalidades")
                        .as(AtrBootstrap::new).colPreference(4);

                root.addCampoListaOf("finalidades", pb.createTipo("finalidade", MTipoString.class)
                        .withSelectionOf("Produção","Importação","Exportação","Comercialização","Utilização"))
                        .withView(MSelecaoMultiplaPorCheckView::new);
            }
        }

        class UsoPretendido {
            final MTipoComposto<MIComposto> root;
            UsoPretendido(PacoteBuilder pb){
                //TODO falta criar modal para cadastrar novo uso pretendido
                root = rootType.addCampoComposto("usosPretendidosComponente");

                root.as(AtrBasic::new).label("Uso pretendido").as(AtrBootstrap::new).colPreference(4);

                root.addCampoListaOf("usosPretendidos",
                        pb.createTipo("usoPretendido", MTipoString.class)
                                .withSelectionOf("Uso 1","Uso 2","Uso 3"))
                        .withView(MSelecaoMultiplaPorPicklistView::new)
                        .as(AtrBasic::new).label("Lista de uso pretendido/mistura");
            }
        }

        class NomeComercial {
            final MTipoLista<MTipoComposto<MIComposto>, MIComposto> root;
            final MTipoComposto<MIComposto> type;
            final MTipoString nome;
            final Fabricante fabricante;

            NomeComercial(PacoteBuilder pb){
                root = rootType.addCampoListaOfComposto("nomesComerciais", "nomeComercial");
                root.withView(MPanelListaView::new).as(AtrBasic::new).label("Nome comercial");

                type = root.getTipoElementos();

                nome = type.addCampoString("nome", true);
                nome.as(AtrBasic::new).label("Nome comercial").tamanhoMaximo(80);

                fabricante = new Fabricante(pb);
            }

            class Fabricante{
                final MTipoLista<MTipoComposto<MIComposto>, MIComposto> root;
                Fabricante(PacoteBuilder pb){
                    root = type.addCampoListaOfComposto("fabricantes", "fabricante");

                    //TODO Fabricante deve ser uma pesquisa
                    MTipoComposto<MIComposto> fabricante = root.getTipoElementos();
                    //TODO como usar o tipo cnpj
                    fabricante.addCampo("cnpj", MTipoCNPJ.class).as(AtrBasic::new).label("CNPJ").as(AtrBootstrap::new).colPreference(4);
                    fabricante.addCampoString("razaoSocial").as(AtrBasic::new).label("Razão social").as(AtrBootstrap::new).colPreference(4);
                    fabricante.addCampoString("cidade").as(AtrBasic::new).label("Cidade").as(AtrBootstrap::new).colPreference(2);
                    fabricante.addCampoString("pais").as(AtrBasic::new).label("País").as(AtrBootstrap::new).colPreference(2);

                    root.withView(MListMasterDetailView::new)
                            .as(AtrBasic::new).label("Fabricante(s)");
                }
            }
        }

        class Embalagem {
            final MTipoLista<MTipoComposto<MIComposto>, MIComposto> root;
            final MTipoComposto<MIComposto> type;
            final MTipoString produtoExterior;
            final MTipoString tipo;
            MTipoString material;
            final MTipoString unidadeMedida;
            final MTipoInteger capacidade;
            private final String[]
                    tiposDisponiveis = new String[]{
                    "Balde", "Barrica", "Bombona", "Caixa", "Carro tanque", "Cilindro",
                    "Container", "Frasco", "Galão", "Garrafa", "Lata", "Saco", "Tambor"
            },
                    materiaisDisponiveis = new String[]{"Papel", "Alumínio", "Ferro", "Madeira"
                    };
            Embalagem(PacoteBuilder pb){
                root = rootType.addCampoListaOfComposto("embalagens", "embalagem");
                root.withView(MTableListaView::new).as(AtrBasic::new).label("Embalagem");
                type = root.getTipoElementos();
                produtoExterior = createFieldProdutoExterior();
                tipo = createFieldTipo();
                material = createFieldMaterial();
                capacidade = createFieldCapacidade();
                unidadeMedida = createFieldUnidadeDeMedida();
            }

            private MTipoString createFieldProdutoExterior() {
                //TODO converter sim nao para true false
                MTipoString field = type.addCampoString("produtoExterior", true);
                field.withSelectionOf("Sim", "Não")
                        .withView(MSelecaoPorRadioView::new)
                        .as(AtrBasic::new).label("Produto formulado no exterior?")
                        .as(AtrBootstrap::new).colPreference(12);
                return field;
            }

            private MTipoString createFieldTipo() {
                MTipoString field = type.addCampoString("tipo", true);
                field.withSelectionOf(tiposDisponiveis)
                        .withView(MSelecaoPorSelectView::new)
                        .as(AtrBasic::new).label("Tipo")
                        .as(AtrBootstrap::new).colPreference(4);
                return field;
            }

            private MTipoString createFieldMaterial() {
                MTipoString field = type.addCampoString("material", true);
                field.withSelectionOf(materiaisDisponiveis)
                        .withView(MSelecaoPorSelectView::new)
                        .as(AtrBasic::new).label("Material")
                        .as(AtrBootstrap::new).colPreference(4);
                return field;
            }

            private MTipoInteger createFieldCapacidade() {
                MTipoInteger field = type.addCampoInteger("capacidade", true);
                field.as(AtrBasic::new).label("Capacidade")
                        .tamanhoMaximo(15)
                        .as(AtrBootstrap::new).colPreference(4);
                return field;
            }

            private MTipoString createFieldUnidadeDeMedida() {
                //TODO caso o array tenha uma string vazia, ocorre um NPE
                MTipoString field = type.addCampoString("unidadeMedida", true);
                field.withSelectionOf(new String[]{"cm"}).withView(MSelecaoPorSelectView::new)
                        .as(AtrBasic::new).label("Unidade medida")
                        .as(AtrBootstrap::new).colPreference(1);
                return field;
            }

        }

        class Anexo {
            final MTipoLista<MTipoComposto<MIComposto>, MIComposto> root;
            final MTipoComposto<MIComposto> type;
            final MTipoAttachment arquivo;
            final MTipoString tipo;
            Anexo(PacoteBuilder pb){
                root = rootType.addCampoListaOfComposto("anexos", "anexo");
                root.as(AtrBasic::new).label("Anexos");
                type = root.getTipoElementos();

                arquivo = createArquivoField();
                tipo = createTipoField();

                MTipo<?> nomeArquivo = (MTipoSimples) arquivo.getCampo(arquivo.FIELD_NAME);
                nomeArquivo.as(AtrBasic::new).label("Nome do Arquivo");
                root.withView(new MListMasterDetailView()
                        .col((MTipoSimples) nomeArquivo)
                        .col(tipo)
                );
            }

            private MTipoAttachment createArquivoField() {
                MTipoAttachment f = type.addCampo("arquivo", MTipoAttachment.class);
                f.as(AtrBasic.class).label("Informe o caminho do arquivo para o anexo")
                        .as(AtrBootstrap::new).colPreference(3);
                return f;
            }

            private MTipoString createTipoField() {
                MTipoString t = type.addCampoString("tipoArquivo");
                t.withSelectionOf("Ficha de emergência", "Ficha de segurança", "Outros")
                        .withView(MSelecaoPorSelectView::new)
                        .as(AtrBasic::new).label("Tipo do arquivo a ser anexado")
                        .as(AtrBootstrap::new).colPreference(3);
                return t;
            }
        }

        class TesteCaracteristicasFisicoQuimicas {
            final MTipoLista<MTipoComposto<MIComposto>, MIComposto> root;
            final MTipoComposto<MIComposto> type;
            final MTipoString estadoFisico,aspecto,cor,odor, hidrolise, estabilidade, observacoes;
            final MTipoDecimal pontoFulgor,constanteDissociacao, coeficienteParticao, densidade;
            final Faixa fusao, ebulicao;
            final PressaoDeValor pressaoDeVapor;
            final Solubilidade solubilidade;
            final PotenciaDeHidrogenio ph;

            private TesteCaracteristicasFisicoQuimicas(PacoteBuilder pb) {
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

                fusao = new Faixa(pb,"Fusao","Fusão");
                ebulicao = new Faixa(pb,"Ebulicao","Ebulição");
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
                observacoes.withView(MTextAreaView::new).as(AtrBasic::new).label("Observações");
            }

            private MTipoDecimal createDecimalField(String fieldname, String label, String subtitle, int colPreference) {
                MTipoDecimal f = type.addCampoDecimal(fieldname);
                f.as(AtrBasic::new).label(label).subtitle(subtitle)
                        .as(AtrBootstrap::new).colPreference(colPreference);
                return f;
            }

            private MTipoString createEstadoFísicoField() {
                MTipoString f = type.addCampoString("estadoFisico", true);
                f.withSelectionOf("Líquido", "Sólido", "Gasoso")
                        .withView(MSelecaoPorSelectView::new)
                        .as(AtrBasic::new).label("Estado físico")
                        .as(AtrBootstrap::new).colPreference(2);
                return f;
            }

            private MTipoString createAspectoField() {
                MTipoString f = type.addCampoString("aspecto", true);
                f.as(AtrBasic::new).label("Aspecto")
                        .tamanhoMaximo(50)
                        .as(AtrBootstrap::new).colPreference(4);
                return f;
            }

            private MTipoString createCorField() {
                MTipoString f = type.addCampoString("cor", true);
                f.as(AtrBasic::new).label("Cor")
                        .tamanhoMaximo(40)
                        .as(AtrBootstrap::new).colPreference(3);
                return f;
            }

            private MTipoString createOdorField() {
                MTipoString f = type.addCampoString("odor");
                f.as(AtrBasic::new).label("Odor")
                        .tamanhoMaximo(40)
                        .as(AtrBootstrap::new).colPreference(3);
                return f;
            }

            private MTipoString createHidroliseField() {
                MTipoString f = type.addCampoString("hidrolise");
                f.as(AtrBasic::new).label("Hidrólise")
                        .as(AtrBootstrap::new).colPreference(6);
                return f;
            }

            private MTipoString createEstabilidadeField() {
                MTipoString f = type.addCampoString("estabilidade");
                f.as(AtrBasic::new).label("Estabilidade às temperaturas normal e elevada")
                        .as(AtrBootstrap::new).colPreference(6);
                return f;
            }

            private MTipoDecimal createPontoFulgorField() {
                MTipoDecimal f = createDecimalField("pontoFulgor", "Ponto de fulgor", "ºC", 3);
                return f;
            }

            private MTipoDecimal createConstanteDissociacaoField() {
                MTipoDecimal f = type.addCampoDecimal("constanteDissociacao");
                f.as(AtrBasic::new).label("Constante de dissociação")
                        .as(AtrBootstrap::new).colPreference(3);
                return f;
            }

            class Faixa {
                final MTipoComposto<MIComposto> root;
                final MTipoDecimal pontoFusao, faixaFusaoDe, faixaFusaoA;
                Faixa(PacoteBuilder pb, String prefix, String nome){
                    root = type.addCampoComposto("faixa"+prefix);
                    root.as(AtrBootstrap::new).colPreference(6).as(AtrBasic::new).label(nome);

                    pontoFusao = createDecimalField("ponto"+prefix, "Ponto de "+nome, "ºC");
                    //TODO o campo faixa de fusao precisa de um tipo intervalo
                    // Exemplo: Faixa De 10 a 20
                    faixaFusaoDe = createDecimalField("faixa"+prefix+"De", "Início", "da Faixa");
                    faixaFusaoA = createDecimalField("faixa"+prefix+"A", "Fim", "da Faixa");
                }

                private MTipoDecimal createDecimalField(String fieldname, String label, String subtitle) {
                    MTipo<?> f = root.addCampoDecimal(fieldname);
                    f.as(AtrBasic::new).label(label).subtitle(subtitle)
                            .as(AtrBootstrap::new).colPreference(4);
                    return (MTipoDecimal) f;
                }
            }

            class PressaoDeValor {
                final MTipoComposto<MIComposto> root;
                final MTipoDecimal valor;
                final MTipoString unidade;
                PressaoDeValor(PacoteBuilder pb){
                    root = type.addCampoComposto("pressaoVapor");
                    root.as(AtrBasic::new).label("Pressão do vapor")
                            .as(AtrBootstrap::new).colPreference(6);

                    valor = createValorField();
                    unidade = createUnidadeField();

                }

                private MTipoDecimal createValorField() {
                    MTipoDecimal f = root.addCampoDecimal("valor");
                    f.as(AtrBasic::new).label("Valor")
                            .as(AtrBootstrap::new).colPreference(6);
                    return f;
                }

                private MTipoString createUnidadeField() {
                    MTipoString f = root.addCampoString("unidade");
                    f.withSelectionOf("mmHg", "Pa", "mPa")
                            .withView(MSelecaoPorSelectView::new)
                            .as(AtrBasic::new).label("Unidade")
                            .as(AtrBootstrap::new).colPreference(6);
                    return f;
                }
            }

            class Solubilidade {
                final MTipoComposto<MIComposto> root;
                final MTipoDecimal agua, outrosSolventes;

                Solubilidade (PacoteBuilder pb){
                    root = type.addCampoComposto("solubilidade");
                    root.as(AtrBasic::new).label("Solubilidade")
                            .as(AtrBootstrap::new).colPreference(6);

                    agua = createDecimalField("solubilidadeAgua", "em água", "mg/L a 20 ou 25 ºC");
                    outrosSolventes = createDecimalField("solubilidadeOutrosSolventes",
                            "em outros solventes", "mg/L a 20 ou 25 ºC");
                }

                private MTipoDecimal createDecimalField(String fieldName, String label, String subtitle) {
                    MTipoDecimal f = root.addCampoDecimal(fieldName);
                    f.as(AtrBasic::new).label(label).subtitle(subtitle)
                            .as(AtrBootstrap::new).colPreference(6);
                    return f;
                }
            }

            class PotenciaDeHidrogenio {
                final MTipoLista<MTipoComposto<MIComposto>, MIComposto> root;
                final MTipoComposto<MIComposto> type;
                final MTipoDecimal valorPh, solucao, temperatura;

                PotenciaDeHidrogenio(PacoteBuilder pb){
                    root = TesteCaracteristicasFisicoQuimicas.this.type.addCampoListaOfComposto("phs", "ph");
                    root.withView(MPanelListaView::new).as(AtrBasic::new).label("Lista de pH");
                    type = root.getTipoElementos();

                    valorPh = createDecimalField("valorPh", "pH", ".");
                    solucao = createDecimalField("solucao", "Solução", "%");
                    temperatura = createDecimalField("temperatura", "Temperatura", "ºC");
                }

                private MTipoDecimal createDecimalField(String fieldname, String label, String subtitle) {
                    MTipoDecimal valorPh = type.addCampoDecimal(fieldname, true);
                    valorPh.as(AtrBasic::new).label(label).subtitle(subtitle)
                            .as(AtrBootstrap::new).colPreference(4);
                    return valorPh;
                }
            }
        }

        class TesteIrritacaoOcular{
            private TesteIrritacaoOcular(PacoteBuilder pb) {

                final MTipoLista<MTipoComposto<MIComposto>, MIComposto> testes = rootType.addCampoListaOfComposto("testesIrritacaoOcular", "irritacaoOcular");
                MTipoComposto<MIComposto> teste = testes.getTipoElementos();

                //TODO criar regra para pelo menos um campo preenchido

                testes
//            .withView(MPanelListaView::new)
                        .as(AtrBasic::new).label("Testes Irritação / Corrosão ocular");

                teste.as(AtrBasic::new).label("Irritação / Corrosão ocular")
                        .as(AtrBootstrap::new).colPreference(4);

                MTipoString laboratorio = teste.addCampoString("laboratorio");
                laboratorio.as(AtrBasic::new).label("Laboratório")
                        .tamanhoMaximo(50);

                MTipoString protocolo = teste.addCampoString("protocoloReferencia");
                protocolo.as(AtrBasic::new).label("Protocolo de referência")
                        .tamanhoMaximo(50);

                MTipoData inicio = teste.addCampoData("dataInicioEstudo");
                inicio.as(AtrBasic::new).label("Data de início do estudo")
                        .as(AtrBootstrap::new).colPreference(3);

                MTipoData fim = teste.addCampoData("dataFimEstudo");
                fim.as(AtrBasic::new).label("Data final do estudo")
                        .as(AtrBootstrap::new).colPreference(3);

                testes.withView(new MListMasterDetailView()
                        .col(laboratorio)
                        .col(protocolo)
                        .col(inicio)
                        .col(fim)
                );

                teste.addCampoString("purezaProdutoTestado")
                        .as(AtrBasic::new).label("Pureza do produto testado")
                        .as(AtrBootstrap::new).colPreference(6);

                teste.addCampoString("unidadeMedida")
                        .withSelectionOf("g/Kg", "g/L")
                        .as(AtrBasic::new).label("Unidade de medida")
                        .as(AtrBootstrap::new).colPreference(2);

                teste.addCampoString("especies")
                        .withSelectionOf("Càes",
                                "Camundongos",
                                "Cobaia",
                                "Coelho",
                                "Galinha",
                                "Informação não disponível",
                                "Peixe",
                                "Primatas",
                                "Rato")
                        .as(AtrBasic::new).label("Espécies")
                        .as(AtrBootstrap::new).colPreference(4);

                teste.addCampoString("linhagem")
                        .as(AtrBasic::new).label("Linhagem")
                        .as(AtrBootstrap::new).colPreference(6);

                teste.addCampoDecimal("numeroAnimais")
                        .as(AtrBasic::new).label("Número de animais")
                        .as(AtrBootstrap::new).colPreference(3);

                teste.addCampoString("veiculo")
                        .as(AtrBasic::new).label("Veículo")
                        .as(AtrBootstrap::new).colPreference(3);

                teste.addCampoString("fluoresceina")
                        .withSelectionOf("Sim", "Não")
                        .as(AtrBasic::new).label("Fluoresceína")
                        .as(AtrBootstrap::new).colPreference(3);

                teste.addCampoString("testeRealizado")
                        .withSelectionOf("Com lavagem", "Sem lavagem")
                        .as(AtrBasic::new).label("Teste realizado")
                        .as(AtrBootstrap::new).colPreference(3);

                MTipoComposto<MIComposto> alteracoes = teste.addCampoComposto("alteracoes");

                alteracoes.as(AtrBasic::new).label("Alterações")
                        .as(AtrBootstrap::new);

                alteracoes.addCampoString("cornea")
                        .withSelectionOf("Sem alterações", "Opacidade persistente", "Opacidade reversível em...")
                        .as(AtrBasic::new).label("Córnea")
                        .as(AtrBootstrap::new).colPreference(6);

                alteracoes.addCampoString("tempoReversibilidadeCornea")
                        .as(AtrBasic::new).label("Tempo de reversibilidade")
                        .as(AtrBootstrap::new).colPreference(6);

                alteracoes.addCampoString("conjuntiva")
                        .withSelectionOf("Sem alterações", "Opacidade persistente", "Opacidade reversível em...")
                        .as(AtrBasic::new).label("Conjuntiva")
                        .as(AtrBootstrap::new).colPreference(6);

                alteracoes.addCampoString("tempoReversibilidadeConjuntiva")
                        .as(AtrBasic::new).label("Tempo de reversibilidade")
                        .as(AtrBootstrap::new).colPreference(6);

                alteracoes.addCampoString("iris")
                        .withSelectionOf("Sem alterações", "Opacidade persistente", "Opacidade reversível em...")
                        .as(AtrBasic::new).label("Íris")
                        .as(AtrBootstrap::new).colPreference(6);

                alteracoes.addCampoString("tempoReversibilidadeIris")
                        .as(AtrBasic::new).label("Tempo de reversibilidade")
                        .as(AtrBootstrap::new).colPreference(6);

                teste.addCampoString("observacoes")
                        .withView(MTextAreaView::new)
                        .as(AtrBasic::new).label("Observações");

            }
        }
    }
}

