/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package br.net.mirante.singular.exemplos.emec.credenciamentoescolagoverno.form;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.view.SViewByBlock;

@SInfoType(spackage = SPackageCredenciamentoEscolaGoverno.class)
public class STypePDIDocumentos extends STypeComposite<SIComposite>{

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        
        addSituacaoLegal();
        addRegularidadeFiscal();
        addDemonstracaoPatrimonio();
        
        // cria um bloco por campo
        setView(SViewByBlock::new)
            .newBlock("22 Situação Legal").add("situacaoLegal")
            .newBlock("23 Regularidade Fiscal").add("regularidadeFiscal")
            .newBlock("24 Demonstração de Patrimônio").add("demonstracaoPatrimonio");
    }
    
    private void addSituacaoLegal() {
        final STypeComposite<SIComposite> situacaoLegal = this.addFieldComposite("situacaoLegal");
        situacaoLegal.addFieldAttachment("atosConstitutivos")
            .asAtr().label("Atos Constitutivos");
        situacaoLegal.addFieldAttachment("certidaoConjuntaDebitos")
            .asAtr().label("Certidão Conjunta de Débitos Relativos a Tributos Federais e à Dívida Ativa da União");
        situacaoLegal.addFieldAttachment("inscricaoCadastroContribuintesEstado")
            .asAtr().label("Inscrição no cadastro de contribuintes do Estado");
        situacaoLegal.addFieldAttachment("inscricaoCadastroContribuintesMunicipio")
            .asAtr().label("Inscrição no cadastro de contribuintes do Município");
        situacaoLegal.addFieldAttachment("comprovanteCNPJ")
            .asAtr().label("Comprovante de CNPJ");
        situacaoLegal.addFieldAttachment("certidaoRegularidadeFGTS")
            .asAtr().label("Certidão de regularidade com FGTS");
        situacaoLegal.addFieldAttachment("certidaoRegularidadeINSS")
            .asAtr().label("Certidão de regularidade com a Seguridade Social (INSS)");
    }

    private void addRegularidadeFiscal() {
        final STypeComposite<SIComposite> regularidadeFiscal = this.addFieldComposite("regularidadeFiscal");
        regularidadeFiscal.addFieldAttachment("fazendaEstadual")
            .asAtr().label("Fazenda Estadual");
        regularidadeFiscal.addFieldAttachment("fazendaMunicipal")
            .asAtr().label("Fazenda Municipal");
    }

    private void addDemonstracaoPatrimonio() {
        final STypeComposite<SIComposite> demonstracaoPatrimonio = this.addFieldComposite("demonstracaoPatrimonio");
        demonstracaoPatrimonio.addFieldAttachment("balanco")
            .asAtr().label("Balanço");
        demonstracaoPatrimonio.addFieldAttachment("demonstracoesContabeis")
            .asAtr().label("Demonstrações Contábeis");
    }
    
}
