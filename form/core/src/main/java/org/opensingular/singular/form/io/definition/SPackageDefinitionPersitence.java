package org.opensingular.singular.form.io.definition;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SDictionary;
import org.opensingular.singular.form.SInfoPackage;
import org.opensingular.singular.form.SPackage;

@SInfoPackage(name = SDictionary.SINGULAR_PACKAGES_PREFIX + "io.definition")
public class SPackageDefinitionPersitence extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pb.createType(STypePersistenceAttribute.class);
        pb.createType(STypePersistenceType.class);
        pb.createType(STypePersistencePackage.class);
        pb.createType(STypePersistenceArchive.class);
    }
}
