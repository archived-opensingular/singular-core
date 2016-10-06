/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.showcase.view.page.form.examples;

import org.opensingular.singular.form.PackageBuilder;
import org.opensingular.singular.form.SPackage;

public class ExamplePackage extends SPackage {

    private static final String PACKAGE = "mform.exemplo.uiShowcase";


    public ExamplePackage() {
        super(PACKAGE);
    }

    @Override
    public void onLoadPackage(PackageBuilder pb) {
        pb.createType(STypeExample.class);
    }

}
