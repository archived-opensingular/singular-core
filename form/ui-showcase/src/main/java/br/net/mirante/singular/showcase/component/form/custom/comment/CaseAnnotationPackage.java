/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.form.custom.comment;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.type.basic.AtrBasic;
import br.net.mirante.singular.form.type.country.brazil.STypeCEP;
import br.net.mirante.singular.form.type.country.brazil.STypeCPF;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;
import br.net.mirante.singular.showcase.component.CaseItem;
import br.net.mirante.singular.showcase.component.Group;
import br.net.mirante.singular.showcase.component.Resource;

/**
 * Anotações e comentários associados a elementos de um form
 */
@CaseItem(componentName = "Annotation", group = Group.CUSTOM, annotation = AnnotationMode.EDIT,
resources = @Resource(PageWithAnnotation.class))
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
        request.addFieldString("itens").asAtr().label("Itens");
        request.addFieldString("obs").asAtr().label("Observações");

        //@destacar
        request.asAtrAnnotation().setAnnotated().label("Observações Finais"); //Permite definir seu pŕoprio rótulo
        super.onLoadPackage(pb);
    }
}
