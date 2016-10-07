/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.server.commons.persistence.entity.form;

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
