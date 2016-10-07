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

package org.opensingular.singular.form.showcase.component.form.custom.comment;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.type.country.brazil.STypeCEP;
import org.opensingular.form.type.country.brazil.STypeCPF;
import org.opensingular.form.view.SViewListByMasterDetail;
import org.opensingular.form.wicket.enums.AnnotationMode;
import org.opensingular.singular.form.showcase.component.CaseItem;
import org.opensingular.singular.form.showcase.component.Group;
import org.opensingular.singular.form.showcase.component.Resource;

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
