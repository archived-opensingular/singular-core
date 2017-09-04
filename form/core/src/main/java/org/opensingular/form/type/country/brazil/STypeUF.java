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

package org.opensingular.form.type.country.brazil;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.lib.commons.util.Loggable;

@SInfoType(name = "UnidadeFederacao", spackage = SPackageCountryBrazil.class)
public class STypeUF extends STypeComposite<SIComposite> implements Loggable {

    public STypeString sigla;
    public STypeString nome;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        this.asAtr()
                .label("Estado")
                .displayString("${nome!} - ${sigla!}")
                .asAtrBootstrap()
                .colPreference(2);

        sigla = this.addFieldString("sigla");
        nome = this.addFieldString("nome");

        this.selection()
                .id(sigla)
                .display("${nome!} - ${sigla!}")
                .simpleProvider(listaBuilder -> {
                    fillAC(listaBuilder.add().get());
                    fillAP(listaBuilder.add().get());
                    fillAM(listaBuilder.add().get());
                    fillBA(listaBuilder.add().get());
                    fillCE(listaBuilder.add().get());
                    fillDF(listaBuilder.add().get());
                    fillES(listaBuilder.add().get());
                    fillGO(listaBuilder.add().get());
                    fillMA(listaBuilder.add().get());
                    fillMT(listaBuilder.add().get());
                    fillMS(listaBuilder.add().get());
                    fillMG(listaBuilder.add().get());
                    fillPA(listaBuilder.add().get());
                    fillPB(listaBuilder.add().get());
                    fillPR(listaBuilder.add().get());
                    fillPE(listaBuilder.add().get());
                    fillPI(listaBuilder.add().get());
                    fillRJ(listaBuilder.add().get());
                    fillRN(listaBuilder.add().get());
                    fillRS(listaBuilder.add().get());
                    fillRO(listaBuilder.add().get());
                    fillRR(listaBuilder.add().get());
                    fillSC(listaBuilder.add().get());
                    fillSP(listaBuilder.add().get());
                    fillSE(listaBuilder.add().get());
                    fillTO(listaBuilder.add().get());

                });
    }

    public SIComposite fillAC(SIComposite AC) {
        AC.setValue(nome, "Acre");
        AC.setValue(sigla, "AC");
        return AC;
    }

    public SIComposite fillAP(SIComposite AP) {
        AP.setValue(nome, "Amapá");
        AP.setValue(sigla, "AP");
        return AP;
    }

    public SIComposite fillAM(SIComposite AM) {
        AM.setValue(nome, "Amazonas");
        AM.setValue(sigla, "AM");
        return AM;
    }

    public SIComposite fillBA(SIComposite BA) {
        BA.setValue(nome, "Bahia");
        BA.setValue(sigla, "BA");
        return BA;
    }

    public SIComposite fillCE(SIComposite CE) {
        CE.setValue(nome, "Ceará");
        CE.setValue(sigla, "CE");
        return CE;
    }

    public SIComposite fillDF(SIComposite DF) {
        DF.setValue(nome, "Distrito Federal");
        DF.setValue(sigla, "DF");
        return DF;
    }

    public SIComposite fillES(SIComposite ES) {
        ES.setValue(nome, "Espírito Santo");
        ES.setValue(sigla, "ES");
        return ES;
    }

    public SIComposite fillGO(SIComposite GO) {
        GO.setValue(nome, "Goiás");
        GO.setValue(sigla, "GO");
        return GO;
    }

    public SIComposite fillMA(SIComposite MA) {
        MA.setValue(nome, "Maranhão");
        MA.setValue(sigla, "MA");
        return MA;
    }

    public SIComposite fillMT(SIComposite MT) {
        MT.setValue(nome, "Mato Grosso");
        MT.setValue(sigla, "MT");
        return MT;
    }

    public SIComposite fillMS(SIComposite MS) {
        MS.setValue(nome, "Mato Grosso do Sul");
        MS.setValue(sigla, "MS");
        return MS;
    }

    public SIComposite fillMG(SIComposite MG) {
        MG.setValue(nome, "Minas Gerais");
        MG.setValue(sigla, "MG");
        return MG;
    }

    public SIComposite fillPA(SIComposite PA) {
        PA.setValue(nome, "Pará");
        PA.setValue(sigla, "PA");
        return PA;
    }

    public SIComposite fillPB(SIComposite PB) {
        PB.setValue(nome, "Paraíba");
        PB.setValue(sigla, "PB");
        return PB;
    }

    public SIComposite fillPR(SIComposite PR) {
        PR.setValue(nome, "Paraná");
        PR.setValue(sigla, "PR");
        return PR;
    }

    public SIComposite fillPE(SIComposite PE) {
        PE.setValue(nome, "Pernambuco");
        PE.setValue(sigla, "PE");
        return PE;
    }

    public SIComposite fillPI(SIComposite PI) {
        PI.setValue(nome, "Piauí");
        PI.setValue(sigla, "PI");
        return PI;
    }

    public SIComposite fillRJ(SIComposite RJ) {
        RJ.setValue(nome, "Rio de Janeiro");
        RJ.setValue(sigla, "RJ");
        return RJ;
    }

    public SIComposite fillRN(SIComposite RN) {
        RN.setValue(nome, "Rio Grande do Norte");
        RN.setValue(sigla, "RN");
        return RN;
    }

    public SIComposite fillRS(SIComposite RS) {
        RS.setValue(nome, "Rio Grande do Sul");
        RS.setValue(sigla, "RS");
        return RS;
    }

    public SIComposite fillRO(SIComposite RO) {
        RO.setValue(nome, "Rondônia");
        RO.setValue(sigla, "RO");
        return RO;
    }

    public SIComposite fillRR(SIComposite RR) {
        RR.setValue(nome, "Roraima");
        RR.setValue(sigla, "RR");
        return RR;
    }

    public SIComposite fillSC(SIComposite SC) {
        SC.setValue(nome, "Santa Catarina");
        SC.setValue(sigla, "SC");
        return SC;
    }

    public SIComposite fillSP(SIComposite SP) {
        SP.setValue(nome, "São Paulo");
        SP.setValue(sigla, "SP");
        return SP;
    }

    public SIComposite fillSE(SIComposite SE) {
        SE.setValue(nome, "Sergipe");
        SE.setValue(sigla, "SE");
        return SE;
    }

    public SIComposite fillTO(SIComposite TO) {
        TO.setValue(nome, "Tocantins");
        TO.setValue(sigla, "TO");
        return TO;
    }

}
