package br.net.mirante.singular.dto;

import java.io.Serializable;

public class GroupDTO implements Serializable {

    private String cod;

    private String name;

    private String connectionURL;

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

}
