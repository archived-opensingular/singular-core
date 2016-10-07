/*
 * Copyright (c) 2016, Singular and/or its affiliates. All rights reserved.
 * Singular PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.server.core.wicket.template;


import org.opensingular.server.commons.wicket.view.template.Menu;


public abstract class WorklistTemplate extends ServerTemplate {

    @Override
    protected String getPageTitleLocalKey() {
        return "anl.page.title";
    }

    @Override
    protected Menu configureMenu(String id) {
        return new MenuWorklist(id);
    }


}
