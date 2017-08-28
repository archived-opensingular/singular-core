package org.opensingular.form.internal.freemarker;

import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.Assertions;
import org.fest.assertions.api.StringAssert;
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

@RunWith(Parameterized.class)
public class FreemarkerErrorHandlerTest extends TestCaseForm {

    private STypeComposite<? extends SIComposite>               dataType;
    private STypeComposite<SIComposite>                         dadosType;
    private STypeList<STypeComposite<SIComposite>, SIComposite> listType;

    public FreemarkerErrorHandlerTest(TestFormConfig testFormConfig) {
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
        
    }

    @Test
    public void compositeSimpleWithPropertyTest() {
        SIComposite dataSI = dataType.newInstance();
        dataSI.setValue("dados.str1Value", "joao");
        dataSI.setValue("dados.str2Value", "");
        dataSI.setValue("dados.str3Value", null);
     
        assertMergeLikeDisplay(dataSI, "dados", "${str1Value}").isEqualTo("joao");
        assertMergeLikeDisplay(dataSI, "dados", "${str2Value}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${str3Value}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${str4Value}").isEqualTo("");
    }
    
    @Test
    public void testWithNullValues(){
        dadosType.asAtr().displayString("${dados.str1Value}");
        SIComposite siDados = dadosType.newInstance();
        assertNotNull(siDados.toStringDisplay());
    }

    @Test(expected = SingularFormException.class)
    public void compositeSimpleWithoutPropertyTest() {

        SIComposite dataSI = dataType.newInstance();
        dataSI.setValue("dados.str1Value", "joao");
        dataSI.setValue("dados.str2Value", "");
        dataSI.setValue("dados.str3Value", null);
     
        assertMerge(dataSI, "dados", "${str1Value}").isEqualTo("joao");
        assertMerge(dataSI, "dados", "${str2Value}").isEqualTo("");
        assertMerge(dataSI, "dados", "${str3Value}").isEqualTo("");
        assertMerge(dataSI, "dados", "${str4Value}").isEqualTo("");
        
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
        

        assertMergeLikeDisplay(dataSI, "dados", "${str1Value}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${dateValue}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${datetimeValue}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${booleanValue}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${numberValue}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${integerValue}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${longValue}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${floatValue}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${monetaryValue}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${timeValue}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${yearMonthValue}").isEqualTo("");
        assertMergeLikeDisplay(dataSI, "dados", "${listType}").isEqualTo("");

        
    }
    
    /**
     * Forma padrão de utilização, nao escapa string html e nao ignora erros.
     * 
     * @param composite
     * @param path
     * @param templateString
     * @return
     */
    private static AbstractAssert<StringAssert, String> assertMerge(SIComposite composite, String path, String templateString) {
        SInstance instance = path == null ? composite : composite.getField(path);
        return Assertions.assertThat(FormFreemarkerUtil.get().merge(instance, templateString));
    }

    /**
     * Forma utilizada dentro do display dos componentes do forms, ignora erros.
     * 
     * @param composite
     * @param path
     * @param templateString
     * @return
     */
    private static AbstractAssert<StringAssert, String> assertMergeLikeDisplay(SIComposite composite, String path, String templateString) {
        SInstance instance = path == null ? composite : composite.getField(path);
        return Assertions.assertThat(FormFreemarkerUtil.get().merge(instance, templateString, false, true));
    }

   
}
