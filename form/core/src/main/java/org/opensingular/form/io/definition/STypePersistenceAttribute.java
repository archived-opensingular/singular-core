package org.opensingular.form.io.definition;

import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;

@SInfoType(spackage = SPackageDefinitionPersitence.class, name = "attribute")
public class STypePersistenceAttribute extends STypeComposite<SIPersistenceAttribute> {

    public STypePersistenceAttribute() {
        super(SIPersistenceAttribute.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {

    }

}
