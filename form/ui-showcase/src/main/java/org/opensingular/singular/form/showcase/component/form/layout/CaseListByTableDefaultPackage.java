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

package org.opensingular.singular.form.showcase.component.form.layout;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.util.STypeYearMonth;
import org.opensingular.form.view.SViewListByTable;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;

/**
 * List by Table
 */
@CaseItem(componentName = "List by Table", subCaseName = "Default", group = Group.LAYOUT)
public class CaseListByTableDefaultPackage extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {

        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        STypeList<STypeComposite<SIComposite>, SIComposite> certificacoes = testForm.addFieldListOfComposite("certificacoes", "certificacao");
        certificacoes.asAtr().label("Certificações");
        STypeComposite<?> certificacao = certificacoes.getElementsType();
        STypeYearMonth dataCertificacao = certificacao.addField("data", STypeYearMonth.class, true);
        STypeString entidadeCertificacao = certificacao.addFieldString("entidade", true);
        STypeDate validadeCertificacao = certificacao.addFieldDate("validade");
        STypeString nomeCertificacao = certificacao.addFieldString("nome", true);
        {
            certificacoes
                    .withView(SViewListByTable::new)
                    .asAtr().label("Certificações");
            certificacao
                    .asAtr().label("Certificação");
            dataCertificacao
                    .asAtr().label("Data")
                    .asAtrBootstrap().colPreference(2);
            entidadeCertificacao
                    .asAtr().label("Entidade")
                    .asAtrBootstrap().colPreference(4);
            validadeCertificacao
                    .asAtr().label("Validade")
                    .asAtrBootstrap().colPreference(2);
            nomeCertificacao
                    .asAtr().label("Nome")
                    .asAtrBootstrap().colPreference(4);
        }
    }
}
