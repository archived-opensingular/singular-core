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

import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.core.annotation.SIAnnotation;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.enums.AnnotationMode;

public class PageWithAnnotation {

    public void buildPage(WicketBuildContext ctx){
        /**
         * Deve-se habilitar as anotações no contexto sendo utilizado
         * para montar a página.
         * Este pode ser tanto em modo de leitura como em modo de escrita
         */
        ctx.setAnnotationMode(AnnotationMode.EDIT);
    }


    public void loadAnnotations(SIComposite pedido, SIList<SIAnnotation> annotations){
        /**
         * Como as anotações são armazenadas de forma separada da instancia a qual faz referencia
         * esta deve ser carregada em conjunto para a exibição das anotações para edição.
         */
        pedido.asAtrAnnotation().loadAnnotations(annotations);
    }

    public void saveAnnotations(SIComposite pedido){
        CaseAnnotationPackage pacote = (CaseAnnotationPackage) pedido.getType().getPackage();

        /**
         * Anotações são armazenadas junto a cada campo onde a mesma está habilitada.
         */
        SInstance instanciaCampoCliente = pedido.getDescendant(pacote.cliente);
        SIAnnotation anotacaoDoCliente = instanciaCampoCliente.asAtrAnnotation().annotation();

        /**
         * As anotações pordem ser persistidas separadamente ou de forma conjunta.
         */
        SIList anotacoes = instanciaCampoCliente.asAtrAnnotation().persistentAnnotations();
    }

}
