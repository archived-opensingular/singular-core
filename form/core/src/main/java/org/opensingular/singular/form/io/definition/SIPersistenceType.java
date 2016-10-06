package org.opensingular.singular.form.io.definition;

import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SIList;

public class SIPersistenceType extends SIComposite {

    public void setSimpleName(String simpleName) {
        setValue(STypePersistenceType.FIELD_NAME, simpleName);
    }

    public String getSimpleName() {
        return getValueString(STypePersistenceType.FIELD_NAME);
    }

    public void setSuperType(String superTypeName) {
        setValue(STypePersistenceType.FIELD_TYPE, superTypeName);
    }

    public String getSuperType() {
        return getValueString(STypePersistenceType.FIELD_TYPE);
    }

    public SIList<SIPersistenceType> getMembers() {
        return getFieldList(STypePersistenceType.FIELD_MEMBERS, SIPersistenceType.class);
    }

    public SIPersistenceType addMember(String simpleName) {
        SIPersistenceType type = getMembers().addNew();
        type.setSimpleName(simpleName);
        return type;
    }
}
