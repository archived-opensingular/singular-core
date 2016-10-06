package org.opensingular.form.persistence;

import org.opensingular.form.TypeBuilder;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;


@SInfoType(name = "STypePersistentComposite", spackage = SPackageFormPersistence.class)
public class STypePersistentComposite extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        setAttributeValue(SPackageFormPersistence.ATR_FORM_KEY, null);
    }
}
