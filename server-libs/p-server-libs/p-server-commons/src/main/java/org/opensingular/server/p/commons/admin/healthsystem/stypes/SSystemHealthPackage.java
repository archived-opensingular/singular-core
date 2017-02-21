package org.opensingular.server.p.commons.admin.healthsystem.stypes;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SPackage;

@SInfoPackage(name = SSystemHealthPackage.PACKAGE_NAME)
public class SSystemHealthPackage extends SPackage {
	public static final String PACKAGE_NAME = "org.opensingular.server.module.admin.healthsystem";
	
	@Override
    protected void onLoadPackage(PackageBuilder pb) {
        super.onLoadPackage(pb);
        
        pb.createType(SDbHealth.class);
        pb.createType(SWebHealth.class);
    }
}
