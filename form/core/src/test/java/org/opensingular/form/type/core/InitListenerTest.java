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

package org.opensingular.form.type.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.helpers.AssertionsSInstance;
import org.opensingular.form.io.TestFormSerializationUtil;
import org.opensingular.lib.commons.context.RefService;

import java.io.Serializable;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class InitListenerTest extends TestCaseForm {

    public InitListenerTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void runInitializationCode() {
        STypeComposite<SIComposite> base = createTestPackage().createCompositeType("base");
        STypeString field1 = base.addFieldString("field1");
        STypeString field2 = base.addFieldString("field2");

        field1.withInitListener(x -> x.setValue("abacate"));
        assertInstance(newInstance(field1)).isValueEquals("abacate");
    }

    @Test
    public void initializationCodeHasAccessToAllServices() {
        STypeComposite<SIComposite> base = createTestPackage().createCompositeType("base");
        STypeString field1 = base.addFieldString("field1");
        STypeString field2 = base.addFieldString("field2");

        field1.withInitListener((x) -> {
            assertThat(x.getDocument()).isNotNull();
            assertThat(x.getDocument().lookupLocalService(P.class).orElse(null)).isNotNull();
            x.setValue("abacate");
        });

        assertInstance(newInstance(field1)).isValueEquals("abacate");
    }

    @Test
    public void initializationIsRunnedForAllInstances() {
        STypeComposite<SIComposite> base = createTestPackage().createCompositeType("base");
        STypeString field1 = base.addFieldString("field1");
        STypeString field2 = base.addFieldString("field2");

        field1.withInitListener(x -> x.setValue("abacate"));
        field2.withInitListener(x -> x.setValue("avocado"));

        assertInstance(newInstance(base))
                .isValueEquals(field1, "abacate").isValueEquals(field2,"avocado");
    }

    private static class P implements Serializable {

    }

    private SInstance newInstance(SType t) {
        return SDocumentFactory.empty()
                .extendAddingSetupStep(document -> document.bindLocalService("test", P.class, RefService.of(new P())))
                .createInstance(RefType.of(() -> t));
    }

    @Test
    public void testIfSimpleInitWithValue() {
        PackageBuilder pb   = createTestPackage();
        STypeString    nome = pb.createType("nome", STypeString.class);

        nome.withInitListener(si -> si.setValue("banana"));

        assertInstance(nome.newInstance()).isValueEquals("banana");

        //Tipo extendido deve manter a inicialização
        STypeString childName = pb.createType("nomeFilho", nome);
        assertInstance(childName.newInstance()).isValueEquals("banana");
    }

    @Test
    public void testIfCompositeInitWithValue() {
        PackageBuilder              pb   = createTestPackage();
        STypeComposite<SIComposite> root = pb.createCompositeType("root");
        STypeString                 nome = root.addFieldString("nome");
        root.addFieldString("origem");

        nome.withInitListener(si -> si.setValue("banana"));
        root.withInitListener(c -> c.setValue("origem", "desconhecida"));

        assertInstance(root.newInstance())
                .isValueEquals("nome", "banana")
                .isValueEquals("origem", "desconhecida");

        //Tipo extendido deve manter a inicialização original
        STypeComposite<SIComposite> child = pb.createType("Filho", root);
        assertInstance(child.newInstance())
                .isValueEquals("nome", "banana")
                .isValueEquals("origem", "desconhecida");
    }

    @Test
    public void testListOfCompositeInitWithValue() {

        PackageBuilder                                      pb     = createTestPackage();
        STypeComposite<SIComposite>                         root   = pb.createCompositeType("root");
        STypeList<STypeComposite<SIComposite>, SIComposite> frutas = root.addFieldListOfComposite("frutas", "fruta");
        STypeComposite<SIComposite>                         fruta  = frutas.getElementsType();

        fruta.addField("nome", STypeString.class)
                .withInitListener(si -> si.setValue("banana"));
        fruta.addField("origem", STypeString.class);

        fruta.withInitListener(c -> c.setValue("origem", "desconhecida"));


        SIList<SIComposite> iFrutas = root.newInstance().getFieldList("frutas", SIComposite.class);
        iFrutas.addNew();

        assertInstance(iFrutas)
                .isValueEquals("[0].nome", "banana")
                .isValueEquals("[0].origem", "desconhecida");
    }

    @Test
    public void testIfListOfSimpleInitWithValue() {
        PackageBuilder pb = createTestPackage();
        STypeList<STypeString, SIString> frutasType = pb.createListTypeOf("frutas", STypeString.class);

        frutasType.getElementsType().withInitListener(si -> si.setValue("banana"));

        SIList<SIString> frutas = frutasType.newInstance();
        frutas.addNew();
        frutas.addValue("manga");

        AssertionsSInstance aFrutas = assertInstance(frutas);
        aFrutas.isValueEquals("[0]", "banana");
        aFrutas.isValueEquals("[1]", "manga");

        //O init da string filha não pode afeta o comportamento STypeString
        assertInstance(pb.getDictionary().getType(STypeString.class).newInstance()).isValueNull();
    }

    private static int counterInitCalls;

    @Test
    public void testInitListenerMustNotBeExecutedDuringDeserialization() {
        counterInitCalls = 0;
        SIComposite instance = (SIComposite) createSerializableTestInstance("teste.pedido", pacote -> {
            STypeComposite<?> tipoPedido = pacote.createCompositeType("pedido");
            tipoPedido.addFieldString("nome");
            tipoPedido.addFieldString("descr");
            tipoPedido.addFieldString("prioridade").withInitListener(i -> {
                counterInitCalls++;
                i.setValue("alta");
            });
            tipoPedido.withInitListener(p -> {
                counterInitCalls++;
                p.setValue("descr", "x");
            });
            STypeComposite<SIComposite> tipoItem = tipoPedido.addFieldListOfComposite("itens", "item")
                    .getElementsType();
            tipoItem.addFieldString("nome");
            tipoItem.addFieldInteger("qtd").withInitListener(i -> {
                counterInitCalls++;
                i.setValue(1);
            });
            tipoItem.withInitListener(i -> {
                counterInitCalls++;
                i.setValue("nome", "w");
            });


        });

        assertEquals(2, counterInitCalls);
        SIComposite item = instance.getFieldList("itens", SIComposite.class).addNew();
        assertEquals(4, counterInitCalls);

        assertInstance(instance)
                .isValueEquals("nome", null)
                .isValueEquals("descr", "x")
                .isValueEquals("prioridade", "alta")
                .isValueEquals("itens[0].nome", "w")
                .isValueEquals("itens[0].qtd", 1);

        instance.setValue("descr","y");
        instance.setValue("prioridade", "baixa");
        item.setValue("nome", "k");
        item.setValue("qtd", 2);

        SInstance instance2 = TestFormSerializationUtil.testSerialization(instance);
        assertEquals(4, counterInitCalls);

        assertInstance(instance2)
                .isNotSameAs(instance)
                .isValueEquals("nome", null)
                .isValueEquals("descr", "y")
                .isValueEquals("prioridade", "baixa")
                .isValueEquals("itens[0].nome", "k")
                .isValueEquals("itens[0].qtd", 2);
    }

}
