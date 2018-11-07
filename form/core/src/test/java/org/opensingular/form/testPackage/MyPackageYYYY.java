package org.opensingular.form.testPackage;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SPackage;

import javax.annotation.Nonnull;

/**
 * @author Daniel C. Bordin
 * @since 2018-09-16
 */
@SInfoPackage(name = "yyyy")
public class MyPackageYYYY extends SPackage {
    @Override
    protected void onLoadPackage(@Nonnull PackageBuilder pb) {
        super.onLoadPackage(pb);
        pb.createType(ConflictPackageType.class);
    }
}
