package br.net.mirante.singular.flow.core.dto;

import java.io.Serializable;

public interface IParameterDTO extends Serializable {
    String getName();

    void setName(String name);

    boolean isRequired();

    void setRequired(boolean required);
}
