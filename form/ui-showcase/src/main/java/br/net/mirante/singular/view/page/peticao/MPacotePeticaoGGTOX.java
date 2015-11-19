package br.net.mirante.singular.view.page.peticao;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MPanelListaView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorCheckView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorPicklistView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoMultiplaPorSelectView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorRadioView;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorSelectView;
import br.net.mirante.singular.form.mform.core.MTipoString;

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

        addTipoDocumento(peticionamento);
        addDadosRequerente(peticionamento);
        addDadosResponsavel(peticionamento);
        addComponentes(pb, peticionamento);
        addTestes(peticionamento);
        addValidacaoResponsavel(peticionamento);
        addResponsavelTransacao(peticionamento);
        addImpressaoPeticao(peticionamento);

    }

    private void addTipoDocumento(MTipoComposto<?> peticionamento) {
        //TODO adicionar variavel como texto na tela
    }

    private void addDadosRequerente(MTipoComposto<?> peticionamento) {

    }

    private void addDadosResponsavel(MTipoComposto<?> peticionamento) {
        MTipoComposto<MIComposto> dadosResponsavel = peticionamento.addCampoComposto("dadosResponsavel");

        dadosResponsavel.as(AtrBasic::new).label("Dados do Responsável");

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

        MTipoComposto<MIComposto> componente = peticionamento.addCampoComposto("componente");

        componente.as(AtrBasic::new).label("Registro de Componente");

        MTipoComposto<MIComposto> identificacaoComponente = peticionamento.addCampoComposto("identificacaoComponente");

        identificacaoComponente.as(AtrBasic::new).label("Identificação de Componente");

        identificacaoComponente.addCampoString("tipoComponente", true)
                .withSelectionOf("Substância", "Mistura")
                .withView(MSelecaoPorRadioView::new)
                .as(AtrBasic::new).label("Tipo componente");

        MTipoComposto<MIComposto> restricoesComponente = peticionamento.addCampoComposto("restricoesComponente");

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

        MTipoComposto<MIComposto> sinonimiaComponente = peticionamento.addCampoComposto("sinonimiaComponente");

        sinonimiaComponente.as(AtrBasic::new).label("Sinonímia");

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

        MTipoComposto<MIComposto> finalidadesComponente = peticionamento.addCampoComposto("finalidadesComponente");

        finalidadesComponente.as(AtrBasic::new).label("Finalidades");

        finalidadesComponente.addCampoListaOf("finalidades", pb.createTipo("finalidade", MTipoString.class)
                .withSelectionOf("Produção",
                        "Importação",
                        "Exportação",
                        "Comercialização",
                        "Utilização"))
                .withView(MSelecaoMultiplaPorCheckView::new);

        //TODO falta criar modal para cadastrar novo uso pretendido
        MTipoComposto<MIComposto> usosPretendidosComponente = peticionamento.addCampoComposto("usosPretendidosComponente");

        usosPretendidosComponente.as(AtrBasic::new).label("Uso pretendido");

        usosPretendidosComponente.addCampoListaOf("usosPretendidos", pb.createTipo("usoPretendido", MTipoString.class)
                .withSelectionOf("Uso 1",
                        "Uso 2",
                        "Uso 3"))
                .withView(MSelecaoMultiplaPorPicklistView::new)
                .as(AtrBasic::new).label("Lista de uso pretendido/mistura");

        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> nomesComerciais = peticionamento.addCampoListaOfComposto("nomesComerciais", "nomeComercial");
        MTipoComposto<MIComposto> nomeComercial = nomesComerciais.getTipoElementos();

        nomeComercial.addCampoString("nome", true)
                .as(AtrBasic::new).label("Nome comercial")
                .tamanhoMaximo(80);
        MTipoLista<MTipoComposto<MIComposto>, MIComposto> fabricantes = nomeComercial.addCampoListaOfComposto("fabricantes", "fabricante");

        //TODO Fabricante deve ser uma pesquisa
        MTipoComposto<MIComposto> fabricante = fabricantes.getTipoElementos();
        fabricante.addCampoString("cnpj").as(AtrBasic::new).label("CNPJ");
        fabricante.addCampoString("razaoSocial").as(AtrBasic::new).label("Razão social");
        fabricante.addCampoString("cidade").as(AtrBasic::new).label("Cidade");
        fabricante.addCampoString("pais").as(AtrBasic::new).label("País");

        fabricantes
                .withView(MPanelListaView::new)
                .as(AtrBasic::new).label("Fabricante(s)").tamanhoInicial(1);

        nomesComerciais
                .withView(MPanelListaView::new)
                .as(AtrBasic::new).label("Nome comercial").tamanhoInicial(1);

//        final MTipoLista<MTipoComposto<MIComposto>, MIComposto> embalagens = peticionamento.addCampoListaOfComposto("embalagens", "embalagem");
//        MTipoComposto<MIComposto> embalagem = embalagens.getTipoElementos();
//
//        //TODO converter sim nao para true false
//        embalagem.addCampoString("produtoExterior", true)
//                .withSelectionOf("Sim", "Não")
//                .withView(MSelecaoPorRadioView::new)
//                .as(AtrBasic::new).label("Produto formulado no exterior?");
//
//        embalagem.addCampoString("tipo", true)
//                .withSelectionOf(getTiposEmbalagem())
//                .withView(MSelecaoPorSelectView::new)
//                .as(AtrBasic::new).label("Tipo");
//
//        embalagem.addCampoString("material", true)
//                .withSelectionOf(getMateriais())
//                .withView(MSelecaoPorSelectView::new)
//                .as(AtrBasic::new).label("Material");
//
//        embalagem.addCampoInteger("capacidade", true)
//                .as(AtrBasic::new).label("Capacidade")
//                .tamanhoMaximo(15);
//
//        embalagem.addCampoString("unidadeMedida", true)
//                .withSelectionOf(getUnidadesMedida())
//                .withView(MSelecaoPorSelectView::new)
//                .as(AtrBasic::new).label("Unidade medida");
//
//        embalagens
//                .withView(MPanelListaView::new)
//                .as(AtrBasic::new).label("Embalagem").tamanhoInicial(1);
    }

//    private String[] getTiposEmbalagem() {
//        return Arrays.stream(TipoEmbalagem.values())
//                .map(TipoEmbalagem::getDescricao)
//                .toArray(size -> new String[size]);
//    }

    private String[] getMateriais() {
        return new String[]{""};
    }

    private String[] getUnidadesMedida() {
        return new String[]{""};
    }

    private void addTestes(MTipoComposto<?> peticionamento) {

    }

    private void addValidacaoResponsavel(MTipoComposto<?> peticionamento) {

    }

    private void addResponsavelTransacao(MTipoComposto<?> peticionamento) {
    }

    private void addImpressaoPeticao(MTipoComposto<?> peticionamento) {
    }


}
