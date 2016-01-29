package br.net.mirante.singular.showcase.component.custom.comment;

import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SList;
import br.net.mirante.singular.form.mform.SInstance2;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.core.annotation.SIAnnotation;
import br.net.mirante.singular.form.wicket.WicketBuildContext;

public class PageWithAnnotation {

    public void buildPage(WicketBuildContext ctx){
        /**
         * Deve-se habilitar as anotações no contexto sendo utilizado para montar a página.
         */
        ctx.enableAnnotation();
    }


    public void loadAnnotations(SIComposite pedido, SList<SIAnnotation> annotations){
        /**
         * Como as anotações são armazenadas de forma separada da instancia a qual faz referencia
         * esta deve ser carregada em conjunto para a exibição das anotações para edição.
         */
        pedido.as(AtrAnnotation::new).loadAnnotations(annotations);
    }

    public void saveAnnotations(SIComposite pedido){
        CaseAnnotationPackage pacote = (CaseAnnotationPackage) pedido.getMTipo().getPacote();

        /**
         * Anotações são armazenadas junto a cada campo onde a mesma está habilitada.
         */
        SInstance2 instanciaCampoCliente = pedido.getDescendant(pacote.cliente);
        SIAnnotation anotacaoDoCliente = instanciaCampoCliente.as(AtrAnnotation::new).annotation();

        /**
         * As anotações pordem ser persistidas separadamente ou de forma conjunta.
         */
        SList anotacoes = instanciaCampoCliente.as(AtrAnnotation::new).persistentAnnotations();
    }

}
