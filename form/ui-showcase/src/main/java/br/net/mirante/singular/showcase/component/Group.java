/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.showcase.component;

import br.net.mirante.singular.showcase.view.page.form.ListPage;
import br.net.mirante.singular.util.wicket.resource.Icone;

public enum Group {
    INPUT("Input", Icone.PUZZLE, ListPage.Tipo.FORM),
    FILE("File", Icone.FOLDER, ListPage.Tipo.FORM),
    LAYOUT("Layout", Icone.GRID, ListPage.Tipo.FORM),
    VALIDATION("Validation", Icone.BAN, ListPage.Tipo.FORM),
    INTERACTION("Interaction", Icone.ROCKET, ListPage.Tipo.FORM),
    CUSTOM("Custom", Icone.WRENCH, ListPage.Tipo.FORM),
    MAPS("Maps", Icone.MAP, ListPage.Tipo.FORM);

    private final String name;
    private final Icone icone;
    private final ListPage.Tipo tipo;

    Group(String name, Icone icone, ListPage.Tipo tipo) {

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

    public ListPage.Tipo getTipo() {
        return tipo;
    }
}
