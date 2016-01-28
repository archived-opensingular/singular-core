package br.net.mirante.singular.bamclient.portlet.filter;

public enum AggregationPeriod {

    WEEKLY("Semanal"),
    MONTHLY("Mensal"),
    YEARLY("Anual"),
    BIMONTHLY("Bimestral");

    private String description;

    AggregationPeriod(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
