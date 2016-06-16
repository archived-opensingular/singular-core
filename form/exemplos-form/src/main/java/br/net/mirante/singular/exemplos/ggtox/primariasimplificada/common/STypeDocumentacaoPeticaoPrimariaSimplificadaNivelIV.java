package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.common;


import br.net.mirante.singular.exemplos.ggtox.primariasimplificada.form.SPackagePPSCommon;
import br.net.mirante.singular.form.*;

@SInfoType(spackage = SPackagePPSCommon.class)
public class STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIV extends STypeDocumentacaoPeticaoPrimariaSimplificadaNivelIII {

    @Override
    protected void onLoadType(TypeBuilder builder) {
        super.onLoadType(builder);

        final STypeAttachmentList informacoesSobreCulturaIndicacao       = addFieldListOfAttachment("informacoesSobreCulturaIndicacao", "informacaoSobreCulturaIndicacao");
        final STypeAttachmentList pareceresTecnicosAvaliacoesDasEmpresas = addFieldListOfAttachment("pareceresTecnicosAvaliacoesDasEmpresas", "parecerTecnicoAvaliacaoDaEmpresa");

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

        adicionarAnexosGerais();
        adicionarAnexosOrgaoRegistrante();
        adicionarAnexosMinisterioSaude();
        adicionarAnexosMInisterioMeioAmbiente();
    }

