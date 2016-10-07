package org.opensingular.form.io.definition;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SPackage;

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
