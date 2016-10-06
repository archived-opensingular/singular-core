package org.opensingular.singular.form.io.definition;

import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.TypeBuilder;

@SInfoType(spackage = SPackageDefinitionPersitence.class, name = "type")
public class STypePersistenceType extends STypeComposite<SIPersistenceType> {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_MEMBERS = "members";

    public STypePersistenceType() {
        super(SIPersistenceType.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        addFieldString(FIELD_NAME);
        addFieldString(FIELD_TYPE);
        addFieldListOf(FIELD_MEMBERS, STypePersistenceType.class);
    }

}
