package br.net.mirante.singular.showcase.component.custom.comment;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.util.comuns.MTipoCPF;

public class CaseAnnotationPackage extends MPacote {

    public MTipoComposto<?> pedido, cliente, endereco, request;

    /*
     * Observe que as anotações só estão disponíveis quando devidamente configuradas no contexto.
     */

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        pedido = pb.createTipoComposto("testForm");
        pedido.as(AtrBasic::new).label("Pedido");

        cliente = pedido.addCampoComposto("Cliente");
        cliente.asAtrBasic().label("Dados do Cliente");
        cliente.addCampoCPF("cpf").as(AtrBasic.class).label("CPF");
        cliente.addCampoEmail("email").as(AtrBasic.class).label("E-Mail");
        //@destacar
        cliente.as(AtrAnnotation::new).setAnnotated(); // Usará o rótulo do campo para a anotação

        endereco = pedido.addCampoComposto("Endereco");
        endereco.asAtrBasic().label("Endereco do Cliente");
        endereco.addCampoCEP("cep").as(AtrBasic.class).label("CEP");
        endereco.addCampoCEP("Logradouro").as(AtrBasic.class).label("Logradouro");

        request = pedido.addCampoComposto("request");
        request.asAtrBasic().label("Pedido");
        request.addCampoString("itens").asAtrBasic().label("Itens");
        request.addCampoString("obs").asAtrBasic().label("Observações");

        //@destacar
        request.as(AtrAnnotation::new).setAnnotated().label("Observações Finais"); //Permite definir seu pŕoprio rótulo
        super.carregarDefinicoes(pb);
    }
}
