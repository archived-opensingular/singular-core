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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class TestMPacoteCoreTipoFormula extends TestCaseForm {

    public TestMPacoteCoreTipoFormula(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testEmpty() {}
    /*
     * public void testCodeSimple() { MDicionario dictionary =
     * createTestDictionary(); PacoteBuilder pb =
     * dictionary.criarNovoPacote("teste");
     *
     * MTipoComposto<?> tipoCampos = pb.createTipoComposto("campos");
     * MTipoInteger tipoQtd = tipoCampos.addCampoInteger("qtd"); MTipoCode
     * tipoCalc = tipoCampos.addCode("calc", Supplier.class);
     *
     * MIComposto campos = tipoCampos.novaInstancia();
     *
     * assertNull(campos.getValue("calc"));
     *
     * campos.setValue("calc", 10); assertNull(campos.getValue("calc"));
     *
     * campos.setValue("calc", (Supplier<Integer>) () -> 10);
     * assertNotNull(campos.getValue("calc"));
     * assertTrue(campos.getValue("calc") instanceof Supplier);
     * assertEquals(campos.getValue("calc", Supplier.class).get(), 10); } public
     * void testMetodoSimples() { MDicionario dictionary = MDicionario.create();
     * PacoteBuilder pb = dictionary.criarNovoPacote("teste");
     *
     * MTipoComposto<?> tipoCampos = pb.createTipoComposto("campos");
     * MTipoInteger tipoQtd = tipoCampos.addCampoInteger("qtd"); MTipoCode
     * tipoCalc = tipoCampos.addMetodo("isNegativo", Boolean.class,
     * (Supplier<Boolean>) () -> false);
     *
     * MIComposto campos = tipoCampos.novaInstancia();
     *
     * assertNull(campos.getValue("isNegativo"));
     *
     * campos.setValue("calc", 10); assertNull(campos.getValue("calc"));
     *
     * assertNotNull(campos.getValue("calc"));
     * assertTrue(campos.getValue("calc") instanceof Supplier);
     * assertEquals(campos.getValue("calc", Supplier.class).get(), 10); }
     *
     * public void testBasicFunction() { MDicionario dictionary =
     * MDicionario.create(); PacoteBuilder pb =
     * dictionary.criarNovoPacote("teste");
     *
     * MTipoComposto<?> tipoCampos = pb.createTipoComposto("campos");
     * MTipoInteger tipoSimples = tipoCampos.addCampoInteger("simples");
     *
     * pb.debug();
     *
     * tipoSimples.asFormula().set(() -> 1 * 2); // tipoSimples.withFormula(()
     * -> 1 * 2); // tipoSimples.withFormulaJS("1 * 2");
     *
     * // tipoSimples.withValorInicial(valor)
     *
     * MIComposto campos = tipoCampos.novaInstancia(); campos.debug();
     * assertEquals(2, campos.getValue("simples")); }
     */
}
