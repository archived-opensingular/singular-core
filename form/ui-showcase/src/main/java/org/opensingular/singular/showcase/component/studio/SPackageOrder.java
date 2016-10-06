package org.opensingular.singular.showcase.component.studio;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SInfoPackage;
import org.opensingular.singular.form.SPackage;

@SInfoPackage(name = "test.order")
public class SPackageOrder extends SPackage {
    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pb.createType(STypeItem.class);
        pb.createType(STypeOrder.class);
    }
}
