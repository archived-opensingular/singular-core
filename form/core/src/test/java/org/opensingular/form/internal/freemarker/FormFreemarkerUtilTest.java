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

package org.opensingular.form.internal.freemarker;

import org.assertj.core.api.AbstractCharSequenceAssert;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.type.core.STypeString;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class FormFreemarkerUtilTest extends TestCaseForm {

    private STypeComposite<? extends SIComposite>               curriculoType;
    private STypeComposite<SIComposite> dataType;
    private STypeList<STypeComposite<SIComposite>, SIComposite> certificadosType;

    public FormFreemarkerUtilTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Before
    public void setUp() {
        PackageBuilder pkt = createTestPackage();
        curriculoType = pkt.createCompositeType("curriculo");

        dataType = curriculoType.addFieldComposite("dados");
        dataType.addFieldString("nome");
        dataType.addFieldInteger("idade");
        dataType.addFieldString("time");

        certificadosType = curriculoType.addFieldListOfComposite("certificados", "certificado");
        STypeComposite<SIComposite> certificadoType = certificadosType.getElementsType();
        certificadoType.addFieldString("nome");
        certificadoType.addFieldDate("data");
    }

    @Test
    public void compositeSimpleTest() {
        SIComposite curriculo = curriculoType.newInstance();
        curriculo.setValue("dados.nome", "Paulo Silva");
        curriculo.setValue("dados.idade", 20);

        SIComposite certificado = (SIComposite) curriculo.getFieldList("certificados").addNew();
        certificado.setValue("nome", "Java");
        certificado.setValue("data", new Date(2016 - 1900, 3 - 1, 10));

        certificado = (SIComposite) curriculo.getFieldList("certificados").addNew();
        certificado.setValue("nome", "Oracle");

        assertMerge(curriculo, "dados", "Nome: ${nome}").isEqualTo("Nome: Paulo Silva");
        assertMerge(curriculo, "dados", "Idade: ${idade}").isEqualTo("Idade: 20");
        assertMerge(curriculo, "dados", "Time: ${time!}").isEqualTo("Time: ");

        assertMerge(curriculo, null, "Nome: ${dados.nome}").isEqualTo("Nome: Paulo Silva");
        assertMerge(curriculo, null, "Idade: ${dados.idade}").isEqualTo("Idade: 20");
        assertMerge(curriculo, null, "Idade: ${dados.idade.value()}").isEqualTo("Idade: 20");
        assertMerge(curriculo, null, "Time: ${dados.time!\"não informado\"}").isEqualTo("Time: não informado");
        assertMerge(curriculo, null, "Time: <#if dados.time??>${dados.time._inst.value()!\"não informado\"}<#else>não informado</#if>").isEqualTo("Time: não informado");

        assertMerge(curriculo, "dados.idade", "Idade: ${_inst}").isEqualTo("Idade: 20");
        assertMerge(curriculo, "dados.idade", "Idade: ${toStringDisplayDefault()}").isEqualTo("Idade: 20");
        assertMerge(curriculo, "dados.idade", "Idade: ${toStringDisplay()}").isEqualTo("Idade: 20");

        assertMerge(curriculo, null, "C1: ${certificados[0].nome}").isEqualTo("C1: Java");
        assertMerge(curriculo, null, "C1: ${certificados[0].data}").isEqualTo("C1: 10/03/2016");
        assertMerge(curriculo, "certificados[0].data", "C1: ${_inst?string.iso}").isEqualTo("C1: 2016-03-10");
        assertMerge(curriculo, "certificados[0].data", "C1: ${toStringDisplayDefault()}").isEqualTo("C1: 10/03/2016");

        assertMerge(curriculo, null, "<#list certificados as c>${c.nome};</#list>").isEqualTo("Java;Oracle;");
        assertMerge(curriculo, "certificados", "<#list _inst as c>${c.nome};</#list>").isEqualTo("Java;Oracle;");
    }

    private static AbstractCharSequenceAssert<?, String> assertMerge(SIComposite composite, String path, String templateString) {
        SInstance instance = path == null ? composite : composite.getField(path);
        return Assertions.assertThat(FormFreemarkerUtil.get().merge(instance, templateString));
    }

    @Test
    public void testMixedFieldsWithMethods() {
        PackageBuilder pkt = createTestPackage();
        STypeComposite<? extends SIComposite> recordType = pkt.createCompositeType("record");
        STypeString                           nameType   = recordType.addFieldString("name");
        recordType.addFieldString("value");
        recordType.addFieldString("toStringDisplayDefault");

        // recordType.asAtrBasic().displayString("xpto");

        SIComposite instance = recordType.newInstance();
        instance.setValue("name", "A");
        instance.setValue("value", "B");
        instance.setValue("toStringDisplayDefault", "C");

        assertMerge(instance, null, "${name}").isEqualTo("A");
        assertMerge(instance, null, "${value}").isEqualTo("B");
        assertMerge(instance, null, "${toStringDisplayDefault}").isEqualTo("C");

        assertMerge(instance, null, "${_inst.name}").isEqualTo("A");
        assertMerge(instance, null, "${_inst.name.value()}").isEqualTo("A");
        assertMerge(instance, null, "${_inst.name._inst.value()}").isEqualTo("A");
        assertMerge(instance, null, "<#list _inst.value() as i>${i}</#list>").isEqualTo("ABC");
        assertMerge(instance, null, "${_inst.toStringDisplayDefault()!\"X\"}").isEqualTo("X");

        assertMerge(instance, "name", "${_inst}").isEqualTo("A");
        assertMerge(instance, "value", "${_inst}").isEqualTo("B");
        assertMerge(instance, "toStringDisplayDefault", "${_inst}").isEqualTo("C");

        nameType.asAtr().displayString("#${_inst.value()}#");

        assertMerge(instance, null, "${name}").isEqualTo("A");
        assertThat(instance.getField("name").toStringDisplay()).isEqualTo("#A#");
        assertMerge(instance, null, "${name.toStringDisplay()}").isEqualTo("#A#");
        assertMerge(instance, "name", "${_inst}").isEqualTo("A");
        assertMerge(instance, "name", "${toStringDisplay()}").isEqualTo("#A#");

    }
}
