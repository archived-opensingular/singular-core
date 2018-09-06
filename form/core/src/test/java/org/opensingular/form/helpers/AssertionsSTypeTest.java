package org.opensingular.form.helpers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.type.core.STypeHTML;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.STypeAttachment;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Daniel C. Bordin
 * @since 2018-08-15
 */
@RunWith(Parameterized.class)
public class AssertionsSTypeTest extends TestCaseForm {

    public AssertionsSTypeTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void isNotExtensionOfTest() {
        SDictionary dic = createTestDictionary();
        assertType(dic.getType(SType.class)).isNotExtensionOf(dic.getType(STypeString.class));
        assertThatThrownBy(() -> assertType(dic.getType(STypeString.class)).isNotExtensionOf(dic.getType(SType.class)))
                .isExactlyInstanceOf(AssertionError.class).hasMessageContaining("Shouldn't extend");
    }

    @Test
    public void testIsComposite() {
        SDictionary dic = createTestDictionary();
        assertTrue(dic.getType(STypeComposite.class).isComposite());
        assertTrue(dic.getType(STypeAttachment.class).isComposite());
        assertFalse(dic.getType(SType.class).isComposite());
        assertFalse(dic.getType(STypeList.class).isComposite());
        assertFalse(dic.getType(STypeSimple.class).isComposite());
    }

    @Test
    public void testCheckCorrectJavaSuperClassDuringExtension() {
        SDictionary dic = createTestDictionary();
        assertType(dic.getType(STypeString.class)).checkCorrectJavaSuperClassDuringExtension(
                dic.getType(STypeString.class));
        assertType(dic.getType(STypeString.class)).checkCorrectJavaSuperClassDuringExtension(dic.getType(SType.class));

        assertThatThrownBy(() -> assertType(dic.getType(STypeString.class))
                .checkCorrectJavaSuperClassDuringExtension(dic.getType(STypeHTML.class))).isExactlyInstanceOf(
                AssertionError.class).hasMessageContaining("deveria igual ou extender a classe");
        assertThatThrownBy(() -> assertType(dic.getType(STypeString.class))
                .checkCorrectJavaSuperClassDuringExtension(dic.getType(STypeList.class))).isExactlyInstanceOf(
                AssertionError.class).hasMessageContaining("deveria igual ou extender a classe");
    }
}
