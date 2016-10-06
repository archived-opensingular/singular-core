package org.opensingular.singular.form.showcase.component.studio;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SPackage;

@SInfoPackage(name = "test.order")
public class SPackageOrder extends SPackage {
    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pb.createType(STypeItem.class);
        pb.createType(STypeOrder.class);
    }
}
