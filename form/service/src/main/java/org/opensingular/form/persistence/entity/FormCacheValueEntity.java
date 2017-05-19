package org.opensingular.form.persistence.entity;

import org.hibernate.annotations.GenericGenerator;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeSimple;
import org.opensingular.form.type.core.STypeDate;
import org.opensingular.form.type.core.STypeDateTime;
import org.opensingular.form.type.core.STypeDecimal;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeLong;
import org.opensingular.form.type.core.STypeMonetary;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.STypeTime;
import org.opensingular.lib.support.persistence.entity.BaseEntity;
import org.opensingular.lib.support.persistence.util.Constants;
import org.opensingular.lib.support.persistence.util.HybridIdentityOrSequenceGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the TB_CACHE_VALOR database table.
 */
@Entity
@GenericGenerator(name = FormCacheValueEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_CACHE_VALOR", schema = Constants.SCHEMA)
public class FormCacheValueEntity  extends BaseEntity<Long> {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_CACHE_VALOR";

    @Id
    @Column(name = "CO_CACHE_VALOR")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private long                 cod;

    @ManyToOne
    @JoinColumn(name = "CO_CACHE_CAMPO")
    private FormCacheFieldEntity cacheField;

    @ManyToOne
    @JoinColumn(name = "CO_VERSAO_FORMULARIO")
    private FormVersionEntity    formVersion;

    @Column(name = "TXT_VALOR", length = 1024)
    private String     stringValue;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VALOR")
    private Date       dateValue;

    @Column(name = "NUM_VALOR")
    private BigDecimal numberValue;

    public FormCacheValueEntity() {
    }

    public FormCacheValueEntity(FormCacheFieldEntity formField, FormVersionEntity formVersion, SInstance field) {
        this.setCacheField(formField);
        this.setFormVersion(formVersion);
        this.setValue(field);
    }

    public Long getCod() {
        return cod;
    }

    public void setCod(long cod) {
        this.cod = cod;
    }

    public FormCacheFieldEntity getCacheField() {
        return cacheField;
    }

    public void setCacheField(FormCacheFieldEntity cacheField) {
        this.cacheField = cacheField;
    }

    public FormVersionEntity getFormVersion() {
        return formVersion;
    }

    public void setFormVersion(FormVersionEntity formVersion) {
        this.formVersion = formVersion;
    }

    public void setValue(SInstance instance) {
        if (instance.getValue() == null) {
            return;
        }

        SType type = instance.getType();
        if (type instanceof STypeSimple) {
            STypeSimple typeSimple = ((STypeSimple) type);

            if (typeSimple  instanceof STypeDate
                    || type instanceof STypeDateTime
                    || type instanceof STypeTime) {
                dateValue = (Date)typeSimple.convert(instance.getValue(), typeSimple.getValueClass());
            } else if (type instanceof STypeInteger) {
                Integer valor = (Integer) typeSimple.convert(instance.getValue(), typeSimple.getValueClass());
                numberValue = new BigDecimal(valor);
            } else if (type instanceof STypeLong) {
                Long valor = (Long) typeSimple.convert(instance.getValue(), typeSimple.getValueClass());
                numberValue = new BigDecimal(valor);
            } else if (type instanceof STypeDecimal || type instanceof STypeMonetary) {
                numberValue = (BigDecimal) typeSimple.convert(instance.getValue(), typeSimple.getValueClass());
            } else {
                String valor = instance.getValue().toString();
                if (valor.length() >= 2048) {
                    valor = instance.getValue().toString().substring(0, 2047);
                }
                stringValue = valor;
            }
        }
    }

    public Date getDateValue() {
        return dateValue;
    }

    public BigDecimal getNumberValue() {
        return numberValue;
    }

    public String getStringValue() {
        return stringValue;
    }

}
