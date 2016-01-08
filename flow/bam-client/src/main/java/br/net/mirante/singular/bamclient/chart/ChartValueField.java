package br.net.mirante.singular.bamclient.chart;

public class ChartValueField {

    private String propertyName;
    private String title;
    private String suffix;

    public ChartValueField(String propertyName, String title, String suffix) {
        this.propertyName = propertyName;
        this.title = title;
        this.suffix = suffix;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getTitle() {
        return title;
    }

    public String getSuffix() {
        return suffix;
    }
}
