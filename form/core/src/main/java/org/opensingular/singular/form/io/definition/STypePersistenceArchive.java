package org.opensingular.singular.form.io.definition;

import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.TypeBuilder;

@SInfoType(spackage = SPackageDefinitionPersitence.class, name = "archive")
public class STypePersistenceArchive extends STypeComposite<SIPersistenceArchive> {

    public final static String FIELD_ROOT_TYPE_NAME = "rootTypeName";
    public final static String FIELD_PACKAGES = "packages";

    public STypePersistenceArchive() {
        super(SIPersistenceArchive.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addFieldString(FIELD_ROOT_TYPE_NAME);
        addFieldListOf(FIELD_PACKAGES, STypePersistencePackage.class);
    }

}
