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
import org.opensingular.form.SingularFormException;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeLong;
import org.opensingular.form.type.core.STypeMonetary;
import org.opensingular.form.type.core.STypeTime;
import org.opensingular.form.type.util.STypeYearMonth;
import org.opensingular.lib.commons.base.SingularProperties;

@RunWith(Parameterized.class)
public class FreemarkerErrorHandlerWithPropertyFalseTest extends TestCaseForm {

    private STypeComposite<? extends SIComposite>               dataType;
    private STypeComposite<SIComposite>                         dadosType;
    private STypeList<STypeComposite<SIComposite>, SIComposite> listType;

    public FreemarkerErrorHandlerWithPropertyFalseTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }
    
    @Before
    public void setUp() {
        PackageBuilder pkt = createTestPackage();
        dataType = pkt.createCompositeType("dataType");

        dadosType = dataType.addFieldComposite("dados");
        dadosType.addFieldString("str1Value");
        dadosType.addFieldString("str2Value");
        dadosType.addFieldString("str3Value");
        dadosType.addFieldString("str4Value");
        
        
        dadosType.addFieldDate("dateValue");
        dadosType.addFieldDateTime("datetimeValue");
        dadosType.addFieldBoolean("booleanValue");
        dadosType.addFieldInteger("numberValue");
        dadosType.addFieldInteger("integerValue");
        dadosType.addField("longValue", STypeLong.class);
        dadosType.addField("doubleValue", STypeDecimal.class);
        dadosType.addField("floatValue", STypeDecimal.class);
        dadosType.addField("monetaryValue", STypeMonetary.class);
        dadosType.addField("timeValue", STypeTime.class);
        dadosType.addField("yearMonthValue", STypeYearMonth.class);

        listType = dataType.addFieldListOfComposite("listType", "item");
        STypeComposite<SIComposite> item = listType.getElementsType();
        item.addFieldString("strValue");

        //Passando false na propriedade, tem a maior precedência.
        System.setProperty(SingularProperties.FREEMARKER_IGNORE_ERROR, "false");
    }

        
    @Test(expected = SingularFormException.class)
    public void compositeSimpleWithPropertyFalse1Test() {
        SIComposite dataSI = dataType.newInstance();
        dataSI.setValue("dados.str1Value", null);
     
        assertMergeLikeDisplay(dataSI, "dados", "${str1Value}").isEqualTo("");
    }

    @Test(expected = SingularFormException.class)
    public void compositeSimpleWithPropertyFalse2Test() {
        SIComposite dataSI = dataType.newInstance();
        dataSI.setValue("dados.str1Value", null);

        assertMerge(dataSI, "dados", "${str1Value}").isEqualTo("");
    }

    
    @Test
    public void compositeSimpleWithoutPropertyTest() {

        SIComposite dataSI = dataType.newInstance();
        dataSI.setValue("dados.str1Value", "joao");
        dataSI.setValue("dados.str2Value", "");
        dataSI.setValue("dados.str3Value", null);
     
        assertMergeLikeDisplay(dataSI, "dados", "${str1Value}").isEqualTo("joao");
        assertMergeLikeDisplay(dataSI, "dados", "${str2Value!'Sem Valor'}").isEqualTo("Sem Valor");
        assertMergeLikeDisplay(dataSI, "dados", "${str3Value!}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${str4Value!}").isEqualTo("");
        
        assertMerge(dataSI, "dados", "${str1Value}").isEqualTo("joao");
        assertMerge(dataSI, "dados", "${str2Value!'Sem Valor'}").isEqualTo("Sem Valor");
        assertMerge(dataSI, "dados", "${str3Value!}").isEqualTo("");
        assertMerge(dataSI, "dados", "${str4Value!}").isEqualTo("");
        
    }
    

    @Test
    public void compositeManyTypesTest() {
        SIComposite dataSI = dataType.newInstance();
        dataSI.setValue("dados.str1Value", null);
        dataSI.setValue("dados.dateValue", null);
        dataSI.setValue("dados.datetimeValue", null);        
        dataSI.setValue("dados.booleanValue", null);
        dataSI.setValue("dados.numberValue", null);
        dataSI.setValue("dados.integerValue", null);
        dataSI.setValue("dados.longValue", null);
        dataSI.setValue("dados.doubleValue", null);
        dataSI.setValue("dados.floatValue", null);
        dataSI.setValue("dados.monetaryValue", null);
        dataSI.setValue("dados.timeValue", null);
        dataSI.setValue("dados.yearMonthValue", null);
        

        assertMergeLikeDisplay(dataSI, "dados", "${str1Value!}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${dateValue!}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${datetimeValue!}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${booleanValue!}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${numberValue!}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${integerValue!}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${longValue!}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${doubleValue!}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${floatValue!}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${monetaryValue!}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${timeValue!}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${yearMonthValue!}").isEqualTo("");
        
        
        assertMerge(dataSI, "dados", "${str1Value!}").isEqualTo("");
        assertMerge(dataSI, "dados", "${dateValue!}").isEqualTo("");
        assertMerge(dataSI, "dados", "${datetimeValue!}").isEqualTo("");
        assertMerge(dataSI, "dados", "${booleanValue!}").isEqualTo("");
        assertMerge(dataSI, "dados", "${numberValue!}").isEqualTo("");
        assertMerge(dataSI, "dados", "${integerValue!}").isEqualTo("");
        assertMerge(dataSI, "dados", "${longValue!}").isEqualTo("");
        assertMerge(dataSI, "dados", "${doubleValue!}").isEqualTo("");
        assertMerge(dataSI, "dados", "${floatValue!}").isEqualTo("");
        assertMerge(dataSI, "dados", "${monetaryValue!}").isEqualTo("");
        assertMerge(dataSI, "dados", "${timeValue!}").isEqualTo("");
        assertMerge(dataSI, "dados", "${yearMonthValue!}").isEqualTo("");
    }
    
    /**
     * Forma padrão de utilização, nao escapa string html e nao ignora erros.
     * 
     * @param composite
     * @param path
     * @param templateString
     * @return
     */
    private static AbstractCharSequenceAssert<?, String> assertMerge(SIComposite composite, String path, String templateString) {
        SInstance instance = path == null ? composite : composite.getField(path);
        return Assertions.assertThat(FormFreemarkerUtil.get().merge(instance, templateString));
    }

    /**
     * Forma utilizada dentro do display dos componentes do forms
     * @param composite
     * @param path
     * @param templateString
     * @return
     */
    private static AbstractCharSequenceAssert<?, String> assertMergeLikeDisplay(SIComposite composite, String path, String templateString) {
        SInstance instance = path == null ? composite : composite.getField(path);
        return Assertions.assertThat(FormFreemarkerUtil.get().merge(instance, templateString, false, true));
    }

   
}
