package org.opensingular.singular.form.io.definition;

import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SIList;

public class SIPersistencePackage extends SIComposite {

    public void setPackageName(String packageName) {
        setValue(STypePersistencePackage.FIELD_PACKAGE_NAME, packageName);
    }

    public String getPackageName() {
        return getValueString(STypePersistencePackage.FIELD_PACKAGE_NAME);
    }

    public SIPersistenceType addType(String simpleName) {
        SIPersistenceType type = getTypes().addNew();
        type.setSimpleName(simpleName);
        return type;
    }

    public SIList<SIPersistenceType> getTypes() {
        return getFieldList(STypePersistencePackage.FIELD_TYPES, SIPersistenceType.class);
    }
}
