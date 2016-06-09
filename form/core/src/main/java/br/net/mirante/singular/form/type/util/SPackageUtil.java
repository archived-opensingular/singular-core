/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.type.util;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SInfoPackage;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.type.basic.SPackageBasic;

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
