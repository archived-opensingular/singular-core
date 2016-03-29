package br.net.mirante.singular.form.mform.basic.ui;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.GregorianCalendar;

import org.junit.Test;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.STypeComposite;

public class DisplayStringCalculationTest {

    private static SIComposite createPedido() {
        SDictionary dict = SDictionary.create();
        PackageBuilder pkt = dict.createNewPackage("teste");
        STypeComposite<SIComposite> tipoPedido = pkt.createCompositeType("pedido");
        tipoPedido.addFieldInteger("cod");
        tipoPedido.addFieldString("nome");
        tipoPedido.addFieldInteger("qtd");
        tipoPedido.addFieldInteger("peso");
        tipoPedido.addFieldDate("entrega");

        STypeComposite<SIComposite> tipoDetalhes = tipoPedido.addFieldComposite("detalhes");
        tipoDetalhes.addFieldBoolean("fragil");
        tipoDetalhes.addFieldString("obs");

        SIComposite pedido = tipoPedido.newInstance();
        pedido.setValue("cod", 10);
        pedido.setValue("nome", "Teclado USB");
        pedido.setValue("qtd", 4);
        pedido.setValue("entrega", new GregorianCalendar(2000, 6, 1).getTime());
        pedido.setValue("detalhes.fragil", true);
        pedido.setValue("detalhes.obs", "usado");
        return pedido;
    }

    @Test
    public void testDisplayStringTypeSimpleDefault() {
        SIComposite pedido = createPedido();

        assertThat(pedido.getField("cod").toStringDisplay()).isEqualTo("10");
        assertThat(pedido.getField("peso").toStringDisplay()).isNull();
        assertThat(pedido.toStringDisplay()).isNull();
    }


    @Test
    public void testDisplayStringTypeSimpleCustomStatic() {
        SIComposite pedido = createPedido();
        pedido.getType().getField("cod").asAtrBasic().displayString("xxxx");
        pedido.getType().asAtrBasic().displayString("yyyy");

        assertThat(pedido.toStringDisplay()).isEqualTo("yyyy");
        assertThat(pedido.getField("peso").toStringDisplay()).isNull();
        assertThat(pedido.getField("cod").toStringDisplay()).isEqualTo("xxxx");
    }

    @Test
    public void testDisplayStringWithCondition() {
        SIComposite pedido = createPedido();
        pedido.getType().asAtrBasic().displayString("${cod}-${nome}<#if detalhes.fragil>-CUIDADO</#if>");
        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB-CUIDADO");

        pedido.setValue("detalhes.fragil", false);
        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB");

        pedido.getType().asAtrBasic().displayString("${cod}-${nome}<#if qtd <= 2>-POUCO</#if>");
        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB");
        pedido.setValue("qtd", 2);
        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB-POUCO");
    }

    @Test
    public void testDisplayStringLambdaDinamicCalculation() {
        SIComposite pedido = createPedido();

        pedido.getType().asAtrBasic().displayString(ctx -> {
            SIComposite p = (SIComposite) ctx.instance();
            return p.getField("cod").toStringDisplay() + "-" + p.getField("nome").toStringDisplay();
        });

        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB");
    }

    @Test
    public void testDisplayStringTypeSimpleCustomDinamicCalculation() {
        SIComposite pedido = createPedido();
        pedido.getType().asAtrBasic().displayString("${cod}-${nome} (${qtd})");
        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB (4)");

        pedido.getType().getField("detalhes").asAtrBasic().displayString("(${fragil})");
        pedido.getType().asAtrBasic().displayString("${cod}-${detalhes}");
        assertThat(pedido.toStringDisplay()).isEqualTo("10-(Sim)");

        pedido.getType().asAtrBasic().displayString("${cod}-${detalhes.toStringDisplayDefault()!\"x\"}");
        assertThat(pedido.toStringDisplay()).isEqualTo("10-x");

        pedido.getType().getField("cod").asAtrBasic().displayString("xxxx");
        pedido.getType().getField("qtd").asAtrBasic().displayString("x${toStringDisplayDefault()}");
        // pedido.getType().getField("peso").asAtrBasic().displayString("${peso}");

        assertThat(pedido.getField("cod").toStringDisplay()).isEqualTo("xxxx");
        assertThat(pedido.getField("qtd").toStringDisplay()).isEqualTo("x4");
        assertThat(pedido.getField("peso").toStringDisplay()).isNull();

        pedido.getType().asAtrBasic().displayString("${cod}-${nome} (${qtd})");
        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB (4)");

        pedido.getType().asAtrBasic().displayString("${cod}-${nome}-${entrega}");
        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB-01/07/2000");
    }

    @Test
    public void testDisplayStringDinamicCalculationWithDate() {
        SIComposite pedido = createPedido();

        pedido.getType().asAtrBasic().displayString("${cod}-${nome}-${entrega}");
        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB-01/07/2000");

        pedido.getType().asAtrBasic().displayString("${qtd?string.percent}-${nome}");
        assertThat(pedido.toStringDisplay()).isEqualTo("400%-Teclado USB");

        pedido.getType().asAtrBasic().displayString("${cod} ${nome} ${entrega?string.iso}");
        assertThat(pedido.toStringDisplay()).isEqualTo("10 Teclado USB 2000-07-01");
    }
}
