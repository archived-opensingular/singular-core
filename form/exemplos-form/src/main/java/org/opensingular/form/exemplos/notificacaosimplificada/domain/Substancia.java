package org.opensingular.form.exemplos.notificacaosimplificada.domain;

// Generated 16/03/2010 08:00:26

import org.opensingular.form.exemplos.notificacaosimplificada.domain.enums.FormaFisica;
import org.opensingular.lib.support.persistence.enums.SimNao;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.generic.VocabularioControlado;
import org.opensingular.lib.support.persistence.util.GenericEnumUserType;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "TB_SUBSTANCIA", schema = "DBMEDICAMENTO")
@PrimaryKeyJoinColumn(name = "CO_SUBSTANCIA", referencedColumnName = "CO_SEQ_VOCABULARIO_CONTROLADO")
public class Substancia extends VocabularioControlado {

    private static final long serialVersionUID = -8039111718846494475L;

    @Column(name = "DS_SUBSTANCIA_INGLES", unique = true)
    private String descricaoIngles;

    @Column(name = "NU_DCB")
    private String numeroDcb;

    @Column(name = "NU_CAS")
    private String numeroCas;

    @Column(name = "ST_SAL", length = 1)
    @Type(type = GenericEnumUserType.CLASS_NAME, parameters = {
            @Parameter(name = "enumClass", value = SimNao.ENUM_CLASS_NAME),
            @Parameter(name = "identifierMethod", value = "getCodigo"),
            @Parameter(name = "valueOfMethod", value = "valueOfEnum")})
    private SimNao tipoSal;

    @Column(name = "TP_FORMA_FISICA", length = 1)
    @Type(type = GenericEnumUserType.CLASS_NAME, parameters = {
            @Parameter(name = "enumClass", value = FormaFisica.ENUM_CLASS_NAME),
            @Parameter(name = "identifierMethod", value = "getCodigo"),
            @Parameter(name = "valueOfMethod", value = "valueOfEnum")})
    private FormaFisica tipoFormaFisica;

    @Column(name = "DS_FORMA_MOLECULAR")
    private String formulaMolecular;

    @Column(name = "NU_PESO_MOLECULAR", precision = 15)
    private BigDecimal numeroPesoMolecular;

    @Column(name = "NU_PONTO_EBULICAO", precision = 15)
    private BigDecimal numeroPontoEbulicao;

    @Column(name = "NU_PONTO_FUSAO", precision = 15)
    private BigDecimal numeroPontoFusao;

    public Substancia() {
    }

    public Substancia(Long id, String descricao, SimNao tipoSal, FormaFisica tipoFormaFisica, SimNao ativa) {
        this.id = id;
        this.descricao = descricao;
        this.tipoSal = tipoSal;
        this.tipoFormaFisica = tipoFormaFisica;
        this.ativa = ativa;
    }

    public String getDescricaoIngles() {
        return this.descricaoIngles;
    }

    public void setDescricaoIngles(String substanciaIngles) {
        this.descricaoIngles = substanciaIngles;
    }

    public String getNumeroDcb() {
        return this.numeroDcb;
    }

    public void setNumeroDcb(String numeroDcb) {
        this.numeroDcb = numeroDcb;
    }

    public String getNumeroCas() {
        return this.numeroCas;
    }

    public void setNumeroCas(String numeroCas) {
        this.numeroCas = numeroCas;
    }

    public SimNao getTipoSal() {
        return this.tipoSal;
    }

    public void setTipoSal(SimNao tipoSal) {
        this.tipoSal = tipoSal;
    }

    public FormaFisica getTipoFormaFisica() {
        return this.tipoFormaFisica;
    }

    public void setTipoFormaFisica(FormaFisica tipoFormaFisica) {
        this.tipoFormaFisica = tipoFormaFisica;
    }

    public String getFormulaMolecular() {
        return this.formulaMolecular;
    }

    public void setFormulaMolecular(String formulaMolecular) {
        this.formulaMolecular = formulaMolecular;
    }

    public BigDecimal getNumeroPesoMolecular() {
        return this.numeroPesoMolecular;
    }

    public void setNumeroPesoMolecular(BigDecimal numeroPesoMolecular) {
        this.numeroPesoMolecular = numeroPesoMolecular;
    }

    public BigDecimal getNumeroPontoEbulicao() {
        return this.numeroPontoEbulicao;
    }

    public void setNumeroPontoEbulicao(BigDecimal numeroPontoEbulicao) {
        this.numeroPontoEbulicao = numeroPontoEbulicao;
    }

    public boolean isLiquido() {
        return FormaFisica.LIQUIDO.equals(getTipoFormaFisica());
    }

    public boolean isSolido() {
        return FormaFisica.SOLIDO.equals(getTipoFormaFisica());
    }

    public BigDecimal getNumeroPontoFusao() {
        return numeroPontoFusao;
    }

    public void setNumeroPontoFusao(BigDecimal numeroPontoFusao) {
        this.numeroPontoFusao = numeroPontoFusao;
    }

}
