package org.opensingular.server.commons.spring.security;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PetitionAuthMetadataDTO implements Serializable {

    private String formTypeAbbreviation;

    private String currentTaskAbbreviation;

    private String definitionKey;

    private Integer taskInstanceCod;

    public PetitionAuthMetadataDTO(String formTypeAbbreviationDraft, String formTypeAbbreviationProcess, String currentTaskAbbreviation, String definitionKey, Integer taskInstanceCod) {
        this.formTypeAbbreviation = formTypeAbbreviationProcess == null ? formTypeAbbreviationDraft :  formTypeAbbreviationProcess;
        this.currentTaskAbbreviation = currentTaskAbbreviation;
        this.definitionKey = definitionKey;
        this.taskInstanceCod  = taskInstanceCod;
    }

    public PetitionAuthMetadataDTO() {
    }

    public String getCurrentTaskAbbreviation() {
        return currentTaskAbbreviation;
    }

    public void setCurrentTaskAbbreviation(String currentTaskAbbreviation) {
        this.currentTaskAbbreviation = currentTaskAbbreviation;
    }

    public String getDefinitionKey() {
        return definitionKey;
    }

    public void setDefinitionKey(String definitionKey) {
        this.definitionKey = definitionKey;
    }

    public String getFormTypeAbbreviation() {
        return formTypeAbbreviation;
    }

    public void setFormTypeAbbreviation(String formTypeAbbreviation) {
        this.formTypeAbbreviation = formTypeAbbreviation;
    }

    public Integer getTaskInstanceCod() {
        return taskInstanceCod;
    }

    public void setTaskInstanceCod(Integer taskInstanceCod) {
        this.taskInstanceCod = taskInstanceCod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PetitionAuthMetadataDTO that = (PetitionAuthMetadataDTO) o;

        return new EqualsBuilder()
                .append(formTypeAbbreviation, that.formTypeAbbreviation)
                .append(currentTaskAbbreviation, that.currentTaskAbbreviation)
                .append(definitionKey, that.definitionKey)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(formTypeAbbreviation)
                .append(currentTaskAbbreviation)
                .append(definitionKey)
                .toHashCode();
    }
}
