package org.opensingular.form.sample;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SPackage;
import org.opensingular.form.persistence.SPackageFormPersistence;

import javax.annotation.Nonnull;

@SInfoPackage(name = FormTestPackage.PACKAGE_NAME)
public class FormTestPackage extends SPackage {

    public static final String PACKAGE_NAME = "br.gov.antaq.form";

    @Override
    protected void onLoadPackage(@Nonnull PackageBuilder pb) {
        pb.loadPackage(SPackageFormPersistence.class);
    }
}