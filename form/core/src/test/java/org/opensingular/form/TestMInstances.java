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

import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.form.SInstances.IVisitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestMInstances extends TestCaseForm {

    private SPackageTesteContatos pacote;

    public TestMInstances(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Before
    public void setUp() {
        pacote = createTestDictionary().loadPackage(SPackageTesteContatos.class);
    }

    @Test
    public void test() {
        SIComposite contato = pacote.contato.newInstance();

        SInstances.getDescendant(contato, pacote.nome).getValue();
        SInstances.listDescendants(contato, pacote.enderecoEstado).stream();

        Assert.assertTrue(SInstances.findCommonAncestor(contato, pacote.contato).get().getType() == pacote.contato);
    }

    @Test
    public void testVisit() {
        SIComposite contato = pacote.contato.newInstance();
        Assert.assertEquals(contato,
            SInstances.visit(contato, findFirst(it -> it.getType() == pacote.contato))
                .orElse(null));

        Assert.assertEquals(pacote.nome,
            SInstances.visit(contato, findFirst(it -> it.getType() == pacote.nome))
                .map(it -> it.getType())
                .orElse(null));
        Assert.assertEquals(pacote.sobrenome,
            SInstances.visit(contato, findFirst(it -> it.getType() == pacote.sobrenome))
                .map(it -> it.getType())
                .orElse(null));
    }

    @Test
    public void testVisitOrder() {
        SIComposite contato = pacote.contato.newInstance();
        Assert.assertEquals(pacote.identificacao,
            SInstances.visit(contato, findFirst(it -> it.getType() == pacote.nome || it.getType() == pacote.identificacao))
                .map(it -> it.getType())
                .orElse(null));
    }

    @Test
    public void testVisitPostOrder() {
        SIComposite contato = pacote.contato.newInstance();
        Assert.assertEquals(pacote.nome,
            SInstances.visitPostOrder(contato, findFirst(it -> it.getType() == pacote.nome || it.getType() == pacote.identificacao))
                .map(it -> it.getType())
                .orElse(null));
    }

    private static IVisitor<SInstance, SInstance> findFirst(IPredicate<SInstance> test) {
        return (i, v) -> {
            if (test.test(i))
                v.stop(i);
        };
    }
}
