package org.opensingular.form;

import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;

/**
 * Apoio para teste voltado para uma única classe de SType
 *
 * @author Daniel C. Bordin
 */
public abstract class AbstractTestOneType<TYPE extends SType<?>, INSTANCE extends SInstance> extends TestCaseForm {

    private final Class<TYPE> typeClass;

    public AbstractTestOneType(TestFormConfig testFormConfig, Class<TYPE> typeClass) {
        super(testFormConfig);
        this.typeClass = typeClass;
    }

    protected final INSTANCE newInstance() {
        final Class<TYPE> c = typeClass;
        RefType refType = RefType.of(() -> createTestDictionary().getType(c));
        return (INSTANCE) SDocumentFactory.empty().createInstance(refType);
    }
}
