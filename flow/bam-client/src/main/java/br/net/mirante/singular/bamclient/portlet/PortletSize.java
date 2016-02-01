package br.net.mirante.singular.bamclient.portlet;

public enum PortletSize {

    SMALL(3),
    MEDIUM(6),
    LARGE(12);

    private int size;

    PortletSize(int size) {
        this.size = size;
    }

    public String getBootstrapSize() {
        String template = "col-lg-%s col-md-%s";
        return String.format(template, size, size * 2 > 12 ? 12 : size * 2);
    }

    public int getSize() {
        return size;
    }
}
