/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.sample;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.basic.AtrXML;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.STypeAttachment;

@SInfoType(spackage = FormTestPackage.class,  name = "STypeTroublesomeListElement")
public class STypeTroublesomeListElement extends STypeComposite<SIComposite> {

    private final String PDF = "pdf";


    public STypeComposite<SIComposite> registroEmbarcacaoComp;
    public STypeAttachment             anexoReg1;
    public STypeAttachment             anexoReg2;
    public STypeAttachment             anexoReg3;

    public STypeComposite<SIComposite> condicaoOperacaoComp;
    public STypeAttachment             anexoCond1;
    public STypeAttachment             anexoCond2;
    public STypeAttachment             anexoCond3;


    public STypeComposite<SIComposite> seguroComp;
    public STypeAttachment             anexoSeg1;
    public STypeAttachment             anexoSeg2;
    public STypeAttachment             anexoSeg3;


    public STypeComposite<SIComposite> cascoNuComp;
    public STypeAttachment             anexoNu1;


    public STypeComposite<SIComposite> contrucaoReformaComp;

    public STypeAttachment anexoConsRef1;
    public STypeAttachment anexoConsRef2;
    public STypeAttachment anexoConsRef3;
    public STypeAttachment anexoConsRef4;
    public STypeAttachment anexoConsRef5;
    public STypeAttachment anexoConsRef6;
    public STypeString     keepNodeTest;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        keepNodeTest = this.addFieldString("keepNodeTest");
        keepNodeTest.as(AtrXML::new).keepEmptyNode();


        registroEmbarcacaoComp = addFieldComposite("registroEmbarcacaoComp");
        registroEmbarcacaoComp.asAtr().label("Registro da Embarcação");
        registroEmbarcacaoComp.asAtr().subtitle("Pelo menos um dos anexos deve ser enviado.");

        anexoReg1 = registroEmbarcacaoComp.addField("anexoReg1", STypeAttachment.class);
        anexoReg1.asAtr().label("PRPM - Provisão de Registro de Propriedade Marítima").asAtrBootstrap().colPreference(6);
        anexoReg1.asAtr().subtitle("Embarcações com AB maior que 100");
        anexoReg1.asAtr().allowedFileTypes(PDF);

        anexoReg2 = registroEmbarcacaoComp.addField("anexoReg2", STypeAttachment.class);
        anexoReg2.asAtr().label("TIE - Título de Inscrição de Embarcação").asAtrBootstrap().colPreference(6);
        anexoReg2.asAtr().subtitle("Embarcações com AB igual ou inferior a 100");
        anexoReg2.asAtr().allowedFileTypes(PDF);

        anexoReg3 = registroEmbarcacaoComp.addField("anexoReg3", STypeAttachment.class);
        anexoReg3.asAtr().label("DPP - Documento Provisório de Propriedade").asAtrBootstrap().colPreference(6);
        anexoReg3.asAtr().allowedFileTypes(PDF);

        condicaoOperacaoComp = addFieldComposite("condicaoOperacaoComp");
        condicaoOperacaoComp.asAtr().label("Condição para Operação da Embarcação");
        condicaoOperacaoComp.asAtr().subtitle("Pelo menos um dos anexos deve ser enviado.");

        anexoCond1 = condicaoOperacaoComp.addField("anexoCond1", STypeAttachment.class);
        anexoCond1.asAtr().label("CSN – Certificado de Segurança da Navegação").asAtrBootstrap().colPreference(6);
        anexoCond1.asAtr().allowedFileTypes(PDF);

        anexoCond2 = condicaoOperacaoComp.addField("anexoCond2", STypeAttachment.class);
        anexoCond2.asAtr().label("CGS – Certificado de Gerenciamento de Segurança").asAtrBootstrap().colPreference(6);
        anexoCond2.asAtr().subtitle("Embarcações SOLAS ou com AB maior que 500");
        anexoCond2.asAtr().allowedFileTypes(PDF);

        anexoCond3 = condicaoOperacaoComp.addField("anexoCond3", STypeAttachment.class);
        anexoCond3.asAtr().label("Termo de Responsabilidade firmado com a Capitania dos Portos").asAtrBootstrap().colPreference(6);
        anexoCond3.asAtr().allowedFileTypes(PDF);

