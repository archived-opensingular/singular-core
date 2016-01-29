package br.net.mirante.singular.bamclient.portlet;

import java.io.Serializable;

import br.net.mirante.singular.bamclient.portlet.filter.FieldType;

public class FilterConfig implements Serializable {

    private String identificador;
    private FieldType fieldType;
    private String label;
    private int size;

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}