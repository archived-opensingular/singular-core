package br.net.mirante.singular.wicket;

import java.io.Serializable;

public class AdminWicketFilterContext implements Serializable {
    private static final long serialVersionUID = 8275388368722905119L;

    private String adminWicketFilterContext;

    public AdminWicketFilterContext(String adminWicketFilterContext) {
        this.adminWicketFilterContext = adminWicketFilterContext;
    }

    public String getAdminWicketFilterContext() {
        return adminWicketFilterContext;
    }

    public String getRelativeContext() {
        return String.format("..%s/", this.getAdminWicketFilterContext());
    }
}
