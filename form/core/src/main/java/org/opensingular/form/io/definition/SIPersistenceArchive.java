package org.opensingular.form.io.definition;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SIList;

public class SIPersistenceArchive extends SIComposite {

    public void setRootTypeName(String rootTypeName) {
        setValue(STypePersistenceArchive.FIELD_ROOT_TYPE_NAME, rootTypeName);
    }

    public String getRootTypeName() {
        return getValueString(STypePersistenceArchive.FIELD_ROOT_TYPE_NAME);
    }

    public SIPersistencePackage addPackage(String name) {
        SIPersistencePackage pkg = getPackages().addNew();
        pkg.setPackageName(name);
        return pkg;
    }

    public SIList<SIPersistencePackage> getPackages() {
        return getFieldList(STypePersistenceArchive.FIELD_PACKAGES, SIPersistencePackage.class);
    }
}
