/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.opensingular.form.exemplos.emec.credenciamentoescolagoverno.form;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.view.SViewByBlock;

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
