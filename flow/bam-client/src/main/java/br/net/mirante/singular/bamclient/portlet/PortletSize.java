package br.net.mirante.singular.bamclient.portlet;

/**
 * Created by danilo.mesquita on 07/01/2016.
 */
public enum PortletSize {

    SMALL(3),
    MEDIUM(6),
    LARGE(12);

    private int size;

    PortletSize(int size) {
        this.size = size;
    }

    public String getBootstrapSize() {
        String template = "col-md-size col-sm-size";
        return template.replace("size", String.valueOf(size));
    }

    public int getSize() {
        return size;
    }
}
