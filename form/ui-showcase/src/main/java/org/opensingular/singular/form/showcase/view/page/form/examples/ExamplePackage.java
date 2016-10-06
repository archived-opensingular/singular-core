/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.view.page.form.examples;

import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SPackage;

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
