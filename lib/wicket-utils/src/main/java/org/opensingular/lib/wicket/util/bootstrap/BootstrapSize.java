package org.opensingular.lib.wicket.util.bootstrap;

public enum BootstrapSize {

    LG("lg"),
    MD("md"),
    SM("sm"),
    NONE("");

    private final String prefix;

    BootstrapSize(String prefix) {
        this.prefix = prefix;
    }

    public String apply(String cssClass) {
        return " " + cssClass + "-" + prefix + " ";
    }

    public String apply(String before, String after) {
        return " " + before + "-" + prefix + "-" + after + " ";
    }

}
