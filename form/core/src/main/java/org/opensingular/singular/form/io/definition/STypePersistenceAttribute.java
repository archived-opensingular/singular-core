package org.opensingular.singular.form.io.definition;

import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.TypeBuilder;

@SInfoType(spackage = SPackageDefinitionPersitence.class, name = "attribute")
public class STypePersistenceAttribute extends STypeComposite<SIPersistenceAttribute> {

    public STypePersistenceAttribute() {
        super(SIPersistenceAttribute.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {

    }

}
