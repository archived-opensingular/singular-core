package org.opensingular.form.type.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.STypeList;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.view.SMultiSelectionBySelectView;
import org.opensingular.form.view.SView;
import org.opensingular.form.view.SViewAttachmentList;
import org.opensingular.form.view.SViewAutoComplete;
import org.opensingular.form.view.SViewTextArea;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Daniel C. Bordin
 * @since 2018-09-27
 */
@RunWith(Parameterized.class)
public class SPackageBootstrapTest extends TestCaseForm {

    public SPackageBootstrapTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void testDefaultValues() {
        verifyColPreference(SType.class, null);
        verifyColPreference(STypeComposite.class, 12);
        verifyColPreference(STypeSimple.class, 4);
        verifyColPreference(STypeInteger.class, 4);
        verifyColPreference(STypeDate.class, 3);

        Supplier<SView> sup = SViewAutoComplete::new;
        verifyColPreference(SType.class, sup, null);
        verifyColPreference(STypeSimple.class, sup, 6);
        verifyColPreference(STypeInteger.class, sup, 6);
        verifyColPreference(STypeDate.class, sup, 6);

        verifyColPreference(STypeList.class, 12);
        verifyColPreference(STypeList.class, SViewAutoComplete::new, 12);
        verifyColPreference(STypeList.class, SMultiSelectionBySelectView::new, 4);

        verifyColPreference(STypeString.class, 6);
        verifyColPreference(STypeString.class, SViewTextArea::new, 12);
        verifyColPreference(STypeString.class, SViewAutoComplete::new, 6);

        verifyColPreference(STypeComposite.class, 12);
        verifyColPreference(STypeComposite.class, SViewAttachmentList::new, 12);
        verifyColPreference(STypeComposite.class, SViewAutoComplete::new, 6);
    }

    private void verifyColPreference(Class<? extends SType> typeClass, Integer expectedColPreference) {
        verifyColPreference(typeClass, null, expectedColPreference);
    }

    private void verifyColPreference(Class<? extends SType> typeClass, Supplier<SView> viewSupplier,
            Integer expectedColPreference) {
        SDictionary dic = createTestDictionary();
        SType<?> type = dic.getType(typeClass);
        if (viewSupplier != null) {
            type = dic.createNewPackage("test").createType("deriveted", type);
            type.withView(viewSupplier);
        }
        verifyColPreference(type, expectedColPreference);

        SType<?> t2 = dic.createNewPackage("test2").createType("deriveted2", type);
        t2.asAtrBootstrap().colPreference(7);
        verifyColPreference(t2, 7);
    }

    private void verifyColPreference(@Nonnull SType<?> type, Integer expectedColPreference) {
        assertThat(type.asAtrBootstrap().getColPreference()).isEqualTo(expectedColPreference);
        if (type.getInstanceClass() != null) {
            SInstance instance = type.newInstance();
            assertThat(instance.asAtrBootstrap().getColPreference()).isEqualTo(expectedColPreference);
        }
    }
}