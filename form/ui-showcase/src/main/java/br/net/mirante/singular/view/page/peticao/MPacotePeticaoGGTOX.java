package br.net.mirante.singular.view.page.peticao;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.*;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCNPJ;
import br.net.mirante.singular.form.wicket.AtrWicket;

public class MPacotePeticaoGGTOX extends MPacote {

    public static final String PACOTE = "mform.peticao";
    public static final String TIPO = "PeticionamentoGGTOX";
    public static final String NOME_COMPLETO = PACOTE + "." + TIPO;

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
        addDadosResponsavel(pb, peticionamento);
        addComponentes(pb, peticionamento);
        addValidacaoResponsavel(pb, peticionamento);
        addResponsavelTransacao(pb, peticionamento);
        addImpressaoPeticao(pb, peticionamento);

    }

    private void addTipoDocumento(PacoteBuilder pb, MTipoComposto<?> peticionamento) {
        //TODO adicionar variavel como texto na tela
    }

    private void addDadosRequerente(PacoteBuilder pb, MTipoComposto<?> peticionamento) {

    }

    private void addDadosResponsavel(PacoteBuilder pb, MTipoComposto<?> peticionamento) {
        MTipoComposto<MIComposto> dadosResponsavel = peticionamento.addCampoComposto("dadosResponsavel");

        dadosResponsavel.as(AtrBasic::new).label("Dados do Responsável")
                .as(AtrWicket::new).larguraPref(4);

        //TODO Como fazer a seleção para um objeto composto/enum ?
        //TODO a recuperação de valores deve ser dinamica
        dadosResponsavel.addCampoString("responsavelTecnico", true)
                .withSelectionOf(getResponsaveis())
                .withView(MSelecaoPorSelectView::new)
                .as(AtrBasic::new).label("Responsável Técnico");
        dadosResponsavel.addCampoString("representanteLegal", true)
                .withSelectionOf(getResponsaveis())
                .withView(MSelecaoPorSelectView::new)
                .as(AtrBasic::new).label("Representante Legal");

        // TODO preciso de um campo boolean mas as labels devem ser as descritas abaixo
        //TODO deve ser possivel alinhar o texto: text-left text-right text-justify text-nowrap
        dadosResponsavel.addCampoString("concordo", true)
                .withSelectionOf("Concordo", "Não Concordo")
                .withView(MSelecaoPorRadioView::new);
    }

    private String[] getResponsaveis() {
        return new String[]{"Daniel", "Delfino", "Fabrício", "Lucas", "Tetsuo", "Vinícius"};
    }

    private void addComponentes(PacoteBuilder pb, MTipoComposto<?> peticionamento) {
        // TODO como fazer uma tabela de componentes, com botao novo (aguardando mestre-detalhe)
        // e mostrar os campos de inserção apenas se clicar no botao novo

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> componentes = peticionamento.addCampoListaOfComposto("componentes", "componente");
        MTipoComposto<MIComposto> componente = componentes.getTipoElementos();

        componentes
                .withView(MPanelListaView::new)
                .as(AtrBasic::new).label("Componente").tamanhoInicial(1);

        componente.as(AtrBasic::new).label("Registro de Componente");

        MTipoComposto<MIComposto> identificacaoComponente = componente.addCampoComposto("identificacaoComponente");

        identificacaoComponente.as(AtrBasic::new).label("Identificação de Componente");

        identificacaoComponente.addCampoString("tipoComponente", true)
                .withSelectionOf("Substância", "Mistura")
                .withView(MSelecaoPorRadioView::new)
                .as(AtrBasic::new).label("Tipo componente");

        MTipoComposto<MIComposto> restricoesComponente = componente.addCampoComposto("restricoesComponente");

        restricoesComponente.as(AtrBasic::new).label("Restrições");

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
                .as(AtrWicket::new).larguraPref(4);

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
                .withView(MPanelListaView::new)
                .as(AtrBasic::new).label("Lista de sinonímias sugeridas para esta substância/mistura").tamanhoInicial(1);

        MTipoComposto<MIComposto> finalidadesComponente = componente.addCampoComposto("finalidadesComponente");

        finalidadesComponente.as(AtrBasic::new).label("Finalidades");

        finalidadesComponente.addCampoListaOf("finalidades", pb.createTipo("finalidade", MTipoString.class)
                .withSelectionOf("Produção",
                        "Importação",
                        "Exportação",
                        "Comercialização",
                        "Utilização"))
                .withView(MSelecaoMultiplaPorCheckView::new);

        //TODO falta criar modal para cadastrar novo uso pretendido
        MTipoComposto<MIComposto> usosPretendidosComponente = componente.addCampoComposto("usosPretendidosComponente");

        usosPretendidosComponente.as(AtrBasic::new).label("Uso pretendido");

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
        fabricante.addCampo("cnpj", MTipoCNPJ.class).as(AtrBasic::new).label("CNPJ");
        fabricante.addCampoString("razaoSocial").as(AtrBasic::new).label("Razão social");
        fabricante.addCampoString("cidade").as(AtrBasic::new).label("Cidade");
        fabricante.addCampoString("pais").as(AtrBasic::new).label("País");

        fabricantes
                .withView(MPanelListaView::new)
                .as(AtrBasic::new).label("Fabricante(s)").tamanhoInicial(1);

        nomesComerciais
                .withView(MPanelListaView::new)
                .as(AtrBasic::new).label("Nome comercial").tamanhoInicial(1);

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> embalagens = componente.addCampoListaOfComposto("embalagens", "embalagem");
        MTipoComposto<MIComposto> embalagem = embalagens.getTipoElementos();

        //TODO converter sim nao para true false
        embalagem.addCampoString("produtoExterior", true)
                .withSelectionOf("Sim", "Não")
                .withView(MSelecaoPorRadioView::new)
                .as(AtrBasic::new).label("Produto formulado no exterior?");

        embalagem.addCampoString("tipo", true)
                .withSelectionOf(getTiposEmbalagem())
                .withView(MSelecaoPorSelectView::new)
                .as(AtrBasic::new).label("Tipo")
                .as(AtrWicket::new).larguraPref(3);

        embalagem.addCampoString("material", true)
                .withSelectionOf(getMateriais())
                .withView(MSelecaoPorSelectView::new)
                .as(AtrBasic::new).label("Material")
                .as(AtrWicket::new).larguraPref(3);

        embalagem.addCampoInteger("capacidade", true)
                .as(AtrBasic::new).label("Capacidade")
                .tamanhoMaximo(15)
                .as(AtrWicket::new).larguraPref(3);

        //TODO caso o array tenha uma string vazia, ocorre um NPE
        embalagem.addCampoString("unidadeMedida", true)
                .withSelectionOf(getUnidadesMedida())
                .withView(MSelecaoPorSelectView::new)
                .as(AtrBasic::new).label("Unidade medida")
                .as(AtrWicket::new).larguraPref(3);

        embalagens
                .withView(MPanelListaView::new)
                .as(AtrBasic::new).label("Embalagem").tamanhoInicial(1);

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> anexos = componente.addCampoListaOfComposto("anexos", "anexo");
        MTipoComposto<MIComposto> anexo = anexos.getTipoElementos();

        anexos
                .withView(MPanelListaView::new)
                .as(AtrBasic::new).label("Anexos").tamanhoInicial(1);

        MTipoAttachment arquivo = anexo.addCampo("arquivo", MTipoAttachment.class);
        arquivo.as(AtrBasic.class).label("Informe o caminho do arquivo para o anexo");

        anexo.addCampoString("tipoArquivo")
                .withSelectionOf("Ficha de emergência", "Ficha de segurança", "Outros")
                .withView(MSelecaoPorSelectView::new)
                .as(AtrBasic::new).label("Tipo do arquivo a ser anexado");

        addTestes(pb, componente);
    }

    private String[] getTiposEmbalagem() {
        return new String[]{
                "Balde",
                "Barrica" ,
                "Bombona",
                "Caixa" ,
                "Carro tanque" ,
                "Cilindro",
                "Container" ,
                "Frasco" ,
                "Galão" ,
                "Garrafa" ,
                "Lata" ,
                "Saco" ,
                "Tambor"
        };

    }

    private String[] getMateriais() {
        return new String[]{
                "Papel",
                "Alumínio",
                "Ferro",
                "Madeira"
        };
    }

    private String[] getUnidadesMedida() {
        return new String[]{"cm"};
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
                .withView(MPanelListaView::new)
                .as(AtrBasic::new).label("Testes Características fisíco-químicas").tamanhoInicial(1);

        teste.as(AtrBasic::new).label("Características fisíco-químicas")
                .as(AtrWicket::new).larguraPref(4);

        teste.addCampoString("estadoFisico", true)
                .withSelectionOf("Líquido", "Sólido", "Gasoso")
                .withView(MSelecaoPorSelectView::new)
                .as(AtrBasic::new).label("Estado físico");

        teste.addCampoString("aspecto", true)
                .as(AtrBasic::new).label("Aspecto")
                .tamanhoMaximo(50);

        teste.addCampoString("cor", true)
                .as(AtrBasic::new).label("Cor")
                .tamanhoMaximo(40);

        teste.addCampoString("odor")
                .as(AtrBasic::new).label("Odor")
                .tamanhoMaximo(40);

        teste.addCampoString("pontoFusao")
                .as(AtrBasic::new).label("Ponto de fusão (ºC)");

        MTipoComposto<MIComposto> faixaFusao = teste.addCampoComposto("faixaFusao");

        //TODO cade as mascaras para campos decimais
        faixaFusao.as(AtrBasic::new).label("Faixa de fusão (ºC)");

        //TODO o campo faixa de fusao precisa de um tipo intervalo
        // Exemplo: Faixa De 10 a 20
        faixaFusao.addCampoString("faixaFusaoDe")
                .as(AtrBasic::new).label("De")
                .as(AtrWicket::new).larguraPref(3);

        faixaFusao.addCampoString("faixaFusaoA")
                .as(AtrBasic::new).label("A")
                .as(AtrWicket::new).larguraPref(3);


        teste.addCampoString("pontoEbulicao")
                .as(AtrBasic::new).label("Ponto de ebulição (ºC)");

        MTipoComposto<MIComposto> faixaEbulicao = teste.addCampoComposto("faixaEbulicao");

        faixaEbulicao.as(AtrBasic::new).label("Faixa de ebulição (ºC)");

        faixaEbulicao.addCampoString("faixaEbulicaoDe")
                .as(AtrBasic::new).label("De")
                .as(AtrWicket::new).larguraPref(3);

        faixaEbulicao.addCampoString("faixaEbulicaoA")
                .as(AtrBasic::new).label("A")
                .as(AtrWicket::new).larguraPref(3);

        teste.addCampoString("pressaoVapor")
                .as(AtrBasic::new).label("Pressão do vapor (mmHg/Pa/mPa)");

        teste.addCampoString("tipoPressaoVapor")
                .withSelectionOf("mmHg", "Pa", "mPa")
                .withView(MSelecaoPorSelectView::new)
                .as(AtrBasic::new).label("Tipo de Pressão do vapor");

        teste.addCampoString("solubilidadeAgua")
                .as(AtrBasic::new).label("Solubilidade em água (mg/L a 20 ou 25 ºC)");

        teste.addCampoString("solubilidadeOutrosSolventes")
                .as(AtrBasic::new).label("Solubilidade em outros solventes (mg/L a 20 ou 25 ºC)");

        teste.addCampoString("hidrolise")
                .as(AtrBasic::new).label("Hidrólise");

        teste.addCampoString("estabilidade")
                .as(AtrBasic::new).label("Estabilidade às temperaturas normal e elevada");

        teste.addCampoString("pontoFulgor")
                .as(AtrBasic::new).label("Ponto de fulgor (ºC)");

        teste.addCampoString("constanteDissociacao")
                .as(AtrBasic::new).label("Constante de dissociação");

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> phs = teste.addCampoListaOfComposto("phs", "ph");
        MTipoComposto<MIComposto> ph = phs.getTipoElementos();

        ph.addCampoInteger("valorPh", true)
                .as(AtrBasic::new).label("pH")
                .tamanhoMaximo(7)
                .as(AtrWicket::new).larguraPref(4);

        ph.addCampoInteger("solucao", true)
                .as(AtrBasic::new).label("Solução (%)")
                .tamanhoMaximo(7)
                .as(AtrWicket::new).larguraPref(4);

        ph.addCampoString("temperatura", true)
                .as(AtrBasic::new).label("Temperatura (ºC)")
                .tamanhoMaximo(8)
                .as(AtrWicket::new).larguraPref(4);

        phs
                .withView(MPanelListaView::new)
                .as(AtrBasic::new).label("Embalagem").tamanhoInicial(1);

        teste.addCampoString("coeficienteParticao")
                .as(AtrBasic::new).label("Coeficiente de partição octanol/Água a 20-25 ºC");

        teste.addCampoInteger("densidade")
                .as(AtrBasic::new).label("Densidade (g/cm³ a 20ºC)");

        teste.addCampoString("observacoes")
                .withView(MTextAreaView::new)
                .as(AtrBasic::new).label("Observações");
    }

    private void addTesteIrritacaoOcular(PacoteBuilder pb, MTipoComposto<?> componente) {

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> testes = componente.addCampoListaOfComposto("testesIrritacaoOcular", "irritacaoOcular");
        MTipoComposto<MIComposto> teste = testes.getTipoElementos();

        //TODO criar regra para pelo menos um campo preenchido

        testes
                .withView(MPanelListaView::new)
                .as(AtrBasic::new).label("Testes Irritação / Corrosão ocular").tamanhoInicial(1);

        teste.as(AtrBasic::new).label("Irritação / Corrosão ocular")
                .as(AtrWicket::new).larguraPref(4);

        teste.addCampoString("laboratorio")
                .as(AtrBasic::new).label("Laboratório")
                .tamanhoMaximo(50);

        teste.addCampoString("protocoloReferencia")
                .as(AtrBasic::new).label("Protocolo de referência")
                .tamanhoMaximo(50);

        teste.addCampoData("dataInicioEstudo")
                .as(AtrBasic::new).label("Data de início do estudo");

        teste.addCampoData("dataFimEstudo")
                .as(AtrBasic::new).label("Data final do estudo");

        teste.addCampoString("purezaProdutoTestado")
                .as(AtrBasic::new).label("Pureza do produto testado");

        teste.addCampoString("unidadeMedida")
                .withSelectionOf("g/Kg", "g/L")
                .as(AtrBasic::new).label("Unidade de medida");

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
                .as(AtrBasic::new).label("Espécies");

        teste.addCampoString("linhagem")
                .as(AtrBasic::new).label("Linhagem");

        teste.addCampoString("numeroAnimais")
                .as(AtrBasic::new).label("Número de animais");

        teste.addCampoString("veiculo")
                .as(AtrBasic::new).label("Veículo");

        teste.addCampoString("fluoresceina")
                .withSelectionOf("Sim", "Não")
                .as(AtrBasic::new).label("Fluoresceína");

        teste.addCampoString("testeRealizado")
                .withSelectionOf("Com lavagem", "Sem lavagem")
                .as(AtrBasic::new).label("Teste realizado");

        MTipoComposto<MIComposto> alteracoes = teste.addCampoComposto("alteracoes");

        alteracoes.as(AtrBasic::new).label("Alterações")
                .as(AtrWicket::new).larguraPref(7);

        alteracoes.addCampoString("cornea")
                .withSelectionOf("Sem alterações", "Opacidade persistente", "Opacidade reversível em...")
                .as(AtrBasic::new).label("Córnea")
                .as(AtrWicket::new).larguraPref(6);

        alteracoes.addCampoString("tempoReversibilidadeCornea")
                .as(AtrBasic::new).label("Tempo de reversibilidade")
                .as(AtrWicket::new).larguraPref(6);

        alteracoes.addCampoString("conjuntiva")
                .withSelectionOf("Sem alterações", "Opacidade persistente", "Opacidade reversível em...")
                .as(AtrBasic::new).label("Conjuntiva")
                .as(AtrWicket::new).larguraPref(6);

        alteracoes.addCampoString("tempoReversibilidadeConjuntiva")
                .as(AtrBasic::new).label("Tempo de reversibilidade")
                .as(AtrWicket::new).larguraPref(6);

        alteracoes.addCampoString("iris")
                .withSelectionOf("Sem alterações", "Opacidade persistente", "Opacidade reversível em...")
                .as(AtrBasic::new).label("Íris")
                .as(AtrWicket::new).larguraPref(6);

        alteracoes.addCampoString("tempoReversibilidadeIris")
                .as(AtrBasic::new).label("Tempo de reversibilidade")
                .as(AtrWicket::new).larguraPref(6);

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

    private void addResponsavelTransacao(PacoteBuilder pb, MTipoComposto<?> peticionamento) {
    }

    private void addImpressaoPeticao(PacoteBuilder pb, MTipoComposto<?> peticionamento) {
    }


}
