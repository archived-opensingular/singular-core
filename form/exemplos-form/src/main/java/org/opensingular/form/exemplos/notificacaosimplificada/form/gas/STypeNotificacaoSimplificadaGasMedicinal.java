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

package org.opensingular.form.exemplos.notificacaosimplificada.form.gas;

import org.opensingular.form.*;
import org.opensingular.form.exemplos.notificacaosimplificada.form.STypeFarmacopeiaReferencia;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewListByMasterDetail;

@SInfoType(name = "STypeNotificacaoSimplificadaGasMedicinal", spackage = SPackageNotificacaoSimplificadaGasMedicinal.class)
public class STypeNotificacaoSimplificadaGasMedicinal extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {

        asAtr().displayString(" ${nomeComercial} - ${descricao} ");
        asAtr().label("Gás Medicinal");

        addDescricao();
        addNomeComercial();
        addInformacoesFarmacopeicas();
        addAcondicionamentos();
    }


    private void addDescricao() {
        STypeString descricao = addFieldString("descricao");
        descricao.asAtr().label("Descrição").required();
        descricao.withSelectView();
        descricao.asAtrBootstrap().colPreference(6);
        descricao.selectionOf("Ciclopropano  99,5%", "Óxido nitroso (NO2) 70%", "Ar comprimido medicinal 79% N2 + 21% O2 ");
    }

    private void addNomeComercial() {
        STypeString nomeComercial = addFieldString("nomeComercial");
        nomeComercial
                .asAtr()
                .label("Nome do gás")
                .asAtrBootstrap()
                .newRow().colPreference(4);

    }

    private void addInformacoesFarmacopeicas() {
        STypeComposite<SIComposite> informacoesFarmacopeicas = addFieldComposite("informacoesFarmacopeicas");
        informacoesFarmacopeicas.asAtr().label("Informações farmacopeicas");

        informacoesFarmacopeicas.addField("farmacopeia", STypeFarmacopeiaReferencia.class);
    }

    private void addAcondicionamentos() {
        STypeList<STypeAcondicionamentoGAS, SIComposite> acondicionamentos = addFieldListOf("acondicionamentos", STypeAcondicionamentoGAS.class);
        acondicionamentos.withMiniumSizeOf(1);
        acondicionamentos
                .withView(new SViewListByMasterDetail()
                        .col(acondicionamentos.getElementsType().embalagemPrimaria, "Embalagem primária"))
                .asAtr().label("Acondicionamento");
    }

}
