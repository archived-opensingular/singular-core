package br.net.mirante.singular.form.mform;

import org.junit.Assert;
import org.junit.Test;

import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.io.MformPersistenciaXML;
import br.net.mirante.singular.form.util.xml.MElement;

public class TestMInstance {

    @Test public void testIncrementoId() {
        SDictionary dicionario = SDictionary.create();
        PackageBuilder pb = dicionario.createNewPackage("teste");

        STypeComposite<?> tipoPedido = pb.createCompositeType("pedido");
        tipoPedido.addFieldString("nome");
        tipoPedido.addFieldString("descr");
        tipoPedido.addFieldString("prioridade");
        tipoPedido.addFieldListOf("clientes", STypeString.class);
        STypeComposite<?> tipoItem = tipoPedido.addFieldListOfComposite("itens", "item").getElementsType();
        tipoItem.addFieldString("nome");
        tipoItem.addFieldBoolean("urgente");

        SIComposite pedido = tipoPedido.newInstance();
        assertId(pedido, 1, 1);
        assertId(pedido.getField("nome"), 2, 2);
        assertId(pedido.getField("descr"), 3, 3);
        assertId(pedido.getFieldList("clientes").addNew(c -> c.setValue("A")), 5, 5);
        assertId(pedido.getField("clientes"), 4, 5);
        assertId(pedido.getFieldList("clientes").addNew(c -> c.setValue("B")), 6, 6);

        pedido.getFieldList("clientes").remove(1);
        assertId(pedido.getFieldList("clientes").addNew(c -> c.setValue("C")), 7, 7);

        pedido.setValue("prioridade", "X");
        assertId(pedido.getField("prioridade"), 8, 8);

        assertId(pedido.getFieldList("itens"), 9, 9);
        assertId(pedido.getFieldList("itens").addNew(), 10, 10);
        assertId(pedido.getField("itens[0].nome"), 11, 11);
        pedido.setValue("itens[0].urgente", true);
        assertId(pedido.getField("itens[0].urgente"), 12, 12);

//        pedido.debug();
        MElement xml = MformPersistenciaXML.toXML(pedido);

        SIComposite pedido2 = (SIComposite) MformPersistenciaXML.fromXML(tipoPedido, xml);
        assertId(pedido2, 1, 12);
        assertId(pedido2.getField("nome"), 13, 13);
        assertId(pedido2.getField("prioridade"), 8, 13);
        assertId(pedido2.getField("itens[0].urgente"), 12, 13);
        assertId(pedido2.getField("itens[0].nome"), 14, 14);
    }

    private static void assertId(SInstance pedido, int idInstancia, int lastId) {
        Assert.assertEquals((Integer) idInstancia, pedido.getId());
        Assert.assertEquals(lastId, pedido.getDocument().getLastId());
    }
}
