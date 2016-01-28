package br.net.mirante.singular.bamclient.portlet.filter;

public enum FieldSize {

    SMALL(3),
    MEDIUM(6),
    LARGE(12);

    private int size;

    FieldSize(int size) {
        this.size = size;
    }

    public int getBootstrapSize() {
        return size;
    }
}
