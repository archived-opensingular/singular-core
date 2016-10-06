package org.opensingular.form.exemplos.notificacaosimplificada.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import org.opensingular.form.exemplos.notificacaosimplificada.domain.enums.TipoControleValor;
import org.opensingular.singular.support.persistence.entity.BaseEntity;
import org.opensingular.singular.support.persistence.util.GenericEnumUserType;


@Entity
@Table(name = "TB_FAIXA_CONCENTRACAO", schema = "DBMEDICAMENTO")
public class FaixaConcentracao extends BaseEntity<Long> implements Serializable {

    private static final long serialVersionUID = 8905832888354927426L;

    private Long              id;
    private UnidadeMedida     unidadeMedida;
    private BigDecimal        numero;
    private TipoControleValor sinal;

    @Id
    @Column(name = "CO_SEQ_FAIXA_CONCENTRACAO", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_FAIXACONCENTRACAO")
    @SequenceGenerator(name = "SEQ_FAIXACONCENTRACAO", sequenceName = "DBMEDICAMENTO.SQ_COSEQFAIXACONCENTRACAO", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CO_UNIDADE_MEDIDA", nullable = false)
    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
    }

    @Column(name = "NU_FAIXA", nullable = false)
    public BigDecimal getNumero() {
        return numero;
    }

    public void setNumero(BigDecimal numero) {
        this.numero = numero;
    }

    @Column(name = "DS_SINAL_FAIXA", nullable = false)
    @Type(type = GenericEnumUserType.CLASS_NAME, parameters = @Parameter(name = "enumClass", value = TipoControleValor.ENUM_CLASS_NAME))
    public TipoControleValor getSinal() {
        return sinal;
    }

    public void setSinal(TipoControleValor sinal) {
        this.sinal = sinal;
    }

    @Override
    public Long getCod() {
        return id;
    }

    public void setCod(Long id) {
        this.id = id;
    }
}
