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
import org.opensingular.form.type.core.SIString;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RunWith(Parameterized.class)
public class TestMInstanciaDescendants extends TestCaseForm {

    public TestMInstanciaDescendants(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void test() {
        SPackageTesteContatos pacote = createTestDictionary().loadPackage(SPackageTesteContatos.class);

        SIComposite contato = pacote.contato.newInstance();

        contato.getDescendant(pacote.nome).setValue("Fulano");
        contato.getDescendant(pacote.sobrenome).setValue("de Tal");

        SIComposite endereco = contato.getDescendant(pacote.enderecos).addNew();
        endereco.getDescendant(pacote.enderecoLogradouro).setValue("QI 25");
        endereco.getDescendant(pacote.enderecoComplemento).setValue("Bloco G");
        endereco.getDescendant(pacote.enderecoNumero).setValue(402);
        endereco.getDescendant(pacote.enderecoCidade).setValue("Guará II");
        endereco.getDescendant(pacote.enderecoEstado).setValue("DF");

        SIList<SIString> telefones = contato.getDescendant(pacote.telefones);
        telefones.addValue("(61) 8888-8888");
        telefones.addValue("(61) 9999-8888");
        telefones.addValue("(61) 9999-9999");

        SIList<SIString> emails = contato.getDescendant(pacote.emails);
        emails.addValue("fulano@detal.com");

        Assert.assertEquals(
            Arrays.asList(
                    "(61) 8888-8888",
                    "(61) 9999-8888",
                    "(61) 9999-9999"),
            contato.listDescendantValues(pacote.telefones.getElementsType(), String.class));
    }

    @Test
    public void testList() {
        SPackageTesteContatos pacote = createTestDictionary().loadPackage(SPackageTesteContatos.class);

        SIComposite contato = pacote.contato.newInstance();

        for (int i = 0; i < 4; i++) {
            SIComposite endereco = (SIComposite) contato.getDescendant(pacote.enderecos).addNew();
            endereco.getDescendant(pacote.enderecoNumero).setValue(Integer.valueOf(i));
        }

        Assert.assertEquals(
            Arrays.asList(0, 1, 2, 3),
            contato.listDescendantValues(pacote.enderecoNumero, Integer.class));

        for (SInstance cid : contato.listDescendants(pacote.enderecoCidade))
            cid.setValue("C" + cid.getAncestor(pacote.endereco).getDescendant(pacote.enderecoNumero).getValue());

        Assert.assertEquals(
            Arrays.asList("C0", "C1", "C2", "C3"),
            contato.listDescendantValues(pacote.enderecoCidade, String.class));
    }

    @Test
    public void testIncorrectAncestor() {
        SPackageTesteContatos pacote = createTestDictionary().loadPackage(SPackageTesteContatos.class);
        SIComposite contato = pacote.contato.newInstance();

        Assert.assertFalse(contato.getDescendant(pacote.telefones).findAncestor(pacote.enderecos).isPresent());
        Assert.assertFalse(contato.getDescendant(pacote.enderecos).findAncestor(pacote.endereco).isPresent());
    }

    @Test
    public void testIncorrectDescendant() {
        SPackageTesteContatos pac = createTestDictionary().loadPackage(SPackageTesteContatos.class);
        SIComposite contato = pac.contato.newInstance();

        Assert.assertFalse(contato.getDescendant(pac.telefones).findDescendant(pac.endereco).isPresent());
        Assert.assertFalse(contato.getDescendant(pac.enderecos).findDescendant(pac.telefones).isPresent());

        contato.getDescendant(pac.enderecos).addNew();
        Assert.assertFalse(contato.getDescendant(pac.endereco).findDescendant(pac.emails).isPresent());
    }

    @Test
    public void testStream() {
        SPackageTesteContatos pacote = createTestDictionary().loadPackage(SPackageTesteContatos.class);

        Set<SType<?>> types = new HashSet<>(Arrays.asList(
            pacote.contato,
            pacote.identificacao,
            pacote.nome,
            pacote.sobrenome,
            pacote.enderecos,
            pacote.endereco,
            pacote.enderecoLogradouro,
            pacote.enderecoNumero,
            pacote.enderecoComplemento,
            pacote.enderecoCidade,
            pacote.enderecoEstado,
            pacote.telefones,
            pacote.telefone,
            pacote.emails,
            pacote.email));

        SIComposite contato = pacote.contato.newInstance();
        contato.getDescendant(pacote.enderecos).addNew();
        contato.getDescendant(pacote.telefones).addNew();
        contato.getDescendant(pacote.emails).addNew();

        contato.streamDescendants(true)
            .forEachOrdered(instance -> Assert.assertTrue(
"Tipo não encontrado: " + instance.getType(),
                types.remove(instance.getType())));

        Assert.assertTrue("Não percorreu o(s) tipo(s) " + types, types.isEmpty());
    }
}
