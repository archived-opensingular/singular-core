package br.net.mirante.singular.form.persistence;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;


@SInfoType(name = "STypePersistentComposite", spackage = SPackageFormPersistence.class)
public class STypePersistentComposite extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        setAttributeValue(SPackageFormPersistence.ATR_FORM_KEY, null);
    }
}
