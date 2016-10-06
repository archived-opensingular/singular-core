/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.component.form.custom.comment;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.country.brazil.STypeCEP;
import org.opensingular.form.type.country.brazil.STypeCPF;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.singular.form.wicket.enums.AnnotationMode;
import org.opensingular.singular.showcase.component.CaseItem;
import org.opensingular.singular.showcase.component.Group;
import org.opensingular.singular.showcase.component.Resource;

/**
 * Anotações e comentários associados a elementos de um form
 */
@CaseItem(componentName = "Annotation", group = Group.CUSTOM, annotation = AnnotationMode.EDIT, resources = @Resource(PageWithAnnotation.class))
public class CaseAnnotationPackage extends SPackage {

    public STypeComposite<?> pedido, cliente, endereco, request, id;

    /*
     * Observe que as anotações só estão disponíveis quando devidamente configuradas no
     * contexto.
     */

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pedido = pb.createCompositeType("testForm");
        pedido.asAtr().label("Pedido");

        id = pedido.addFieldComposite("id");
        id.asAtr().label("Identificador");
        id.addFieldInteger("numero");
        id.asAtrAnnotation().setAnnotated();

        cliente = pedido.addFieldComposite("Cliente");
        cliente.asAtr().label("Dados do Cliente");
        cliente.addField("cpf", STypeCPF.class);
        cliente.addFieldEmail("email").asAtr().label("E-Mail");
        //@destacar
        cliente.asAtrAnnotation().setAnnotated(); // Usará o rótulo do campo para a anotação
        cliente.asAtrBootstrap().colPreference(6);

        endereco = pedido.addFieldComposite("Endereco");
        endereco.asAtr().label("Endereco do Cliente");
        endereco.addField("cep", STypeCEP.class);
        endereco.addFieldString("Logradouro").asAtr().label("Logradouro");
        endereco.asAtrBootstrap().colPreference(6);

        request = pedido.addFieldComposite("request");
        request.asAtr().label("Pedido");
        STypeList<STypeComposite<SIComposite>, SIComposite> itens = request.addFieldListOfComposite("itens", "item");
        itens.asAtr().label("Itens");

        STypeComposite<SIComposite> item = itens.getElementsType();
        item.addFieldString("descricao").asAtr().label("Descrição");
        item.addFieldString("obs").asAtr().label("Observações");
        item.asAtrAnnotation().setAnnotated();

        itens.setView(() -> new SViewListByMasterDetail());

        //@destacar
        request.asAtrAnnotation().setAnnotated().label("Observações Finais"); //Permite definir seu pŕoprio rótulo
        super.onLoadPackage(pb);
    }
}
