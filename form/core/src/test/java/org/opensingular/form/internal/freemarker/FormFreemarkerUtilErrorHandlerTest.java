package org.opensingular.form.internal.freemarker;

import java.util.Calendar;
import java.util.Date;

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
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeLong;
import org.opensingular.form.type.core.STypeMonetary;
import org.opensingular.form.type.core.STypeTime;
import org.opensingular.form.type.util.STypeYearMonth;

@RunWith(Parameterized.class)
public class FormFreemarkerUtilErrorHandlerTest extends TestCaseForm {

    private STypeComposite<? extends SIComposite>               dataType;
    private STypeComposite<SIComposite>                         dadosType;
    private STypeList<STypeComposite<SIComposite>, SIComposite> listType;
    private Date dateParam;

    public FormFreemarkerUtilErrorHandlerTest(TestFormConfig testFormConfig) {
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
        
        Calendar cal = Calendar.getInstance();
        cal.set(2017, 5, 29, 11, 00, 00);
        dateParam = cal.getTime(); 
    }

    
    @Test
    public void compositeSimpleTest() {
        SIComposite dataSI = dataType.newInstance();
        dataSI.setValue("dados.str1Value", "joao");
        dataSI.setValue("dados.str2Value", "");
        dataSI.setValue("dados.str3Value", null);
        
        
        
        dataSI.setValue("dados.dateValue", dateParam);
        dataSI.setValue("dados.datetimeValue", dateParam);        
        dataSI.setValue("dados.booleanValue", true);
        dataSI.setValue("dados.numberValue", 10);
        dataSI.setValue("dados.integerValue", 11);
        dataSI.setValue("dados.longValue", 12l);
        dataSI.setValue("dados.doubleValue", 13d);
        dataSI.setValue("dados.floatValue", 14f);
        dataSI.setValue("dados.monetaryValue", 15.2d);
        dataSI.setValue("dados.timeValue", dateParam);
        dataSI.setValue("dados.yearMonthValue", dateParam);
        
        SIComposite item1SI = (SIComposite) dataSI.getFieldList("listType").addNew();
        item1SI.setValue("strValue", "Java");
        SIComposite item2SI = (SIComposite) dataSI.getFieldList("listType").addNew();
        item2SI.setValue("strValue", "c");
        SIComposite item3SI = (SIComposite) dataSI.getFieldList("listType").addNew();
        item3SI.setValue("strValue", "scala");


        assertMerge(dataSI, "dados", "${str1Value}").isEqualTo("joao");
        assertMerge(dataSI, "dados", "${str2Value}").isEqualTo("");
        assertMerge(dataSI, "dados", "${str3Value}").isEqualTo("");
        assertMerge(dataSI, "dados", "${str4Value}").isEqualTo("");
        
//        assertMerge(dataSI, "dados", "${(dateValue?date)!}").isEqualTo("29/06/2017");
//        assertMerge(dataSI, "dados", "${(dateValue?string['dd.MM.yyyy, HH:mm'])!}").isEqualTo("29.06.2017, 11:00");
        
    }

    private static AbstractAssert<StringAssert, String> assertMerge(SIComposite composite, String path, String templateString) {
        SInstance instance = path == null ? composite : composite.getField(path);
        return Assertions.assertThat(FormFreemarkerUtil.merge(instance, templateString));
    }

   
}
