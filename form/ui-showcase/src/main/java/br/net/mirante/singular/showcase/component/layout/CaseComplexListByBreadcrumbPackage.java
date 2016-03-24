/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.layout;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.SViewBreadcrumb;

public class CaseComplexListByBreadcrumbPackage extends SPackage {

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        STypeComposite<?> testForm = pb.createCompositeType("testForm");

        STypeList<STypeComposite<SIComposite>, SIComposite> componentes = testForm.addFieldListOfComposite("componentes", "componente");
        STypeComposite<SIComposite> componente = componentes.getElementsType();
        componente.asAtrBasic().label("Componente");

        componente.addFieldString("nome")
                .asAtrBasic().label("Nome");

        STypeList<STypeComposite<SIComposite>, SIComposite> testes = componente.addFieldListOfComposite("testes", "teste");
        testes
                .withView(SViewBreadcrumb::new)
                .asAtrBasic().label("Testes de componente");

        STypeComposite<SIComposite> teste = testes.getElementsType();
        teste.asAtrBasic().label("Teste de Componentes");

        teste.addFieldString("nome")
                .asAtrBasic().label("Nome");


        componentes
                .withView(SViewBreadcrumb::new)
                .asAtrBasic().label("Componentes");

    }
}
