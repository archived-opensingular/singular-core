package br.net.mirante.singular.form.io.definition;

import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SIList;

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
