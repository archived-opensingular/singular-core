package org.opensingular.singular.form.showcase.view.page.form.examples;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.STypeAttachment;
import org.opensingular.form.type.country.brazil.STypeCNPJ;
import org.opensingular.form.view.SMultiSelectionByCheckboxView;
import org.opensingular.form.view.SMultiSelectionByPicklistView;
import org.opensingular.form.view.SMultiSelectionBySelectView;
import org.opensingular.form.view.SViewAutoComplete;
import org.opensingular.form.view.SViewListByForm;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.form.view.SViewListByTable;
import org.opensingular.form.view.SViewSelectionByRadio;
import org.opensingular.form.view.SViewSelectionBySelect;
import org.opensingular.form.view.SViewTab;

@SInfoType(spackage = SPackagePeticaoGGTOX.class, name = "STypePeticaoGGTOX")
public class STypePeticaoGGTOX extends STypeComposite<SIComposite> {

    private DadosResponsavel dadosResponsavel;
    private Componente       componentes;

    @Override
    protected void onLoadType(TypeBuilder tb) {

        asAtr().label("Petição GGTOX");

        //TODO deveria ser possivel passar uma coleção para o withSelectionOf

        //TODO solicitar criacao de validacao para esse exemplo:
        /*
        MTipoComposto<MIComposto> sinonimiaComponente = addCampoComposto("sinonimiaComponente");

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

        dadosResponsavel = new DadosResponsavel();
        componentes = new Componente();
        STypeString choice = addFieldString("choice");
        choice.asAtr().label("Escolha um número");
        choice.withView(SViewAutoComplete::new);
        choice.selectionOf("One", "Two", "Three", "Four", "Five", "Six", "Seven");

        SType<?> forshow = addFieldString("forshow").asAtr().label("Just here").getTipo();

        SViewTab tabbed = new SViewTab();
        tabbed.addTab("tudo", "Tudo").add(dadosResponsavel.root).add(componentes.root)
                .add(choice).add(forshow);
        tabbed.addTab(dadosResponsavel.root);
        tabbed.addTab(componentes.root);
        withView(tabbed);

    }

    class DadosResponsavel {
        final String[] responsaveis = new String[]{"Daniel", "Delfino", "Fabrício", "Lucas", "Tetsuo", "Vinícius"};

        final STypeComposite<SIComposite> root;
        final STypeString                 responsavelTecnico, representanteLegal, concordo;

        DadosResponsavel() {
            root = addFieldComposite("dadosResponsavel");

            root.asAtr().label("Dados do Responsável");
            //TODO Como fazer a seleção para um objeto composto/enum ?
            //TODO a recuperação de valores deve ser dinamica

            responsavelTecnico = addPersonField("responsavelTecnico", "Responsável Técnico", 3);
            representanteLegal = addPersonField("representanteLegal", "Representante Legal", 3);
            concordo = createConcordoField();
        }

        private STypeString addPersonField(String fieldname, String label, int colPreference) {
            STypeString f = root.addFieldString(fieldname, true);
            f.selectionOf(responsaveis)
                    .withView(SViewSelectionBySelect::new)
                    .asAtr().label(label)
                    .asAtrBootstrap().colPreference(colPreference);
            return f;
        }

        private STypeString createConcordoField() {
            // TODO preciso de um campo boolean mas as labels devem ser as descritas abaixo
            //TODO deve ser possivel alinhar o texto: text-left text-right text-justify text-nowrap
            STypeString field = root.addFieldString("concordo", true);
            field.selectionOf("Concordo", "Não Concordo").withView(SViewSelectionByRadio::new);
            return field;
        }

    }


    class Componente {
        final STypeList<STypeComposite<SIComposite>, SIComposite> root;
        final STypeComposite<SIComposite>                         rootType;
        final Identificacao                                       identificacao;
        final Restricao                                           restricao;
        final Sinonimia                                           sinonimia;
        final Finalidade                                          finalidade;
        final UsoPretendido                                       usoPretendido;
        final NomeComercial                                       nomeComercial;
        final Embalagem                                           embalagem;
        final Anexo                                               anexo;
        final TesteCaracteristicasFisicoQuimicas                  caracteristicasFisicoQuimicas;
        final TesteIrritacaoOcular                                irritacaoOcular;

        Componente() {
            root = addFieldListOfComposite("componentes", "componente");
            root.asAtr().label("Componente");
            rootType = root.getElementsType();
            rootType.asAtr().label("Registro de Componente");

            identificacao = new Identificacao();
            restricao = new Restricao();
            sinonimia = new Sinonimia();
            finalidade = new Finalidade();
            usoPretendido = new UsoPretendido();
            nomeComercial = new NomeComercial();
            embalagem = new Embalagem();
            anexo = new Anexo();

            caracteristicasFisicoQuimicas = new TesteCaracteristicasFisicoQuimicas();
            irritacaoOcular = new TesteIrritacaoOcular();

            root.withView(new SViewListByMasterDetail()
                            .col(identificacao.tipoComponente)
//                    .col(sinonimia.sugerida)
            );
        }

        class Identificacao {
            final STypeComposite<SIComposite> root;
            final STypeString                 tipoComponente;

            Identificacao() {
                root = rootType.addFieldComposite("identificacaoComponente");
                root.asAtr().label("Identificação de Componente")
                        .asAtrBootstrap().colPreference(4);

                tipoComponente = root.addFieldString("tipoComponente", true);
                tipoComponente.selectionOf("Substância", "Mistura")
                        .withView(SViewSelectionByRadio::new)
                        .asAtr().label("Tipo componente");
            }
        }

        class Restricao {
            final STypeComposite<SIComposite> root;

            Restricao() {
                root = rootType.addFieldComposite("restricoesComponente");
                root.asAtr().label("Restrições")
                        .asAtrBootstrap().colPreference(4);

                //TODO caso eu marque sem restrições os outros campos devem ser desabilitados
                STypeString restricao = addField("restricao", STypeString.class);

                final STypeList<STypeString, SIString> restricoes = root.addFieldListOf("restricoes", restricao);

                restricoes.selectionOf("Impureza relevante presente",
                        "Controle de impureza determinado",
                        "Restrição de uso em algum país",
                        "Restrição de uso em alimentos",
                        "Sem restrições")
                        .withView(SMultiSelectionByCheckboxView::new)
                        .asAtr().label("Restrições");

            }
        }

        class Sinonimia {
            final STypeComposite<SIComposite>      root;
            final STypeList<STypeString, SIString> lista;
            final STypeString                      sugerida;

            Sinonimia() {
                root = rootType.addFieldComposite("sinonimiaComponente");
                root.asAtr().label("Sinonímia").asAtrBootstrap().colPreference(4);

                lista = createListaField();
                sugerida = createSugeridaField();

            }

            private STypeList<STypeString, SIString> createListaField() {
                STypeString                      sinonimia = addField("sinonimia", STypeString.class);
                STypeList<STypeString, SIString> field     = root.addFieldListOf("sinonimiaAssociada", sinonimia);
                field.selectionOf("Sinonímia teste", "Sinonímia teste 2", "Sinonímia teste 3");
                field.withView(SMultiSelectionBySelectView::new)
                        .asAtr()
                        .label("Sinonímias já associadas a esta substância/mistura")
                        .enabled(false);
                return field;
            }

            private STypeString createSugeridaField() {
                final STypeList<STypeComposite<SIComposite>, SIComposite> sinonimias = root.addFieldListOfComposite("sinonimias", "sinonimia");
                final STypeComposite<?>                                   sinonimia  = sinonimias.getElementsType();

                sinonimias.withView(SViewListByTable::new);
                sinonimias.asAtr()
                        .label("Lista de sinonímias sugeridas para esta substância/mistura");

                STypeString field = sinonimia.addFieldString("nomeSinonimia", true);

                field.asAtr().label("Sinonímia sugerida").maxLength(100);

                return field;
            }
        }

        class Finalidade {
            final STypeComposite<SIComposite> root;

            Finalidade() {
                root = rootType.addFieldComposite("finalidadesComponente");

                root.asAtr().label("Finalidades")
                        .asAtrBootstrap().colPreference(4);

                STypeString                            finalidade  = addField("finalidadeConformeMatriz", STypeString.class);
                final STypeList<STypeString, SIString> finalidades = root.addFieldListOf("finalidades", finalidade);
                finalidades.selectionOf("Produção", "Importação", "Exportação", "Comercialização", "Utilização").cast();
                finalidades.withView(SMultiSelectionByCheckboxView::new);
            }
        }

        class UsoPretendido {
            final STypeComposite<SIComposite> root;

            UsoPretendido() {
                //TODO falta criar modal para cadastrar novo uso pretendido
                root = rootType.addFieldComposite("usosPretendidosComponente");

                root.asAtr().label("Uso pretendido").asAtrBootstrap().colPreference(4);

                final STypeString                      usoPretendido   = addField("usoPretendido", STypeString.class);
                final STypeList<STypeString, SIString> usosPretendidos = root.addFieldListOf("usosPretendidos", usoPretendido);
                usosPretendidos.selectionOf("Uso 1", "Uso 2", "Uso 3");
                usosPretendidos.withView(SMultiSelectionByPicklistView::new)
                        .asAtr().label("Lista de uso pretendido/mistura");
            }
        }

        class NomeComercial {
            final STypeList<STypeComposite<SIComposite>, SIComposite> root;
            final STypeComposite<SIComposite>                         type;
            final STypeString                                         nome;
            final Fabricante                                          fabricante;

            NomeComercial() {
                root = rootType.addFieldListOfComposite("nomesComerciais", "nomeComercial");
                root.withView(SViewListByForm::new).asAtr().label("Nome comercial");
                type = root.getElementsType();

                nome = type.addFieldString("nome", true);
                nome.asAtr().label("Nome comercial").maxLength(80);

                fabricante = new Fabricante();
            }

            class Fabricante {
                final STypeList<STypeComposite<SIComposite>, SIComposite> root;
                final STypeComposite<SIComposite>                         type;
                final STypeCNPJ                                           cnpj;
                final STypeString                                         razaoSocial, cidade, pais;

                Fabricante() {
                    root = NomeComercial.this.type.addFieldListOfComposite("fabricantes", "fabricante");
                    root.withView(SViewListByMasterDetail::new).asAtr().label("Fabricante(s)");

                    //TODO Fabricante deve ser uma pesquisa
                    type = root.getElementsType();
                    //TODO como usar o tipo cnpj
                    cnpj = type.addField("cnpj", STypeCNPJ.class);
                    cnpj.asAtr().label("CNPJ").asAtrBootstrap().colPreference(4);
                    razaoSocial = createStringField(type, "razaoSocial", "Razão social", 4);
                    cidade = createStringField(type, "cidade", "Cidade", 2);
                    pais = createStringField(type, "pais", "País", 2);

                }

                private STypeString createStringField(STypeComposite<SIComposite> fabricante, String fieldname, String label, int colPreference) {
                    STypeString f = fabricante.addFieldString(fieldname);
                    f.asAtr().label(label).asAtrBootstrap().colPreference(colPreference);
                    return f;
                }
            }
        }

        class Embalagem {
            final STypeList<STypeComposite<SIComposite>, SIComposite> root;
            final STypeComposite<SIComposite>                         type;
            final STypeString                                         produtoExterior;
            final STypeString                                         tipo;
            STypeString material;
            final STypeString  unidadeMedida;
            final STypeInteger capacidade;
            private final String[]
                    tiposDisponiveis     = new String[]{
                    "Balde", "Barrica", "Bombona", "Caixa", "Carro tanque", "Cilindro",
                    "Container", "Frasco", "Galão", "Garrafa", "Lata", "Saco", "Tambor"
            },
                    materiaisDisponiveis = new String[]{"Papel", "Alumínio", "Ferro", "Madeira"
                    };

            Embalagem() {
                root = rootType.addFieldListOfComposite("embalagens", "embalagem");
                root.withView(SViewListByTable::new).asAtr().label("Embalagem");
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
                field.selectionOf("Sim", "Não")
                        .withView(SViewSelectionByRadio::new)
                        .asAtr().label("Produto formulado no exterior?")
                        .asAtrBootstrap().colPreference(12);
                return field;
            }

            private STypeString createFieldTipo() {
                STypeString field = type.addFieldString("tipo", true);
                field.selectionOf(tiposDisponiveis)
                        .withView(SViewSelectionBySelect::new)
                        .asAtr().label("Tipo")
                        .asAtrBootstrap().colPreference(4);
                return field;
            }

            private STypeString createFieldMaterial() {
                STypeString field = type.addFieldString("material", true);
                field.selectionOf(materiaisDisponiveis)
                        .withView(SViewSelectionBySelect::new)
                        .asAtr().label("Material")
                        .asAtrBootstrap().colPreference(4);
                return field;
            }

            private STypeInteger createFieldCapacidade() {
                STypeInteger field = type.addFieldInteger("capacidade", true);
                field.asAtr().label("Capacidade")
                        .maxLength(15)
                        .asAtrBootstrap().colPreference(4);
                return field;
            }

            private STypeString createFieldUnidadeDeMedida() {
                //TODO caso o array tenha uma string vazia, ocorre um NPE
                STypeString field = type.addFieldString("unidadeMedida", true);
                field.selectionOf(new String[]{"cm"}).withView(SViewSelectionBySelect::new)
                        .asAtr().label("Unidade medida")
                        .asAtrBootstrap().colPreference(1);
                return field;
            }

        }

        class Anexo {
            final STypeList<STypeComposite<SIComposite>, SIComposite> root;
            final STypeComposite<SIComposite>                         type;
            final STypeAttachment                                     arquivo;
            final STypeString                                         tipo;

            Anexo() {
                root = rootType.addFieldListOfComposite("anexos", "anexo");
                root.asAtr().label("Anexos");
                type = root.getElementsType();

                arquivo = createArquivoField();
                tipo = createTipoField();

                SType<?> nomeArquivo = (STypeSimple) arquivo.getField(arquivo.FIELD_NAME);
                nomeArquivo.asAtr().label("Nome do Arquivo");
                root.withView(new SViewListByMasterDetail()
                                .col((STypeSimple) nomeArquivo)
                                .col(tipo)
                );
            }

            private STypeAttachment createArquivoField() {
                STypeAttachment f = type.addField("arquivo", STypeAttachment.class);
                f.asAtr().label("Informe o caminho do arquivo para o anexo")
                        .asAtrBootstrap().colPreference(9);
                return f;
            }

            private STypeString createTipoField() {
                STypeString t = type.addFieldString("tipoArquivo");
                t.selectionOf("Ficha de emergência", "Ficha de segurança", "Outros")
                        .withView(SViewSelectionBySelect::new)
                        .asAtr().label("Tipo do arquivo a ser anexado")
                        .asAtrBootstrap().colPreference(3);
                return t;
            }
        }

        class TesteCaracteristicasFisicoQuimicas {
            final STypeList<STypeComposite<SIComposite>, SIComposite> root;
            final STypeComposite<SIComposite>                         type;
            final STypeString                                         estadoFisico, aspecto, cor, odor, hidrolise, estabilidade, observacoes;
            final STypeDecimal pontoFulgor, constanteDissociacao, coeficienteParticao, densidade;
            final Faixa fusao, ebulicao;
            final PressaoDeValor       pressaoDeVapor;
            final Solubilidade         solubilidade;
            final PotenciaDeHidrogenio ph;

            private TesteCaracteristicasFisicoQuimicas() {
                root = rootType.addFieldListOfComposite("testesCaracteristicasFisicoQuimicas", "caracteristicasFisicoQuimicas");
                root.asAtr().label("Testes Características fisíco-químicas");
                type = root.getElementsType();
                type.asAtr().label("Características fisíco-químicas");

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

                fusao = new Faixa("Fusao", "Fusão");
                ebulicao = new Faixa("Ebulicao", "Ebulição");
                pressaoDeVapor = new PressaoDeValor();
                solubilidade = new Solubilidade();

                hidrolise = createHidroliseField();
                estabilidade = createEstabilidadeField();
                pontoFulgor = createPontoFulgorField();
                constanteDissociacao = createConstanteDissociacaoField();
                ph = new PotenciaDeHidrogenio();

                coeficienteParticao = createDecimalField("coeficienteParticao", "Coeficiente de partição octanol/Água", "a 20-25 ºC", 4);
                densidade = createDecimalField("densidade", "Densidade", "g/cm³ a 20ºC", 4);

                observacoes = type.addFieldString("observacoes");
                observacoes.withTextAreaView().asAtr().label("Observações");
            }

            private STypeDecimal createDecimalField(String fieldname, String label, String subtitle, int colPreference) {
                STypeDecimal f = type.addFieldDecimal(fieldname);
                f.asAtr().label(label).subtitle(subtitle)
                        .asAtrBootstrap().colPreference(colPreference);
                return f;
            }

            private STypeString createEstadoFísicoField() {
                STypeString f = type.addFieldString("estadoFisico", true);
                f.selectionOf("Líquido", "Sólido", "Gasoso")
                        .withView(SViewSelectionBySelect::new)
                        .asAtr().label("Estado físico")
                        .asAtrBootstrap().colPreference(2);
                return f;
            }

            private STypeString createAspectoField() {
                STypeString f = type.addFieldString("aspecto", true);
                f.asAtr().label("Aspecto")
                        .maxLength(50)
                        .asAtrBootstrap().colPreference(4);
                return f;
            }

            private STypeString createCorField() {
                STypeString f = type.addFieldString("cor", true);
                f.asAtr().label("Cor")
                        .maxLength(40)
                        .asAtrBootstrap().colPreference(3);
                return f;
            }

            private STypeString createOdorField() {
                STypeString f = type.addFieldString("odor");
                f.asAtr().label("Odor")
                        .maxLength(40)
                        .asAtrBootstrap().colPreference(3);
                return f;
            }

            private STypeString createHidroliseField() {
                STypeString f = type.addFieldString("hidrolise");
                f.asAtr().label("Hidrólise")
                        .asAtrBootstrap().colPreference(6);
                return f;
            }

            private STypeString createEstabilidadeField() {
                STypeString f = type.addFieldString("estabilidade");
                f.asAtr().label("Estabilidade às temperaturas normal e elevada")
                        .asAtrBootstrap().colPreference(6);
                return f;
            }

            private STypeDecimal createPontoFulgorField() {
                STypeDecimal f = createDecimalField("pontoFulgor", "Ponto de fulgor", "ºC", 3);
                return f;
            }

            private STypeDecimal createConstanteDissociacaoField() {
                STypeDecimal f = type.addFieldDecimal("constanteDissociacao");
                f.asAtr().label("Constante de dissociação")
                        .asAtrBootstrap().colPreference(3);
                return f;
            }

            class Faixa {
                final STypeComposite<SIComposite> root;
                final STypeDecimal                pontoFusao, faixaFusaoDe, faixaFusaoA;

                Faixa(String prefix, String nome) {
                    root = type.addFieldComposite("faixa" + prefix);
                    root.asAtrBootstrap().colPreference(6).asAtr().label(nome);

                    pontoFusao = createDecimalField("ponto" + prefix, "Ponto de " + nome, "ºC");
                    //TODO o campo faixa de fusao precisa de um tipo intervalo
                    // Exemplo: Faixa De 10 a 20
                    faixaFusaoDe = createDecimalField("faixa" + prefix + "De", "Início", "da Faixa");
                    faixaFusaoA = createDecimalField("faixa" + prefix + "A", "Fim", "da Faixa");
                }

                private STypeDecimal createDecimalField(String fieldname, String label, String subtitle) {
                    SType<?> f = root.addFieldDecimal(fieldname);
                    f.asAtr().label(label).subtitle(subtitle)
                            .asAtrBootstrap().colPreference(4);
                    return (STypeDecimal) f;
                }
            }

            class PressaoDeValor {
                final STypeComposite<SIComposite> root;
                final STypeDecimal                valor;
                final STypeString                 unidade;

                PressaoDeValor() {
                    root = type.addFieldComposite("pressaoVapor");
                    root.asAtr().label("Pressão do vapor")
                            .asAtrBootstrap().colPreference(6);

                    valor = createValorField();
                    unidade = createUnidadeField();

                }

                private STypeDecimal createValorField() {
                    STypeDecimal f = root.addFieldDecimal("valor");
                    f.asAtr().label("Valor")
                            .asAtrBootstrap().colPreference(6);
                    return f;
                }

                private STypeString createUnidadeField() {
                    STypeString f = root.addFieldString("unidade");
                    f.selectionOf("mmHg", "Pa", "mPa")
                            .withView(SViewSelectionBySelect::new)
                            .asAtr().label("Unidade")
                            .asAtrBootstrap().colPreference(6);
                    return f;
                }
            }

            class Solubilidade {
                final STypeComposite<SIComposite> root;
                final STypeDecimal                agua, outrosSolventes;

                Solubilidade() {
                    root = type.addFieldComposite("solubilidade");
                    root.asAtr().label("Solubilidade")
                            .asAtrBootstrap().colPreference(6);

                    agua = createDecimalField("solubilidadeAgua", "em água", "mg/L a 20 ou 25 ºC");
                    outrosSolventes = createDecimalField("solubilidadeOutrosSolventes",
                            "em outros solventes", "mg/L a 20 ou 25 ºC");
                }

                private STypeDecimal createDecimalField(String fieldName, String label, String subtitle) {
                    STypeDecimal f = root.addFieldDecimal(fieldName);
                    f.asAtr().label(label).subtitle(subtitle)
                            .asAtrBootstrap().colPreference(6);
                    return f;
                }
            }

            class PotenciaDeHidrogenio {
                final STypeList<STypeComposite<SIComposite>, SIComposite> root;
                final STypeComposite<SIComposite>                         type;
                final STypeDecimal                                        valorPh, solucao, temperatura;

                PotenciaDeHidrogenio() {
                    root = TesteCaracteristicasFisicoQuimicas.this.type.addFieldListOfComposite("phs", "ph");
                    root.withView(SViewListByForm::new).asAtr().label("Lista de pH");
                    type = root.getElementsType();

                    valorPh = createDecimalField("valorPh", "pH", ".");
                    solucao = createDecimalField("solucao", "Solução", "%");
                    temperatura = createDecimalField("temperatura", "Temperatura", "ºC");
                }

                private STypeDecimal createDecimalField(String fieldname, String label, String subtitle) {
                    STypeDecimal valorPh = type.addFieldDecimal(fieldname, true);
                    valorPh.asAtr().label(label).subtitle(subtitle)
                            .asAtrBootstrap().colPreference(4);
                    return valorPh;
                }
            }
        }

        class TesteIrritacaoOcular {
            final STypeList<STypeComposite<SIComposite>, SIComposite> root;
            final STypeComposite<SIComposite>                         type;
            final STypeString                                         laboratorio, protocolo, purezaProdutoTestado, unidadeMedida, especies,
                    linhagem, veiculo, fluoresceina, testeRealizado;
            final STypeDate inicio, fim;
            final Alteracao alteracao;

            class Alteracao {
                final STypeComposite<SIComposite> root;
                final STypeString                 cornea, tempoReversibilidadeCornea, conjuntiva, tempoReversibilidadeConjuntiva,
                        iris, tempoReversibilidadeIris;

                Alteracao() {
                    root = type.addFieldComposite("alteracoes");
                    root.asAtr().label("Alterações");

                    cornea = createStringField("cornea", "Córnea", 6);
                    cornea.selectionOf("Sem alterações", "Opacidade persistente", "Opacidade reversível em...");

                    tempoReversibilidadeCornea = createStringField("tempoReversibilidadeCornea", "Tempo de reversibilidade", 6);

                    conjuntiva = createStringField("conjuntiva", "Conjuntiva", 6);
                    conjuntiva.selectionOf("Sem alterações", "Opacidade persistente",
                            "Opacidade reversível em...");

                    tempoReversibilidadeConjuntiva = createStringField("tempoReversibilidadeConjuntiva", "Tempo de reversibilidade", 6);

                    iris = createStringField("iris", "Íris", 6);
                    iris.selectionOf("Sem alterações", "Opacidade persistente", "Opacidade reversível em...");

                    tempoReversibilidadeIris = createStringField("tempoReversibilidadeIris", "Tempo de reversibilidade", 6);
                }

                private STypeString createStringField(String fieldname, String label,
                                                      int colPreference) {
                    STypeString f = root.addFieldString(fieldname);
                    f.asAtr().label(label)
                            .asAtrBootstrap().colPreference(colPreference);
                    return f;
                }
            }

            private TesteIrritacaoOcular() {
                //TODO criar regra para pelo menos um campo preenchido
                root = rootType.addFieldListOfComposite("testesIrritacaoOcular", "irritacaoOcular");
                root.asAtr().label("Testes Irritação / Corrosão ocular");
                type = root.getElementsType();
                type.asAtr().label("Irritação / Corrosão ocular")
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
                unidadeMedida.selectionOf("g/Kg", "g/L");

                especies = createStringField("especies", "Espécies", null, 4);
                especies.selectionOf("Càes", "Camundongos", "Cobaia", "Coelho", "Galinha",
                        "Informação não disponível", "Peixe", "Primatas", "Rato");

                linhagem = createStringField("linhagem", "Linhagem", null, 6);

                type.addFieldDecimal("numeroAnimais")
                        .asAtr().label("Número de animais")
                        .asAtrBootstrap().colPreference(3);

                veiculo = createStringField("veiculo", "Veículo", null, 3);

                fluoresceina = createStringField("fluoresceina", "Fluoresceína", null, 3);
                fluoresceina.withView(new SViewSelectionByRadio());
                fluoresceina.selectionOf("Sim", "Não");

                testeRealizado = createStringField("testeRealizado", "Teste realizado", null, 3);
                testeRealizado.selectionOf("Com lavagem", "Sem lavagem");

                alteracao = new Alteracao();

                type.addFieldString("observacoes")
                        .withTextAreaView()
                        .asAtr().label("Observações");
            }

            private STypeString createStringField(String fieldname, String label, Integer maxSize, Integer colPreference) {
                STypeString f = type.addFieldString(fieldname);
                f.asAtr().label(label).maxLength(maxSize)
                        .asAtrBootstrap().colPreference(colPreference);
                return f;
            }

            private STypeDate createDateField(String fieldName, String label, int colPreference) {
                STypeDate f = type.addFieldDate(fieldName);
                f.asAtr().label(label).asAtrBootstrap().colPreference(colPreference);
                return f;
            }
        }
    }


}
