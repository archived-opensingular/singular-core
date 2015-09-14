package br.net.mirante.singular.wicket;

import java.io.Serializable;

public class UIAdminWicketFilterContext implements Serializable {
    private static final long serialVersionUID = 8275388368722905119L;

    private String adminWicketFilterContext;

    public UIAdminWicketFilterContext() {
        this.adminWicketFilterContext = "";
    }

    public UIAdminWicketFilterContext(String adminWicketFilterContext) {
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
