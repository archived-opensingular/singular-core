package br.net.mirante.singular.form.mform;

import br.net.mirante.singular.form.mform.io.FormSerializationUtil;
import org.junit.Assert;
import org.junit.Test;

import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MElement;

public class TestMInstance {

    @Test public void testIncrementoId() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoComposto<?> tipoPedido = pb.createTipoComposto("pedido");
        tipoPedido.addCampoString("nome");
        tipoPedido.addCampoString("descr");
        tipoPedido.addCampoString("prioridade");
        tipoPedido.addCampoListaOf("clientes", MTipoString.class);
        MTipoComposto<?> tipoItem = tipoPedido.addCampoListaOfComposto("itens", "item").getTipoElementos();
        tipoItem.addCampoString("nome");
        tipoItem.addCampoBoolean("urgente");

        MIComposto pedido = tipoPedido.novaInstancia();
        assertId(pedido, 1, 1);
        assertId(pedido.getCampo("nome"), 2, 2);
        assertId(pedido.getCampo("descr"), 3, 3);
        assertId(pedido.getFieldList("clientes").addNovo(c -> c.setValor("A")), 5, 5);
        assertId(pedido.getCampo("clientes"), 4, 5);
        assertId(pedido.getFieldList("clientes").addNovo(c -> c.setValor("B")), 6, 6);

        pedido.getFieldList("clientes").remove(1);
        assertId(pedido.getFieldList("clientes").addNovo(c -> c.setValor("C")), 7, 7);

        pedido.setValor("prioridade", "X");
        assertId(pedido.getCampo("prioridade"), 8, 8);

        assertId(pedido.getFieldList("itens"), 9, 9);
        assertId(pedido.getFieldList("itens").addNovo(), 10, 10);
        assertId(pedido.getCampo("itens[0].nome"), 11, 11);
        pedido.setValor("itens[0].urgente", true);
        assertId(pedido.getCampo("itens[0].urgente"), 12, 12);

//        pedido.debug();
        MElement xml = MformPersistenciaXML.toXML(pedido);

        MIComposto pedido2 = (MIComposto) MformPersistenciaXML.fromXML(tipoPedido, xml);
        assertId(pedido2, 1, 12);
        assertId(pedido2.getCampo("nome"), 13, 13);
        assertId(pedido2.getCampo("prioridade"), 8, 13);
        assertId(pedido2.getCampo("itens[0].urgente"), 12, 13);
        assertId(pedido2.getCampo("itens[0].nome"), 14, 14);
    }

    private static void assertId(MInstancia pedido, int idInstancia, int lastId) {
        Assert.assertEquals((Integer) idInstancia, pedido.getId());
        Assert.assertEquals(lastId, pedido.getDocument().getLastId());
    }

    @Test public void testSerialializeEmptyObject() {
        MDicionario dicionario = MDicionario.create();
        PacoteBuilder pb = dicionario.criarNovoPacote("teste");

        MTipoComposto<?> tipoPedido = pb.createTipoComposto("pedido");
        tipoPedido.addCampoString("nome");
        tipoPedido.addCampoString("descr");
        tipoPedido.addCampoString("prioridade");
        tipoPedido.addCampoListaOf("clientes", MTipoString.class);

        MIComposto instance = tipoPedido.novaInstancia();
        FormSerializationUtil.toInstance(FormSerializationUtil.toSerializedObject(instance));
    }

}
