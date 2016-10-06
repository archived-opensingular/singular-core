package org.opensingular.server.commons.persistence.entity.form;

import org.opensingular.form.persistence.entity.FormVersionEntity;
import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.enums.SimNao;
import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.GenericEnumUserType;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@IdClass(FormVersionHistoryPK.class)
@Table(name = "TB_HISTORICO_VERSAO_FORMULARIO", schema = Constants.SCHEMA)
public class FormVersionHistoryEntity extends BaseEntity<FormVersionHistoryPK> {

    @Id
    @Column(name = "CO_HISTORICO")
    private Long codPetitionContentHistory;

    @Id
    @Column(name = "CO_VERSAO_FORMULARIO")
    private Long codFormVersion;

    @Column(name = "ST_FORM_PRINCIPAL", length = 1)
    @Type(type = GenericEnumUserType.CLASS_NAME, parameters = {
            @org.hibernate.annotations.Parameter(name = "enumClass", value = SimNao.ENUM_CLASS_NAME),
            @org.hibernate.annotations.Parameter(name = "identifierMethod", value = "getCodigo"),
            @org.hibernate.annotations.Parameter(name = "valueOfMethod", value = "valueOfEnum")})
    private SimNao mainForm;

    @ManyToOne
    @JoinColumn(name = "CO_HISTORICO", insertable = false, updatable = false)
    private PetitionContentHistoryEntity petitionContentHistory;

    @ManyToOne
    @JoinColumn(name = "CO_VERSAO_FORMULARIO", insertable = false, updatable = false)
    private FormVersionEntity formVersion;

    @Override
    public FormVersionHistoryPK getCod() {
        return new FormVersionHistoryPK(codPetitionContentHistory, codFormVersion);
    }

    public PetitionContentHistoryEntity getPetitionContentHistory() {
        return petitionContentHistory;
    }

    public void setPetitionContentHistory(PetitionContentHistoryEntity petitionContentHistory) {
        this.petitionContentHistory = petitionContentHistory;
    }

    public FormVersionEntity getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(FormVersionEntity formVersion) {
        this.formVersion = formVersion;
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

    public SimNao getMainForm() {
        return mainForm;
    }

    public void setMainForm(SimNao mainForm) {
        this.mainForm = mainForm;
    }

}