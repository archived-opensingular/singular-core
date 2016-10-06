/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.singular.form.showcase.component;

import org.opensingular.singular.util.wicket.resource.Icone;

public enum Group {
    INPUT("Input", Icone.PUZZLE, ShowCaseType.FORM),
    FILE("File", Icone.FOLDER, ShowCaseType.FORM),
    LAYOUT("Layout", Icone.GRID, ShowCaseType.FORM),
    VALIDATION("Validation", Icone.BAN, ShowCaseType.FORM),
    INTERACTION("Interaction", Icone.ROCKET, ShowCaseType.FORM),
    CUSTOM("Custom", Icone.WRENCH, ShowCaseType.FORM),
    MAPS("Maps", Icone.MAP, ShowCaseType.FORM),
    STUDIO_SAMPLES("Samples", Icone.DOCS, ShowCaseType.STUDIO);

    private final String name;
    private final Icone icone;
    private final ShowCaseType tipo;

    Group(String name, Icone icone, ShowCaseType tipo) {

        this.name = name;
        this.icone = icone;
        this.tipo = tipo;
    }

    public String getName() {
        return name;
    }

    public Icone getIcone() {
        return icone;
    }

    public ShowCaseType getTipo() {
        return tipo;
    }
}
