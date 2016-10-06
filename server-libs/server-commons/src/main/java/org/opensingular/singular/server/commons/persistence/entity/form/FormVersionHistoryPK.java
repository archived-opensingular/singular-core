package org.opensingular.singular.server.commons.persistence.entity.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class FormVersionHistoryPK implements Serializable {

    private Long codPetitionContentHistory;
    private Long codFormVersion;

    public FormVersionHistoryPK() {
    }

    public FormVersionHistoryPK(Long codPetitionContentHistory, Long codFormVersion) {
        this.codPetitionContentHistory = codPetitionContentHistory;
        this.codFormVersion = codFormVersion;
    }

    public Long getCodPetitionContentHistory() {
        return codPetitionContentHistory;
    }

    public void setCodPetitionContentHistory(Long codPetitionContentHistory) {
        this.codPetitionContentHistory = codPetitionContentHistory;
    }

    public Long getCodFormVersion() {
        return codFormVersion;
    }

    public void setCodFormVersion(Long codFormVersion) {
        this.codFormVersion = codFormVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FormVersionHistoryPK that = (FormVersionHistoryPK) o;

        return new EqualsBuilder()
                .append(codPetitionContentHistory, that.codPetitionContentHistory)
                .append(codFormVersion, that.codFormVersion)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(codPetitionContentHistory)
                .append(codFormVersion)
                .toHashCode();
    }
}
