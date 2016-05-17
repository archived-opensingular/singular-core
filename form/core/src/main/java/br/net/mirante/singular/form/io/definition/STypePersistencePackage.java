package br.net.mirante.singular.form.io.definition;

import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;

@SInfoType(spackage = SPackageDefinitionPersitence.class, name = "package")
public class STypePersistencePackage extends STypeComposite<SIPersistencePackage> {

    public static final String FIELD_PACKAGE_NAME = "packageName";
    public static final String FIELD_TYPES = "types";

    public STypePersistencePackage() {
        super(SIPersistencePackage.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addFieldString(FIELD_PACKAGE_NAME);
        addFieldListOf(FIELD_TYPES, STypePersistenceType.class);
    }

}
