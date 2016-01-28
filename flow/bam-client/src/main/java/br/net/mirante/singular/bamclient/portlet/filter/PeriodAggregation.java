package br.net.mirante.singular.bamclient.portlet.filter;

public enum PeriodAggregation {

    WEEKLY("Semanal"),
    MONTHLY("Mensal"),
    YEARLY("Anual"),
    BIMONTHLY("Bimestral");

    private String description;

    PeriodAggregation(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
