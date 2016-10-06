/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.form.type.util;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SInfoPackage;
import org.opensingular.form.SPackage;
import org.opensingular.form.type.basic.SPackageBasic;

@SInfoPackage(name = SDictionary.SINGULAR_PACKAGES_PREFIX + "util")
public class SPackageUtil extends SPackage {

    @Override
    protected void onLoadPackage(PackageBuilder pb) {
        pb.createType(STypeEMail.class);
        pb.createType(STypeYearMonth.class);
        pb.createType(STypePersonName.class);
        pb.createType(STypeLatitudeLongitude.class);

        pb.addAttribute(STypeYearMonth.class, SPackageBasic.ATR_EDIT_SIZE, 7);
    }
}
