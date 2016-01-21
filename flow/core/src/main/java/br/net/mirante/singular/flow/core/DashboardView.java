package br.net.mirante.singular.flow.core;

import java.util.List;
import java.util.Map;

public abstract class DashboardView {

    private String name;
    private String title;
    private String subtitle;

    public DashboardView(String title, String subtitle) {
        this.name = getClass().getSimpleName();
        this.title = title;
        this.subtitle = subtitle;
    }

    public DashboardView(String name, String title, String subtitle) {
        this.name = name;
        this.title = title;
        this.subtitle = subtitle;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public abstract List<Map<String, String>> getData(String processAbbreviation);

}
