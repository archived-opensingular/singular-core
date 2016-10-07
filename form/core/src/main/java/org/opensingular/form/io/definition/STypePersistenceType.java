package org.opensingular.form.io.definition;

import org.opensingular.form.TypeBuilder;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;

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
