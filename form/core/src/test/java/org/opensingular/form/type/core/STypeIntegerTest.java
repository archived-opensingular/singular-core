package org.opensingular.form.type.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TestCaseForm;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.lib.commons.context.RefService;

import java.io.Serializable;

@RunWith(Parameterized.class)
public class STypeIntegerTest extends  TestCaseForm  {

    public STypeIntegerTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test(expected = RuntimeException.class)
    public void valorMuitoGrande() {
        STypeComposite<SIComposite> base = createTestPackage().createCompositeType("base");
        STypeInteger field1 = base.addFieldInteger("numero");

        field1.withInitListener(x -> x.setValue(Long.MAX_VALUE));
        
        field1.asAtr().maxLength(20);
        assertInstance(newInstance(field1)).isValueEquals(Long.MAX_VALUE);
    }
    
    private SInstance newInstance(SType t) {
        return SDocumentFactory.empty()
                .extendAddingSetupStep(document -> document.bindLocalService("test", P.class, RefService.of(new P())))
                .createInstance(RefType.of(() -> t));
    }

    private static class P implements Serializable {

    }

   
}
