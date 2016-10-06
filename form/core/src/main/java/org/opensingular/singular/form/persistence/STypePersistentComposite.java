package org.opensingular.singular.form.persistence;

import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.TypeBuilder;


@SInfoType(name = "STypePersistentComposite", spackage = SPackageFormPersistence.class)
public class STypePersistentComposite extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        setAttributeValue(SPackageFormPersistence.ATR_FORM_KEY, null);
    }
}
