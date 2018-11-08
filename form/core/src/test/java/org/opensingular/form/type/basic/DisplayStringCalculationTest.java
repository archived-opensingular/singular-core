/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form.type.basic;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TestCaseForm;

import java.util.GregorianCalendar;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class DisplayStringCalculationTest extends TestCaseForm {

    public DisplayStringCalculationTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    private SIComposite createPedido() {
        PackageBuilder              pkt = createTestPackage();
        STypeComposite<SIComposite> tipoPedido = pkt.createCompositeType("pedido");
        tipoPedido.addFieldInteger("cod");
        tipoPedido.addFieldString("nome");
        tipoPedido.addFieldInteger("qtd");
        tipoPedido.addFieldInteger("peso");
        tipoPedido.addFieldDate("entrega");

        STypeComposite<SIComposite> tipoDetalhes = tipoPedido.addFieldComposite("detalhes");
        tipoDetalhes.addFieldBoolean("fragil");
        tipoDetalhes.addFieldString("obs");

        STypeList<STypeComposite<SIComposite>, SIComposite> listaQualquerCoisa = tipoPedido.addFieldListOfComposite("coisas", "qualquerCoisa");
        listaQualquerCoisa.getElementsType().addFieldString("nome");
        listaQualquerCoisa.getElementsType().addFieldString("identificador");
        listaQualquerCoisa.getElementsType().asAtr().displayString("${nome}");

        SIComposite pedido = tipoPedido.newInstance();
        pedido.setValue("cod", 10);
        pedido.setValue("nome", "Teclado USB");
        pedido.setValue("qtd", 4);
        pedido.setValue("entrega", new GregorianCalendar(2000, 6, 1).getTime());
        pedido.setValue("detalhes.fragil", true);
        pedido.setValue("detalhes.obs", "usado");
        SIComposite composite = pedido.getField(listaQualquerCoisa).addNew();
        composite.setValue("nome", "supernome");
        composite.setValue("identificador", "1");

        SIComposite composite2 = pedido.getField(listaQualquerCoisa).addNew();
        composite2.setValue("nome", "supernome2");
        composite2.setValue("identificador", "2");
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
        pedido.getType().getField("cod").asAtr().displayString("xxxx");
        pedido.getType().asAtr().displayString("yyyy");

        assertThat(pedido.toStringDisplay()).isEqualTo("yyyy");
        assertThat(pedido.getField("peso").toStringDisplay()).isNull();
        assertThat(pedido.getField("cod").toStringDisplay()).isEqualTo("xxxx");
    }

    @Test
    public void testDisplayStringWithCondition() {
        SIComposite pedido = createPedido();
        pedido.getType().asAtr().displayString("${cod}-${nome}<#if detalhes.fragil>-CUIDADO</#if>");
        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB-CUIDADO");

        pedido.setValue("detalhes.fragil", false);
        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB");

        pedido.getType().asAtr().displayString("${cod}-${nome}<#if qtd <= 2>-POUCO</#if>");
        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB");
        pedido.setValue("qtd", 2);
        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB-POUCO");
    }

    @Test
    public void testDisplayStringLambdaDinamicCalculation() {
        SIComposite pedido = createPedido();

        pedido.getType().asAtr().displayString(ctx -> {
            SIComposite p = (SIComposite) ctx.instanceContext();
            return p.getField("cod").toStringDisplay() + "-" + p.getField("nome").toStringDisplay();
        });

        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB");
    }

    @Test
    public void testDisplayStringTypeSimpleCustomDinamicCalculation() {
        SIComposite pedido = createPedido();
        pedido.getType().asAtr().displayString("${cod}-${nome} (${qtd})");
        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB (4)");

        pedido.getType().getField("detalhes").asAtr().displayString("(${fragil})");
        pedido.getType().asAtr().displayString("${cod}-${detalhes}");
        assertThat(pedido.toStringDisplay()).isEqualTo("10-(Sim)");

        pedido.getType().asAtr().displayString("${cod}-${detalhes.toStringDisplayDefault()!\"x\"}");
        assertThat(pedido.toStringDisplay()).isEqualTo("10-x");

        pedido.getType().getField("cod").asAtr().displayString("xxxx");
        pedido.getType().getField("qtd").asAtr().displayString("x${toStringDisplayDefault()}");
        // pedido.getType().getField("peso").asAtrBasic().displayString("${peso}");

        assertThat(pedido.getField("cod").toStringDisplay()).isEqualTo("xxxx");
        assertThat(pedido.getField("qtd").toStringDisplay()).isEqualTo("x4");
        assertThat(pedido.getField("peso").toStringDisplay()).isNull();

        pedido.getType().asAtr().displayString("${cod}-${nome} (${qtd})");
        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB (4)");

        pedido.getType().asAtr().displayString("${cod}-${nome}-${entrega}");
        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB-01/07/2000");
    }

    @Test
    public void testDisplayStringDinamicCalculationWithDate() {
        SIComposite pedido = createPedido();

        pedido.getType().asAtr().displayString("${cod}-${nome}-${entrega}");
        assertThat(pedido.toStringDisplay()).isEqualTo("10-Teclado USB-01/07/2000");

        pedido.getType().asAtr().displayString("${qtd?string.percent}-${nome}");
        assertThat(pedido.toStringDisplay()).isEqualTo("400%-Teclado USB");

        pedido.getType().asAtr().displayString("${cod} ${nome} ${entrega?string.iso}");
        assertThat(pedido.toStringDisplay()).isEqualTo("10 Teclado USB 2000-07-01");
    }

    @Test
    public void testDisplayStringListOfComposite() {
        SIComposite pedido = createPedido();
        Assert.assertEquals("supernome, supernome2",pedido.getField("coisas").toStringDisplay());
    }


    @Test
    public void testDisplayStringEmptyListOfComposite() {
        SIComposite pedido = createPedido();
        pedido.getField("coisas").clearInstance();
        Assert.assertEquals("",pedido.getField("coisas").toStringDisplay());
    }
}
