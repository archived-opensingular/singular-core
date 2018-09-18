package org.opensingular.form.processor;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SPackage;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.type.core.STypeString;

/**
 * @author Daniel C. Bordin
 * @since 2018-08-12
 */
@RunWith(Parameterized.class)
public class TypeProcessorPublicFieldsReferencesTest extends TestCaseForm {

    public TypeProcessorPublicFieldsReferencesTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testBlockWithPublicAndPrivateFields() {
        Assertions.assertThatThrownBy(() -> createTestDictionary().getType(BlockWithPublicAndPrivateFields.class))
                .isExactlyInstanceOf(SingularFormException.class).hasMessageContaining(
                "Não foi encontrado o campo publico esperado").hasMessageContaining("public STypeString field1");
    }

    @Test
    public void testBlockWithPublicAndPackageFields() {
        Assertions.assertThatThrownBy(() -> createTestDictionary().getType(BlockWithPublicAndPackageFields.class))
                .isExactlyInstanceOf(SingularFormException.class).hasMessageContaining(
                "Não foi encontrado o campo publico esperado").hasMessageContaining("public STypeString field1");
    }

    @SInfoPackage
    public static class PackageBase extends SPackage {
    }

    @SInfoType(spackage = PackageBase.class)
    public static class BlockWithPublicAndPrivateFields extends STypeComposite<SIComposite> {
        public STypeString field0;
        private STypeString field1;

        @Override
        protected void onLoadType(TypeBuilder tb) {
            field0 = addFieldString("field0");
            field1 = addFieldString("field1");
        }
    }

    @SInfoType(spackage = PackageBase.class)
    public static class BlockWithPublicAndPackageFields extends STypeComposite<SIComposite> {
        public STypeString field0;
        STypeString field1;

        @Override
        protected void onLoadType(TypeBuilder tb) {
            field0 = addFieldString("field0");
            field1 = addFieldString("field1");
        }
    }
}
