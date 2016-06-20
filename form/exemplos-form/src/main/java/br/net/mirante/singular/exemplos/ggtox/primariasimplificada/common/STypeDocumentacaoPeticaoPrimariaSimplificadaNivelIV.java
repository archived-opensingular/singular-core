package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIV extends STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIII {

    public STypeAttachmentList informacoesSobreCulturaIndicacao;
    public STypeAttachmentList pareceresTecnicosAvaliacoesDasEmpresas;

    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

        informacoesSobreCulturaIndicacao = addFieldListOfAttachment("informacoesSobreCulturaIndicacao", "informacaoSobreCulturaIndicacao");
        pareceresTecnicosAvaliacoesDasEmpresas = addFieldListOfAttachment("pareceresTecnicosAvaliacoesDasEmpresas", "parecerTecnicoAvaliacaoDaEmpresa");

        informacoesSobreCulturaIndicacao
                .asAtr()
                .label("Informações sobre cultura e indicação")
                .asAtrBootstrap()
                .colPreference(12);

        pareceresTecnicosAvaliacoesDasEmpresas
                .asAtr()
                .label("Parecer técnicos de Avaliação da Empresa")
                .asAtrBootstrap()
                .colPreference(12);

        adicionarAnexosNaturezaQuimicaBiomquimica();
        adicionarAnexosOrgaoRegistrante();
        adicionarAnexosMinisterioSaude();
        adicionarAnexosMInisterioMeioAmbiente();
    }

    public STypeComposite<SIComposite> quimicaBioquimica;
    public STypeAttachmentList         indicacoesUso;
    public STypeAttachmentList         restricoesUso;
    public STypeAttachmentList         intervalosSeguranca;
    public STypeAttachmentList         intervalosReentrada;
    public STypeAttachmentList         especificacoesEPI;
    public STypeAttachmentList         procedimentosDescontaminacao;
    public STypeAttachmentList         sistemasRecolhimentoRestos;
    public STypeAttachmentList         comprovantesProtocoloComponente;

    private void adicionarAnexosNaturezaQuimicaBiomquimica() {

        quimicaBioquimica = addFieldComposite("quimicaBioquimica");
        indicacoesUso = quimicaBioquimica.addFieldListOfAttachment("indicacoesUso", "indicacaoUso");
        restricoesUso = quimicaBioquimica.addFieldListOfAttachment("restricoesUso", "restricaoUso");
        intervalosSeguranca = quimicaBioquimica.addFieldListOfAttachment("intervalosSeguranca", "intervaloSeguranca");
        intervalosReentrada = quimicaBioquimica.addFieldListOfAttachment("intervalosReentrada", "intervaloReentrada");
        especificacoesEPI = quimicaBioquimica.addFieldListOfAttachment("especificacoesEPI", "especificacaoEPI");
        procedimentosDescontaminacao = quimicaBioquimica.addFieldListOfAttachment("procedimentosDescontaminacao", "procedimentoDescontaminacao");
        sistemasRecolhimentoRestos = quimicaBioquimica.addFieldListOfAttachment("sistemasRecolhimentoRestos", "sistemaRecolhimento");
//        final STypeAttachmentList modelosRotuloBula = addFieldListOfAttachment("modelosRotuloBula", "modeloRotuloBula");
        comprovantesProtocoloComponente = quimicaBioquimica.addFieldListOfAttachment("comprovantesProtocoloComponente", "comprovanteProtocoloComponente");

        quimicaBioquimica
                .asAtr()
                .label("Produtos formulados e pré-misturas de natureza química ou bioquímica");


        indicacoesUso
                .asAtr()
                .label("Indicação de uso (culturas e alvos biológicos), informações detalhadas sobre o modo de ação " +
                        "do produto, modalidade de emprego (pré-emergência, pós-emergência, etc.), dose recomendada, " +
                        "concentração e modo de preparo de calda, modo e equipamentos de aplicação, época, número e " +
                        "intervalo de aplicações");

        restricoesUso
                .asAtr()
                .label("Restrições de uso e recomendações especiais");

        intervalosSeguranca
                .asAtr()
                .label("Intervalo de segurança");

        intervalosReentrada
                .asAtr()
                .label("Intervalo de reentrada");

        especificacoesEPI
                .asAtr()
                .label("Especificação dos equipamentos de proteção individual apropriados para a aplicação do " +
                        "produto, bem como medidas de proteção coletiva");

        procedimentosDescontaminacao
                .asAtr()
                .label("Procedimentos para descontaminação de embalagens e equipamentos de aplicação");

        sistemasRecolhimentoRestos
                .asAtr()
                .label("Sistema de recolhimento de destinação final de embalagens e restos de produtos");
//
//        modelosRotuloBula
//                .asAtr()
//                .label("Modelo de rótulo e bula");

        comprovantesProtocoloComponente
                .asAtr()
                .label("Comprovante ou protocolo de registro no Brasil de seus componentes, inclusive do produto " +
                        "técnico");
    }

    public STypeComposite<SIComposite> anexosOrgaoRegistrante;
    public STypeAttachmentList         estudosEficiencia;
    public STypeAttachmentList         informacoesCompatibilidade;
    public STypeAttachmentList         informacoesDesenvolvimentoResistencia;
    public STypeAttachmentList         relatoriosEstudo;
    public STypeAttachmentList         metodosAnalitico;

    private void adicionarAnexosOrgaoRegistrante() {

        anexosOrgaoRegistrante = addFieldComposite("anexosOrgaoRegistrante");
        estudosEficiencia = anexosOrgaoRegistrante.addFieldListOfAttachment("estudosEficiencia", "estudoEficiencia");
        informacoesCompatibilidade = anexosOrgaoRegistrante.addFieldListOfAttachment("informacoesCompatibilidade", "informacaoCompatibilidade");
        informacoesDesenvolvimentoResistencia = anexosOrgaoRegistrante.addFieldListOfAttachment("informacoesDesenvolvimentoResistencia", "informacaoDesenvolvimentoResistencia");
        relatoriosEstudo = anexosOrgaoRegistrante.addFieldListOfAttachment("relatoriosEstudo", "relatorioEstudo");
        metodosAnalitico = anexosOrgaoRegistrante.addFieldListOfAttachment("metodosAnalitico", "metodoAnalitico");

        anexosOrgaoRegistrante
                .asAtr()
                .label("Anexos ao Órgão Registrante");

        estudosEficiencia
                .asAtr()
                .label("Estudos e informações sobre a eficiência e a praticabilidade do produto na(s) finalidadeConformeMatriz(s) " +
                        "de uso proposta(s), devendo ser conduzidos conforme suas características e de acordo com " +
                        "as normas complementares do órgão responsável");

        informacoesCompatibilidade
                .asAtr()
                .label("Informações referentes à sua compatibilidade com outros produtos");

        informacoesDesenvolvimentoResistencia
                .asAtr()
                .label("Informações sobre o desenvolvimento de resistência ao produto");

        relatoriosEstudo
                .asAtr()
                .label("Relatório de estudo de resíduos, intervalo de segurança e, quando for o caso, limite dos " +
                        "resíduos estranhos");

        metodosAnalitico
                .asAtr()
                .label("Método analítico e sua sensibilidade para determinação de resíduos do agrotóxico, para fins " +
                        "de monitoramento e fiscalização");
    }

    public STypeComposite<SIComposite> anexosMinisterioSaude;
    public STypeAttachmentList         relatoriosEstudoFisicoQuimico;
    public STypeAttachmentList         relatoriosEstudoResiduos;
    public STypeAttachmentList         metodosAnaliticosResiduo2;
    public STypeAttachmentList         intervalosReentradaAreaTratada;
    public STypeAttachmentList         estudosToxicologicosAgudos;
    public STypeAttachmentList         antidotosIntoxicacaoHumana;
    public STypeAttachmentList         informacoesCompatibilidade2;

    private void adicionarAnexosMinisterioSaude() {

        anexosMinisterioSaude = addFieldComposite("anexosMinisterioSaude");
        relatoriosEstudoFisicoQuimico = anexosMinisterioSaude.addFieldListOfAttachment("relatoriosEstudoFisicoQuimico", "relatorioEstudoFisicoQuimico");
        relatoriosEstudoResiduos = anexosMinisterioSaude.addFieldListOfAttachment("relatoriosEstudoResiduos", "relatorioEstudoResiduos");
        metodosAnaliticosResiduo2 = anexosMinisterioSaude.addFieldListOfAttachment("metodosAnaliticosResiduo2", "metodoAnaliticoResiduo");
        intervalosReentradaAreaTratada = anexosMinisterioSaude.addFieldListOfAttachment("intervalosReentradaAreaTratada", "intervaloReentradaAreaTratada");
        estudosToxicologicosAgudos = anexosMinisterioSaude.addFieldListOfAttachment("estudosToxicologicosAgudos", "estudoToxicologicoAgudo");
        antidotosIntoxicacaoHumana = anexosMinisterioSaude.addFieldListOfAttachment("antidotosIntoxicacaoHumana", "antidotoIntoxicacaoHumana");
        informacoesCompatibilidade2 = anexosMinisterioSaude.addFieldListOfAttachment("informacoesCompatibilidade2", "informacaoCompatibilidade");

        anexosMinisterioSaude
                .asAtr()
                .label("Anexos ao Ministério da Saúde");

        relatoriosEstudoFisicoQuimico
                .asAtr()
                .label("Relatório de estudos de propriedades físico-químicas");

        relatoriosEstudoResiduos
                .asAtr()
                .label("Relatório de estudo de resíduos, intervalo de segurança e, quando for o caso, limite dos " +
                        "resíduos estranhos");

        metodosAnaliticosResiduo2
                .asAtr()
                .label("Método analítico e sua sensibilidade para determinação de resíduos de agrotóxico, para fins " +
                        "de monitoramento e fiscalização");

        intervalosReentradaAreaTratada
                .asAtr()
                .label("Intervalo de reentrada de pessoas nas áreas tratadas");

        estudosToxicologicosAgudos
                .asAtr()
                .label("Estudos toxicológicos agudos e de mutagenicidade");

        antidotosIntoxicacaoHumana
                .asAtr()
                .label("Antídoto ou tratamento disponível no país, para os casos de intoxicação humana");

        informacoesCompatibilidade2
                .asAtr()
                .label("Informações referentes à sua compatibilidade com outros produtos");
    }

    public STypeComposite<SIComposite> anexosMinisterioMeioAmbiente;
    public STypeAttachmentList         relatoriosPropriedadesFisicoQuimicas;
    public STypeAttachmentList         relatoriosEstudoToxicidade;
    public STypeAttachmentList         relatoriosEstudoToxicidadeAnimaisSuperiores;
    public STypeAttachmentList         relatoriosEstudoToxicidadePotencialMutagenico;
    public STypeAttachmentList         metodosAnaliticosResiduo;
    public STypeAttachmentList         informacoesCompatibilidade3;

    private void adicionarAnexosMInisterioMeioAmbiente() {

        anexosMinisterioMeioAmbiente = addFieldComposite("anexosMinisterioMeioAmbiente");
        relatoriosPropriedadesFisicoQuimicas = anexosMinisterioMeioAmbiente.addFieldListOfAttachment("relatoriosPropriedadesFisicoQuimicas", "relatorioPropriedadesFisicoQuimicas");
        relatoriosEstudoToxicidade = anexosMinisterioMeioAmbiente.addFieldListOfAttachment("relatoriosEstudoToxicidade", "relatorioEstudoToxicidade");
        relatoriosEstudoToxicidadeAnimaisSuperiores = anexosMinisterioMeioAmbiente.addFieldListOfAttachment("relatoriosEstudoToxicidadeAnimaisSuperiores", "relatorioEstudoToxicidadeAnimaisSuperiores");
        relatoriosEstudoToxicidadePotencialMutagenico = anexosMinisterioMeioAmbiente.addFieldListOfAttachment("relatoriosEstudoToxicidadePotencialMutagenico", "relatorioEstudoToxicidadePotencialMutagenico");
        metodosAnaliticosResiduo = anexosMinisterioMeioAmbiente.addFieldListOfAttachment("metodosAnaliticosResiduo", "metodoAnaliticosResiduo");
        informacoesCompatibilidade3 = anexosMinisterioMeioAmbiente.addFieldListOfAttachment("informacoesCompatibilidade3", "informacaoCompatibilidade");

        anexosMinisterioMeioAmbiente
                .asAtr()
                .label("Anexos ao Ministério do Meio Ambiente");

        relatoriosPropriedadesFisicoQuimicas
                .asAtr()
                .label("Relatórios de estudos de propriedades físico-químicas");

        relatoriosEstudoToxicidade
                .asAtr()
                .label("Relatório de estudos de dados relativos à toxicidade para microorganismos, microcrustáceos, " +
                        "peixes, algas, organismos de solo, aves, plantas e insetos não-alvo");

        relatoriosEstudoToxicidadeAnimaisSuperiores
                .asAtr()
                .label("Relatório de estudos de dados relativos à toxicidade para animais superiores");

        relatoriosEstudoToxicidadePotencialMutagenico
                .asAtr()
                .label("Relatório de estudos de dados relativos ao potencial mutagênico");

        metodosAnaliticosResiduo
                .asAtr()
                .label("Método analítico e sua sensibilidade para determinação de resíduos de agortóxico, para fins " +
                        "de monitoramento e fiscalização");

        informacoesCompatibilidade3
                .asAtr()
                .label("Informações referentes à sua compatibilidade com outros produtos");
    }
}
