package br.net.mirante.singular.showcase.component.custom.comment;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.core.annotation.MIAnnotation;

public class PageWithAnnotation {

    public void saveAnnotations(MIComposto pedido){
        CaseAnnotationPackage pacote = (CaseAnnotationPackage) pedido.getMTipo().getPacote();

        /**
         * Anotações são armazenadas junto a cada campo onde a mesma está habilitada.
         */
        MInstancia instanciaCampoCliente = pedido.getDescendant(pacote.cliente);
        MIAnnotation anotacaoDoCliente = instanciaCampoCliente.as(AtrAnnotation::new).annotation();

        /**
         * As anotações pordem ser persistidas separadamente ou de forma conjunta.
         */
        MILista anotacoes = instanciaCampoCliente.as(AtrAnnotation::new).persistentAnnotations();
    }

    public void loadAnnotations(MIComposto pedido, MILista<MIAnnotation> annotations){
        /**
         * Como as anotações são armazenadas de forma separada da instancia a qual faz referencia
         * esta deve ser carregada em conjunto para a exibição das anotações para edição.
         */
        pedido.as(AtrAnnotation::new).loadAnnotations(annotations);
    }
}
