/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.studio.wicket;

import java.io.Serializable;

public class StudioWicketFilterContext implements Serializable {
    private static final long serialVersionUID = 8275388368722905119L;

    private String adminWicketFilterContext;

    public StudioWicketFilterContext() {
        this.adminWicketFilterContext = "";
    }

    public StudioWicketFilterContext(String adminWicketFilterContext) {
        if (adminWicketFilterContext == null
                || adminWicketFilterContext.isEmpty()
                || adminWicketFilterContext.equals("/")) {
            this.adminWicketFilterContext = "";
        } else if (!adminWicketFilterContext.startsWith("/")) {
            this.adminWicketFilterContext = "/".concat(adminWicketFilterContext);
        } else {
            this.adminWicketFilterContext = adminWicketFilterContext;
        }
    }

    public String getAdminWicketFilterContext() {
        return adminWicketFilterContext;
    }

    public String getRelativeContext() {
        return String.format("..%s/", this.getAdminWicketFilterContext());
    }
}