    private void adicionarAnexosGerais() {
        final STypeAttachmentList unidadesImpressasRotuloBula = addFieldListOfAttachment("unidadesImpressasRotuloBula", "unidadeImpressaRotuloBula");
        final STypeAttachmentList indicacoesUso = addFieldListOfAttachment("indicacoesUso", "indicacaoUso");
        final STypeAttachmentList restricoesUso = addFieldListOfAttachment("restricoesUso", "restricaoUso");
        final STypeAttachmentList intervalosSeguranca = addFieldListOfAttachment("intervalosSeguranca", "intervaloSeguranca");
        final STypeAttachmentList intervalosReentrada = addFieldListOfAttachment("intervalosReentrada", "intervaloReentrada");
        final STypeAttachmentList especificacoesEPI = addFieldListOfAttachment("especificacoesEPI", "especificacaoEPI");
        final STypeAttachmentList procedimentosDescontaminacao = addFieldListOfAttachment("procedimentosDescontaminacao", "procedimentoDescontaminacao");
        final STypeAttachmentList sistemasRecolhimentoRestos = addFieldListOfAttachment("sistemasRecolhimentoRestos", "sistemaRecolhimento");
//        final STypeAttachmentList modelosRotuloBula = addFieldListOfAttachment("modelosRotuloBula", "modeloRotuloBula");
        final STypeAttachmentList comprovantesProtocoloComponente = addFieldListOfAttachment("comprovantesProtocoloComponente", "comprovanteProtocoloComponente");

        unidadesImpressasRotuloBula
                .asAtr()
                .label("Unidades impressas do rótulo e da bula do produto, quando existentes no país de origem");

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

    private void adicionarAnexosOrgaoRegistrante() {
        final STypeComposite<SIComposite> anexosOrgaoRegistrante                = addFieldComposite("anexosOrgaoRegistrante");
        final STypeAttachmentList         estudosEficiencia                     = anexosOrgaoRegistrante.addFieldListOfAttachment("estudosEficiencia", "estudoEficiencia");
        final STypeAttachmentList         informacoesCompatibilidade            = anexosOrgaoRegistrante.addFieldListOfAttachment("informacoesCompatibilidade", "informacaoCompatibilidade");
        final STypeAttachmentList         informacoesDesenvolvimentoResistencia = anexosOrgaoRegistrante.addFieldListOfAttachment("informacoesDesenvolvimentoResistencia", "informacaoDesenvolvimentoResistencia");
        final STypeAttachmentList relatoriosEstudo = anexosOrgaoRegistrante.addFieldListOfAttachment("relatoriosEstudo", "relatorioEstudo");
        final STypeAttachmentList metodosAnalitico = anexosOrgaoRegistrante.addFieldListOfAttachment("metodosAnalitico", "metodoAnalitico");

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

    private void adicionarAnexosMinisterioSaude() {
        final STypeComposite<SIComposite> anexosMinisterioSaude = addFieldComposite("anexosMinisterioSaude");
        final STypeAttachmentList relatoriosEstudoFisicoQuimico = anexosMinisterioSaude.addFieldListOfAttachment("relatoriosEstudoFisicoQuimico", "relatorioEstudoFisicoQuimico");
        final STypeAttachmentList relatoriosEstudoResiduos = anexosMinisterioSaude.addFieldListOfAttachment("relatoriosEstudoResiduos", "relatorioEstudoResiduos");
        final STypeAttachmentList metodosAnaliticosResiduo = anexosMinisterioSaude.addFieldListOfAttachment("metodosAnaliticosResiduo", "metodoAnaliticoResiduo");
        final STypeAttachmentList intervalosReentradaAreaTratada = anexosMinisterioSaude.addFieldListOfAttachment("intervalosReentradaAreaTratada", "intervaloReentradaAreaTratada");
        final STypeAttachmentList estudosToxicologicosAgudos = anexosMinisterioSaude.addFieldListOfAttachment("estudosToxicologicosAgudos", "estudoToxicologicoAgudo");
        final STypeAttachmentList antidotosIntoxicacaoHumana = anexosMinisterioSaude.addFieldListOfAttachment("antidotosIntoxicacaoHumana", "antidotoIntoxicacaoHumana");
        final STypeAttachmentList informacoesCompatibilidade = anexosMinisterioSaude.addFieldListOfAttachment("informacoesCompatibilidade", "informacaoCompatibilidade");

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

        metodosAnaliticosResiduo
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

        informacoesCompatibilidade
                .asAtr()
                .label("Informações referentes à sua compatibilidade com outros produtos");
    }

    private void adicionarAnexosMInisterioMeioAmbiente() {
        final STypeComposite<SIComposite> anexosMinisterioMeioAmbiente = addFieldComposite("anexosMinisterioMeioAmbiente");
        final STypeAttachmentList relatoriosPropriedadesFisicoQuimicas = anexosMinisterioMeioAmbiente.addFieldListOfAttachment("relatoriosPropriedadesFisicoQuimicas", "relatorioPropriedadesFisicoQuimicas");
        final STypeAttachmentList relatoriosEstudoToxicidade = anexosMinisterioMeioAmbiente.addFieldListOfAttachment("relatoriosEstudoToxicidade", "relatorioEstudoToxicidade");
        final STypeAttachmentList relatoriosEstudoToxicidadeAnimaisSuperiores = anexosMinisterioMeioAmbiente.addFieldListOfAttachment("relatoriosEstudoToxicidadeAnimaisSuperiores", "relatorioEstudoToxicidadeAnimaisSuperiores");
        final STypeAttachmentList relatoriosEstudoToxicidadePotencialMutagenico = anexosMinisterioMeioAmbiente.addFieldListOfAttachment("relatoriosEstudoToxicidadePotencialMutagenico", "relatorioEstudoToxicidadePotencialMutagenico");
        final STypeAttachmentList metodosAnaliticosResiduo = anexosMinisterioMeioAmbiente.addFieldListOfAttachment("metodosAnaliticosResiduo", "metodoAnaliticosResiduo");
        final STypeAttachmentList informacoesCompatibilidade = anexosMinisterioMeioAmbiente.addFieldListOfAttachment("informacoesCompatibilidade", "informacaoCompatibilidade");

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

        informacoesCompatibilidade
                .asAtr()
                .label("Informações referentes à sua compatibilidade com outros produtos");
    }
}
