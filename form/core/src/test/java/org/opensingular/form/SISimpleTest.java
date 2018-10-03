package org.opensingular.form;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.calculation.SimpleValueCalculationInstanceOptional;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.type.core.SIInteger;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeMonetary;
import org.opensingular.form.view.SViewByBlock;
import org.opensingular.form.view.SViewTab;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Daniel C. Bordin
 * @since 2018-09-27
 */
@RunWith(Parameterized.class)
public class SISimpleTest extends TestCaseForm {

    private AtrRef<STypeInteger, SIInteger, Integer> atrTest = SPackageBasic.ATR_DISPLAY_ORDER;

    public SISimpleTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testCalculatedAttributes() {
        SDictionary dic = createTestDictionary();

        verifyColPreference(dic, SType.class, null);
        verifyColPreference(dic, STypeSimple.class, null);
        verifyColPreference(dic, STypeInteger.class, null);
        verifyColPreference(dic, STypeDecimal.class, null);
        verifyColPreference(dic, STypeMonetary.class, null);
        verifyColPreference(dic, STypeList.class, null);
        verifyColPreference(dic, STypeComposite.class, null);

        dic.getType(STypeSimple.class).setAttributeCalculationInstanceOptional(atrTest,
                ctx -> ctx.typeContext().getClass().getSimpleName().length());

        verifyColPreference(dic, SType.class, null);
        verifyColPreference(dic, STypeSimple.class, 11);
        verifyColPreference(dic, STypeInteger.class, 12);
        verifyColPreference(dic, STypeDecimal.class, 12);
        verifyColPreference(dic, STypeMonetary.class, 13);
        verifyColPreference(dic, STypeList.class, null);
        verifyColPreference(dic, STypeComposite.class, null);

        dic.getType(STypeDecimal.class).setAttributeValue(atrTest, 30);

        verifyColPreference(dic, SType.class, null);
        verifyColPreference(dic, STypeSimple.class, 11);
        verifyColPreference(dic, STypeInteger.class, 12);
        verifyColPreference(dic, STypeDecimal.class, 30);
        verifyColPreference(dic, STypeMonetary.class, 30);
        verifyColPreference(dic, STypeList.class, null);
        verifyColPreference(dic, STypeComposite.class, null);

        dic.getType(STypeComposite.class).setAttributeCalculationInstanceOptional(atrTest,
                SimpleValueCalculationInstanceOptional.nil(Integer.class).appendOnView(SViewByBlock.class, 100));

        verifyColPreference(dic, SType.class, null);
        verifyColPreference(dic, STypeSimple.class, 11);
        verifyColPreference(dic, STypeList.class, null);
        verifyColPreference(dic, STypeComposite.class, null);

        dic.getType(STypeComposite.class).withView(SViewByBlock::new);

        verifyColPreference(dic, SType.class, null);
        verifyColPreference(dic, STypeList.class, null);
        verifyColPreference(dic, STypeComposite.class, 100);

        dic.getType(STypeComposite.class).withView(SViewTab::new);

        verifyColPreference(dic, SType.class, null);
        verifyColPreference(dic, STypeList.class, null);
        verifyColPreference(dic, STypeComposite.class, null);

        dic.getType(STypeComposite.class).setAttributeValue(atrTest, 200);

        verifyColPreference(dic, SType.class, null);
        verifyColPreference(dic, STypeList.class, null);
        verifyColPreference(dic, STypeComposite.class, 200);
    }

    private void verifyColPreference(@Nonnull SDictionary dic, Class<? extends SType> typeClass,
            Integer expectedValue) {
        SType<?> type = dic.getType(typeClass);
        assertThat(type.getAttributeValue(atrTest)).isEqualTo(expectedValue);

        if (type.getInstanceClass() == null) {
            return;
        }
        SInstance instance = type.newInstance();
        assertThat(instance.getAttributeValue(atrTest)).isEqualTo(expectedValue);
    }

}