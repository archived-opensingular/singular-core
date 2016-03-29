/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component.custom.comment;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.core.annotation.SIAnnotation;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.AnnotationMode;

public class PageWithAnnotation {

    public void buildPage(WicketBuildContext ctx){
        /**
         * Deve-se habilitar as anotações no contexto sendo utilizado
         * para montar a página.
         * Este pode ser tanto em modo de leitura como em modo de escrita
         */
        ctx.annotation(AnnotationMode.EDIT);
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
