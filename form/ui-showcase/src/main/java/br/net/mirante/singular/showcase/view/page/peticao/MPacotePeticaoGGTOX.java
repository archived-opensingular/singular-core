package br.net.mirante.singular.showcase.view.page.peticao;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.*;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCNPJ;
import br.net.mirante.singular.form.wicket.AtrBootstrap;
import java.util.Optional;

public class MPacotePeticaoGGTOX extends MPacote {

    public static final String PACOTE        = "mform.peticao";
    public static final String TIPO          = "PeticionamentoGGTOX";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

    private MTipoComposto<MIComposto> dadosResponsavel;
    private MTipoString               dadosResponsavel_responsavelTecnico;

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

        addTipoDocumento(pb, peticionamento);
        addDadosRequerente(pb, peticionamento);
        MTipoComposto<MIComposto> dadosResponsavel = addDadosResponsavel(pb, peticionamento);
        MTipoLista<MTipoComposto<MIComposto>, MIComposto> componentes = addComponentes(pb, peticionamento);
        addValidacaoResponsavel(pb, peticionamento);
        addResponsavelTransacao(pb, peticionamento);
        addImpressaoPeticao(pb, peticionamento);

        MTabView tabbed = new MTabView();
        tabbed.addTab("tudo" , "Tudo").add(dadosResponsavel).add(componentes);
        tabbed.addTab(dadosResponsavel);
        tabbed.addTab(componentes);
        peticionamento.withView(tabbed);

    }

    private void addTipoDocumento(PacoteBuilder pb, MTipoComposto<?> peticionamento) {
        //TODO adicionar variavel como texto na tela
    }

    private void addDadosRequerente(PacoteBuilder pb, MTipoComposto<?> peticionamento) {

    }

    private MTipoComposto<MIComposto> addDadosResponsavel(PacoteBuilder pb, MTipoComposto<?> peticionamento) {
        dadosResponsavel = peticionamento.addCampoComposto("dadosResponsavel");

        dadosResponsavel.as(AtrBasic::new).label("Dados do Responsável");

        //TODO Como fazer a seleção para um objeto composto/enum ?
        //TODO a recuperação de valores deve ser dinamica
        dadosResponsavel_responsavelTecnico = dadosResponsavel.addCampoString("responsavelTecnico", true);
        dadosResponsavel_responsavelTecnico
            .withSelectionOf(getResponsaveis())
            .withView(MSelecaoPorSelectView::new)
            .as(AtrBasic::new).label("Responsável Técnico")
                .as(AtrBootstrap::new).colPreference(3);
        dadosResponsavel.addCampoString("representanteLegal", true)
            .withSelectionOf(getResponsaveis())
            .withView(MSelecaoPorSelectView::new)
            .as(AtrBasic::new).label("Representante Legal")
                .as(AtrBootstrap::new).colPreference(3);

        // TODO preciso de um campo boolean mas as labels devem ser as descritas abaixo
        //TODO deve ser possivel alinhar o texto: text-left text-right text-justify text-nowrap
        dadosResponsavel.addCampoString("concordo", true)
            .withSelectionOf("Concordo", "Não Concordo")
            .withView(MSelecaoPorRadioView::new);

        return dadosResponsavel;
    }

    private String[] getResponsaveis() {
        return new String[] { "Daniel", "Delfino", "Fabrício", "Lucas", "Tetsuo", "Vinícius" };
    }

    private MTipoLista<MTipoComposto<MIComposto>, MIComposto> addComponentes(PacoteBuilder pb, MTipoComposto<?> peticionamento) {
        // TODO como fazer uma tabela de componentes, com botao novo (aguardando mestre-detalhe)
        // e mostrar os campos de inserção apenas se clicar no botao novo

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> componentes = peticionamento.addCampoListaOfComposto("componentes", "componente");
        MTipoComposto<MIComposto> componente = componentes.getTipoElementos();

        componentes
            .withView(MPanelListaView::new)
            .as(AtrBasic::new)
            .label("Componente");

        componente.as(AtrBasic::new).label("Registro de Componente");

        MTipoComposto<MIComposto> identificacaoComponente = componente.addCampoComposto("identificacaoComponente");

        identificacaoComponente.as(AtrBasic::new).label("Identificação de Componente")
                .as(AtrBootstrap::new).colPreference(4);

        identificacaoComponente.addCampoString("tipoComponente", true)
            .withSelectionOf("Substância", "Mistura")
            .withView(MSelecaoPorRadioView::new)
            .as(AtrBasic::new).label("Tipo componente");

        MTipoComposto<MIComposto> restricoesComponente = componente.addCampoComposto("restricoesComponente");

        restricoesComponente.as(AtrBasic::new).label("Restrições")
                .as(AtrBootstrap::new).colPreference(4);

        //TODO caso eu marque sem restrições os outros campos devem ser desabilitados
        restricoesComponente.addCampoListaOf("restricoes", pb.createTipo("restricao", MTipoString.class)
            .withSelectionOf("Impureza relevante presente",
                "Controle de impureza determinado",
                "Restrição de uso em algum país",
                "Restrição de uso em alimentos",
                "Sem restrições"))
            .withView(MSelecaoMultiplaPorCheckView::new)
            .as(AtrBasic::new).label("Restrições");

        MTipoComposto<MIComposto> sinonimiaComponente = componente.addCampoComposto("sinonimiaComponente");

        sinonimiaComponente.as(AtrBasic::new).label("Sinonímia")
            .as(AtrBootstrap::new).colPreference(4);

        sinonimiaComponente.addCampoListaOf("sinonimiaAssociada", pb.createTipo("sinonimia", MTipoString.class)
            .withSelectionOf("Sinonímia teste",
                "Sinonímia teste 2",
                "Sinonímia teste 3"))
            .withView(MSelecaoMultiplaPorSelectView::new)
            .as(AtrBasic::new)
            .label("Sinonímias já associadas a esta substância/mistura")
            .enabled(false);

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> sinonimias = sinonimiaComponente.addCampoListaOfComposto("sinonimias", "sinonimia");
        final MTipoComposto<?> sinonimia = sinonimias.getTipoElementos();
        sinonimia.addCampoString("nomeSinonimia", true)
            .as(AtrBasic::new).label("Sinonímia sugerida")
            .tamanhoMaximo(100);

        sinonimias
            .withView(MTableListaView::new)
            .as(AtrBasic::new).label("Lista de sinonímias sugeridas para esta substância/mistura");

        MTipoComposto<MIComposto> finalidadesComponente = componente.addCampoComposto("finalidadesComponente");

        finalidadesComponente.as(AtrBasic::new).label("Finalidades")
                .as(AtrBootstrap::new).colPreference(4);

        finalidadesComponente.addCampoListaOf("finalidades", pb.createTipo("finalidade", MTipoString.class)
            .withSelectionOf("Produção",
                "Importação",
                "Exportação",
                "Comercialização",
                "Utilização"))
            .withView(MSelecaoMultiplaPorCheckView::new);

        //TODO falta criar modal para cadastrar novo uso pretendido
        MTipoComposto<MIComposto> usosPretendidosComponente = componente.addCampoComposto("usosPretendidosComponente");

        usosPretendidosComponente.as(AtrBasic::new).label("Uso pretendido")
                .as(AtrBootstrap::new).colPreference(4);

        usosPretendidosComponente.addCampoListaOf("usosPretendidos", pb.createTipo("usoPretendido", MTipoString.class)
            .withSelectionOf("Uso 1",
                "Uso 2",
                "Uso 3"))
            .withView(MSelecaoMultiplaPorPicklistView::new)
            .as(AtrBasic::new).label("Lista de uso pretendido/mistura");

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> nomesComerciais = componente.addCampoListaOfComposto("nomesComerciais", "nomeComercial");
        MTipoComposto<MIComposto> nomeComercial = nomesComerciais.getTipoElementos();

        nomeComercial.addCampoString("nome", true)
            .as(AtrBasic::new).label("Nome comercial")
            .tamanhoMaximo(80);
        MTipoLista<MTipoComposto<MIComposto>, MIComposto> fabricantes = nomeComercial.addCampoListaOfComposto("fabricantes", "fabricante");

        //TODO Fabricante deve ser uma pesquisa
        MTipoComposto<MIComposto> fabricante = fabricantes.getTipoElementos();
        //TODO como usar o tipo cnpj
        fabricante.addCampo("cnpj", MTipoCNPJ.class).as(AtrBasic::new).label("CNPJ").as(AtrBootstrap::new).colPreference(4);
        fabricante.addCampoString("razaoSocial").as(AtrBasic::new).label("Razão social").as(AtrBootstrap::new).colPreference(4);
        fabricante.addCampoString("cidade").as(AtrBasic::new).label("Cidade").as(AtrBootstrap::new).colPreference(2);
        fabricante.addCampoString("pais").as(AtrBasic::new).label("País").as(AtrBootstrap::new).colPreference(2);

        fabricantes
            .withView(MPanelListaView::new)
            .as(AtrBasic::new).label("Fabricante(s)");

        nomesComerciais
            .withView(MPanelListaView::new)
            .as(AtrBasic::new).label("Nome comercial");

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> embalagens = componente.addCampoListaOfComposto("embalagens", "embalagem");
        MTipoComposto<MIComposto> embalagem = embalagens.getTipoElementos();

        //TODO converter sim nao para true false
        embalagem.addCampoString("produtoExterior", true)
            .withSelectionOf("Sim", "Não")
            .withView(MSelecaoPorRadioView::new)
            .as(AtrBasic::new).label("Produto formulado no exterior?")
                .as(AtrBootstrap::new).colPreference(2);

        embalagem.addCampoString("tipo", true)
            .withSelectionOf(getTiposEmbalagem())
            .withView(MSelecaoPorSelectView::new)
            .as(AtrBasic::new).label("Tipo")
            .as(AtrBootstrap::new).colPreference(3);

        embalagem.addCampoString("material", true)
            .withSelectionOf(getMateriais())
            .withView(MSelecaoPorSelectView::new)
            .as(AtrBasic::new).label("Material")
            .as(AtrBootstrap::new).colPreference(3);

        embalagem.addCampoInteger("capacidade", true)
            .as(AtrBasic::new).label("Capacidade")
            .tamanhoMaximo(15)
            .as(AtrBootstrap::new).colPreference(3);

        //TODO caso o array tenha uma string vazia, ocorre um NPE
        embalagem.addCampoString("unidadeMedida", true)
            .withSelectionOf(getUnidadesMedida())
            .withView(MSelecaoPorSelectView::new)
            .as(AtrBasic::new).label("Unidade medida")
            .as(AtrBootstrap::new).colPreference(1);

        embalagens
            .withView(MTableListaView::new)
            .as(AtrBasic::new).label("Embalagem");

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> anexos = componente.addCampoListaOfComposto("anexos", "anexo");
        MTipoComposto<MIComposto> anexo = anexos.getTipoElementos();

        anexos
            .withView(MPanelListaView::new)
            .as(AtrBasic::new).label("Anexos");

        MTipoAttachment arquivo = anexo.addCampo("arquivo", MTipoAttachment.class);
        arquivo.as(AtrBasic.class).label("Informe o caminho do arquivo para o anexo")
                .as(AtrBootstrap::new).colPreference(3);

        anexo.addCampoString("tipoArquivo")
            .withSelectionOf("Ficha de emergência", "Ficha de segurança", "Outros")
            .withView(MSelecaoPorSelectView::new)
            .as(AtrBasic::new).label("Tipo do arquivo a ser anexado")
                .as(AtrBootstrap::new).colPreference(3);

        addTestes(pb, componente);

        return componentes;
    }

    private String[] getTiposEmbalagem() {
        return new String[] {
            "Balde",
            "Barrica",
            "Bombona",
            "Caixa",
            "Carro tanque",
            "Cilindro",
            "Container",
            "Frasco",
            "Galão",
            "Garrafa",
            "Lata",
            "Saco",
            "Tambor"
        };

    }

    private String[] getMateriais() {
        return new String[] {
            "Papel",
            "Alumínio",
            "Ferro",
            "Madeira"
        };
    }

    private String[] getUnidadesMedida() {
        return new String[] { "cm" };
    }

    private void addTestes(PacoteBuilder pb, MTipoComposto<?> componente) {
        //TODO deve ser encontrado uma maneira de vincular o teste ao componente
        addTesteCaracteristicasFisicoQuimicas(pb, componente);
        addTesteIrritacaoOcular(pb, componente);
        addTesteTeratogenicidade(pb, componente);
        addTesteNeurotoxicidade(pb, componente);
    }

    private void addTesteCaracteristicasFisicoQuimicas(PacoteBuilder pb, MTipoComposto<?> componente) {
        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> testes = componente.addCampoListaOfComposto("testesCaracteristicasFisicoQuimicas", "caracteristicasFisicoQuimicas");
        MTipoComposto<MIComposto> teste = testes.getTipoElementos();

        testes
//            .withView(MPanelListaView::new)
            .as(AtrBasic::new).label("Testes Características fisíco-químicas");

        teste.as(AtrBasic::new).label("Características fisíco-químicas");

        MTipoString estadoFisico = teste.addCampoString("estadoFisico", true);
        estadoFisico.withSelectionOf("Líquido", "Sólido", "Gasoso")
            .withView(MSelecaoPorSelectView::new)
            .as(AtrBasic::new).label("Estado físico")
                .as(AtrBootstrap::new).colPreference(2);

        MTipoString aspecto = teste.addCampoString("aspecto", true);
        aspecto.as(AtrBasic::new).label("Aspecto")
            .tamanhoMaximo(50)
                .as(AtrBootstrap::new).colPreference(4);

        MTipoString cor = teste.addCampoString("cor", true);
        cor.as(AtrBasic::new).label("Cor")
            .tamanhoMaximo(40)
                .as(AtrBootstrap::new).colPreference(3);

        MTipoString odor = teste.addCampoString("odor");
        odor.as(AtrBasic::new).label("Odor")
            .tamanhoMaximo(40)
                .as(AtrBootstrap::new).colPreference(3);

        testes.withView(new MListMasterDetailView()
                .col(estadoFisico)
                .col(aspecto)
                .col(cor)
                .col(odor)
        );

        MTipoComposto<MIComposto> faixaFusao = teste.addCampoComposto("faixaFusao");
        faixaFusao.as(AtrBootstrap::new).colPreference(6);

        //TODO cade as mascaras para campos decimais
        faixaFusao.as(AtrBasic::new).label("Fusão");

        faixaFusao.addCampoDecimal("pontoFusao")
                .as(AtrBasic::new).label("Ponto de fusão").subtitle("ºC")
                .as(AtrBootstrap::new).colPreference(4);

        //TODO o campo faixa de fusao precisa de um tipo intervalo
        // Exemplo: Faixa De 10 a 20
        faixaFusao.addCampoDecimal("faixaFusaoDe")
            .as(AtrBasic::new).label("Início").subtitle("da Faixa")
            .as(AtrBootstrap::new).colPreference(4);

        faixaFusao.addCampoDecimal("faixaFusaoA")
            .as(AtrBasic::new).label("Fim").subtitle("da Faixa")
            .as(AtrBootstrap::new).colPreference(4);

        MTipoComposto<MIComposto> faixaEbulicao = teste.addCampoComposto("faixaEbulicao");

        faixaEbulicao.as(AtrBasic::new).label("Ebulição")
                .as(AtrBootstrap::new).colPreference(6);

        faixaEbulicao.addCampoDecimal("pontoEbulicao")
                .as(AtrBasic::new).label("Ebulição").subtitle("ºC")
                .as(AtrBootstrap::new).colPreference(4);

        faixaEbulicao.addCampoDecimal("faixaEbulicaoDe")
            .as(AtrBasic::new).label("Início").subtitle("da Faixa")
            .as(AtrBootstrap::new).colPreference(4);

        faixaEbulicao.addCampoDecimal("faixaEbulicaoA")
            .as(AtrBasic::new).label("Fim").subtitle("da Faixa")
            .as(AtrBootstrap::new).colPreference(4);

        MTipoComposto<MIComposto> pressaoVapor = teste.addCampoComposto("pressaoVapor");
        pressaoVapor.as(AtrBasic::new).label("Pressão do vapor")
                .as(AtrBootstrap::new).colPreference(6);

        pressaoVapor.addCampoDecimal("valor")
            .as(AtrBasic::new).label("Valor")
                .as(AtrBootstrap::new).colPreference(6);

        pressaoVapor.addCampoString("unidade")
            .withSelectionOf("mmHg", "Pa", "mPa")
            .withView(MSelecaoPorSelectView::new)
            .as(AtrBasic::new).label("Unidade")
                .as(AtrBootstrap::new).colPreference(6);

        MTipoComposto<MIComposto> solubilidade = teste.addCampoComposto("solubilidade");
        solubilidade.as(AtrBasic::new).label("Solubilidade")
                .as(AtrBootstrap::new).colPreference(6);

        solubilidade.addCampoDecimal("solubilidadeAgua")
            .as(AtrBasic::new).label("em água").subtitle("mg/L a 20 ou 25 ºC")
                .as(AtrBootstrap::new).colPreference(6);

        solubilidade.addCampoDecimal("solubilidadeOutrosSolventes")
            .as(AtrBasic::new).label("em outros solventes").subtitle("mg/L a 20 ou 25 ºC")
                .as(AtrBootstrap::new).colPreference(6);

        teste.addCampoString("hidrolise")
            .as(AtrBasic::new).label("Hidrólise")
                .as(AtrBootstrap::new).colPreference(6);

        teste.addCampoString("estabilidade")
            .as(AtrBasic::new).label("Estabilidade às temperaturas normal e elevada")
                .as(AtrBootstrap::new).colPreference(6);

        teste.addCampoDecimal("pontoFulgor")
            .as(AtrBasic::new).label("Ponto de fulgor").subtitle("ºC")
                .as(AtrBootstrap::new).colPreference(3);

        teste.addCampoDecimal("constanteDissociacao")
            .as(AtrBasic::new).label("Constante de dissociação")
                .as(AtrBootstrap::new).colPreference(3);

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> phs = teste.addCampoListaOfComposto("phs", "ph");
        MTipoComposto<MIComposto> ph = phs.getTipoElementos();

        ph.addCampoDecimal("valorPh", true)
            .as(AtrBasic::new).label("pH")
            .as(AtrBootstrap::new).colPreference(4);

        ph.addCampoDecimal("solucao", true)
            .as(AtrBasic::new).label("Solução").subtitle("%")
            .as(AtrBootstrap::new).colPreference(4);

        ph.addCampoDecimal("temperatura", true)
            .as(AtrBasic::new).label("Temperatura").subtitle("ºC")
            .as(AtrBootstrap::new).colPreference(4);

        phs
            .withView(MPanelListaView::new)
            .as(AtrBasic::new).label("Embalagem");

        teste.addCampoDecimal("coeficienteParticao")
            .as(AtrBasic::new).label("Coeficiente de partição octanol/Água").subtitle("a 20-25 ºC")
                .as(AtrBootstrap::new).colPreference(4);

        teste.addCampoDecimal("densidade")
            .as(AtrBasic::new).label("Densidade").subtitle("g/cm³ a 20ºC")
                .as(AtrBootstrap::new).colPreference(4);

        teste.addCampoString("observacoes")
            .withView(MTextAreaView::new)
            .as(AtrBasic::new).label("Observações");
    }

    private void addTesteIrritacaoOcular(PacoteBuilder pb, MTipoComposto<?> componente) {

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> testes = componente.addCampoListaOfComposto("testesIrritacaoOcular", "irritacaoOcular");
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

    private void addTesteTeratogenicidade(PacoteBuilder pb, MTipoComposto<?> componente) {

    }

    private void addTesteNeurotoxicidade(PacoteBuilder pb, MTipoComposto<?> componente) {

    }

    private void addValidacaoResponsavel(PacoteBuilder pb, MTipoComposto<?> peticionamento) {

    }

    private void addResponsavelTransacao(PacoteBuilder pb, MTipoComposto<?> peticionamento) {}

    private void addImpressaoPeticao(PacoteBuilder pb, MTipoComposto<?> peticionamento) {}

}
