/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.component.form.custom.comment;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.core.annotation.SIAnnotation;
import org.opensingular.singular.form.wicket.WicketBuildContext;
import org.opensingular.singular.form.wicket.enums.AnnotationMode;

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