        seguroComp = addFieldComposite("seguroComp");
        seguroComp.asAtr().label("Seguros");
        seguroComp.asAtr().subtitle("Pelo menos um dos anexos deve ser enviado.");

        anexoSeg1 = seguroComp.addField("anexoSeg1", STypeAttachment.class);
        anexoSeg1
                .asAtr()
                .label("Seguro Obrigatório de Danos Pessoais Causados por Embarcação e suas Cargas - DPEM")
                .asAtrBootstrap()
                .colPreference(6);
        anexoSeg1.asAtr().allowedFileTypes(PDF);

        anexoSeg2 = seguroComp.addField("anexoSeg2", STypeAttachment.class);
        anexoSeg2
                .asAtr()
                .label("Seguro Protection and Indemnity (P&I).")
                .asAtrBootstrap()
                .colPreference(6);
        anexoSeg2.asAtr().allowedFileTypes(PDF);

        anexoSeg3 = seguroComp.addField("anexoSeg3", STypeAttachment.class);
        anexoSeg3
                .asAtr()
                .label("Outros")
                .asAtrBootstrap()
                .colPreference(6);
        anexoSeg3.asAtr().allowedFileTypes(PDF);


        cascoNuComp = addFieldComposite("cascoNuComp");
        cascoNuComp.asAtr().label("Embarcação Afretada a Casco Nu");

        anexoNu1 = cascoNuComp.addField("anexoNu1", STypeAttachment.class);
        anexoNu1.asAtr()
                .label("Contrato de afretamento registrado por escritura pública lavrada por qualquer Tabelionato de Notas ou instrumento particular com reconhecimento de firma. (NR)")
                .asAtrBootstrap().colPreference(6);
        anexoNu1.asAtr().allowedFileTypes(PDF);

        contrucaoReformaComp = addFieldComposite("contrucaoReformaComp");
        contrucaoReformaComp.asAtr().label("Embarcação em Construção ou Reforma");
        contrucaoReformaComp.asAtr().subtitle("Pelo menos um dos anexos deve ser enviado.");

        anexoConsRef1 = contrucaoReformaComp.addField("anexoConsRef1", STypeAttachment.class);
        anexoConsRef1.asAtr().label("Contrato de Construção de Embarcação").asAtrBootstrap().colPreference(6);
        anexoConsRef1.asAtr().allowedFileTypes(PDF);

        anexoConsRef2 = contrucaoReformaComp.addField("anexoConsRef2", STypeAttachment.class);
        anexoConsRef2.asAtr().label("Cronograma Físico e Financeiro de Construção").asAtrBootstrap().colPreference(6);
        anexoConsRef2.asAtr().allowedFileTypes(PDF);

        anexoConsRef3 = contrucaoReformaComp.addField("anexoConsRef3", STypeAttachment.class);
        anexoConsRef3.asAtr().label("Quadro de Usos e Fontes").asAtrBootstrap().colPreference(6);
        anexoConsRef3.asAtr().allowedFileTypes(PDF);

        anexoConsRef4 = contrucaoReformaComp.addField("anexoConsRef4", STypeAttachment.class);
        anexoConsRef4.asAtr().label("Licença da Marinha do Brasil para Construção de Embarcação").asAtrBootstrap().colPreference(6);
        anexoConsRef4.asAtr().allowedFileTypes(PDF);

        anexoConsRef5 = contrucaoReformaComp.addField("anexoConsRef5", STypeAttachment.class);
        anexoConsRef5.asAtr().label("Termo de Compromisso de Relatório Trimestral").asAtrBootstrap().colPreference(6);
        anexoConsRef5.asAtr().allowedFileTypes(PDF);

        anexoConsRef6 = contrucaoReformaComp.addField("anexoConsRef6", STypeAttachment.class);
        anexoConsRef6.asAtr().label("Licença Provisória para Entrada em Tráfego").asAtrBootstrap().colPreference(6);
        anexoConsRef6.asAtr().allowedFileTypes(PDF);

    }

}
