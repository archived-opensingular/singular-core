package br.net.mirante.singular.showcase.component.custom.comment;

import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.AtrBootstrap;
import br.net.mirante.singular.form.mform.core.annotation.AtrAnnotation;
import br.net.mirante.singular.form.mform.util.comuns.STypeEMail;

public class CaseAnnotationPackage extends SPackage {

    public STypeComposite<?> pedido, cliente, endereco, request, id;

    /*
     * Observe que as anotações só estão disponíveis quando devidamente configuradas no contexto.
     */

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        pedido = pb.createTipoComposto("testForm");
        pedido.as(AtrBasic::new).label("Pedido");


        id = pedido.addCampoComposto("id");
        id.asAtrBasic().label("Identificador");
        id.addCampoInteger("number");
        id.as(AtrAnnotation::new).setAnnotated();

        cliente = pedido.addCampoComposto("Cliente");
        cliente.asAtrBasic().label("Dados do Cliente");
        cliente.addCampoCPF("cpf").as(AtrBasic.class).label("CPF");
        cliente.addCampoEmail("email").as(AtrBasic.class).label("E-Mail");
        //@destacar
        cliente.as(AtrAnnotation::new).setAnnotated(); // Usará o rótulo do campo para a anotação
        cliente.as(AtrBootstrap::new).colPreference(6);

        endereco = pedido.addCampoComposto("Endereco");
        endereco.asAtrBasic().label("Endereco do Cliente");
        endereco.addCampoCEP("cep").as(AtrBasic.class).label("CEP");
        endereco.addCampoCEP("Logradouro").as(AtrBasic.class).label("Logradouro");
        endereco.as(AtrBootstrap::new).colPreference(6);

        request = pedido.addCampoComposto("request");
        request.asAtrBasic().label("Pedido");
        request.addCampoString("itens").asAtrBasic().label("Itens");
        request.addCampoString("obs").asAtrBasic().label("Observações");

        //@destacar
        request.as(AtrAnnotation::new).setAnnotated().label("Observações Finais"); //Permite definir seu pŕoprio rótulo
        super.carregarDefinicoes(pb);
    }
}
