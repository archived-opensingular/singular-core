package org.opensingular.form.helpers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SType;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.type.core.SIString;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.SIAttachment;
import org.opensingular.form.type.core.attachment.STypeAttachment;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Daniel C. Bordin
 * @since 2018-08-13
 */
@RunWith(Parameterized.class)
public class AssertionsSInstanceTest extends TestCaseForm {

    public AssertionsSInstanceTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testIsTypeOf() {
        SDictionary dic = createTestDictionary();
        STypeAttachment typeAttachment = dic.getType(STypeAttachment.class);

        AssertionsSInstance assertI = assertInstance(typeAttachment.newInstance());

        assertI.isTypeOf(STypeAttachment.class).isExactTypeOf(typeAttachment);

        for (SType<?> current = typeAttachment; current != null; current = current.getSuperType()) {
            assertI.isTypeOf(current);
        }

        assertThatThrownBy(() -> assertI.isTypeOf(STypeInteger.class)).isExactlyInstanceOf(AssertionError.class)
                .hasMessageContaining("isn't of the expected type or extension");

        SType<SIAttachment> parent = Objects.requireNonNull(typeAttachment.getSuperType());
        assertThatThrownBy(() -> assertI.isExactTypeOf(parent)).isExactlyInstanceOf(AssertionError.class)
                .hasMessageContaining("isn't of the expected type");
    }

    @Test
    public void testAssertExpectedType() {
        SDictionary dic = createTestDictionary();
        SIString s = dic.getType(STypeString.class).newInstance();
        assertInstance(s).assertExpectedType(s.getType(), s, false);
        assertInstance(s).assertExpectedType(s.getType(), s, true);

        assertThatThrownBy(() -> assertInstance(s).assertExpectedType(dic.getType(STypeInteger.class), s, true))
                .isExactlyInstanceOf(AssertionError.class).hasMessageContaining("(ou extendesse o tipo)");
        assertThatThrownBy(() -> assertInstance(s).assertExpectedType(dic.getType(STypeInteger.class), s, false))
                .isExactlyInstanceOf(AssertionError.class).hasMessageContaining("Não são a mesma instância de tipo");

        assertInstance(s).assertExpectedType(dic.getType(SType.class), s, true);
        assertInstance(s).assertExpectedType(dic.getType(STypeSimple.class), s, true);
        assertThatThrownBy(() -> assertInstance(s).assertExpectedType(dic.getType(STypeSimple.class), s, false))
                .isExactlyInstanceOf(AssertionError.class).hasMessageContaining("Não são a mesma instância de tipo");
    }
}