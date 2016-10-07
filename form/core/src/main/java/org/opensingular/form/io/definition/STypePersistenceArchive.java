package org.opensingular.form.io.definition;

import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;

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
