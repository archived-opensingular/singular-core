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

package org.opensingular.form;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.io.SFormXMLUtil;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeHTML;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.internal.lib.commons.xml.MElement;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(Parameterized.class)
public class TestSInstance extends TestCaseForm {

    public TestSInstance(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test public void testIncrementoId() {
        PackageBuilder pb = createTestPackage();

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

        MElement xml = SFormXMLUtil.toXML(pedido).orElse(null);

        SIComposite pedido2 = (SIComposite) SFormXMLUtil.fromXML(tipoPedido, xml);
        assertId(pedido2, 1, 12);
        assertId(pedido2.getField("nome"), 13, 13);
        assertId(pedido2.getField("prioridade"), 8, 13);
        assertId(pedido2.getField("itens[0].urgente"), 12, 13);
        assertId(pedido2.getField("itens[0].nome"), 14, 14);
    }

    private static void assertId(SInstance pedido, int idInstance, int lastId) {
        Assert.assertEquals((Integer) idInstance, pedido.getId());
        Assert.assertEquals(lastId, pedido.getDocument().getLastId());
    }

    @Test
    public void getFieldOnSInstanceSimple() {
        STypeComposite<SIComposite> blockType = createTestPackage().createCompositeType("block");
        blockType.addFieldBoolean("a");
        assertException(() -> blockType.newInstance().getField("a.b"), SingularFormException.class,
                "Não suporta leitura de subCampos");
        assertException(() -> blockType.newInstance().getFieldOpt("a.b"), SingularFormException.class,
                "Não suporta leitura de subCampos");

    }

    @Test
    public void getValueOnSInstanceSimple() {
        STypeComposite<SIComposite> blockType = createTestPackage().createCompositeType("block");
        blockType.addFieldBoolean("a");

        SIComposite block = blockType.newInstance();
        assertException(() -> block.getValue("a.b", Object.class), SingularFormException.class,
                "Não se aplica um path a um tipo simples");

        block.setValue("a", Boolean.TRUE);
        assertException(() -> block.getValue("a.b", Object.class), SingularFormException.class,
                "Não suporta leitura de subCampos");

    }

    @Test
    public void testIsTypeOf() {
        SDictionary dic = createTestDictionary();
        SIString s = dic.getType(STypeString.class).newInstance();
        assertTrue(s.isTypeOf(dic.getType(STypeString.class)));
        assertTrue(s.isTypeOf(dic.getType(SType.class)));
        assertFalse(s.isTypeOf(dic.getType(STypeHTML.class)));
        assertFalse(s.isTypeOf(dic.getType(STypeList.class)));

        assertThatThrownBy(() -> s.isTypeOf(createTestDictionary().getType(STypeList.class)))
                .isExactlyInstanceOf(SingularFormException.class).hasMessageContaining(
                "foi criado em outro dicionário");
    }
}
