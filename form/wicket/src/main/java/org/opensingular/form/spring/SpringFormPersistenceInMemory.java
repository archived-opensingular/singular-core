package org.opensingular.form.spring;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.persistence.FormPersistenceInMemory;

import javax.inject.Inject;

/**
 * @param <TYPE>
 * @param <INSTANCE>
 * @author ronaldtm
 */
public class SpringFormPersistenceInMemory<TYPE extends SType<INSTANCE>, INSTANCE extends SInstance>
        extends FormPersistenceInMemory<TYPE, INSTANCE> {
    private final Class<? extends SType<?>> type;

    @Inject
    private SDocumentFactory documentFactory;

    public SpringFormPersistenceInMemory(Class<? extends SType<?>> type) {
        this.type = type;
    }

    @Override
    public INSTANCE createInstance() {
        return (INSTANCE) documentFactory.createInstance(RefType.of(type));
    }
}