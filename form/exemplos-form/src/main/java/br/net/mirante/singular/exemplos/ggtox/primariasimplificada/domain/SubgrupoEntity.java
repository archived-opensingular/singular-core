package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.domain;

import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import br.net.mirante.singular.support.persistence.util.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TD_SUBGRUPO", schema = Constants.PPSTOX)
public class SubgrupoEntity extends BaseEntity<Long> {

    @Id
    @Column(name = "CO_SEQ_SUBGRUPO")
    private Long cod;

    @Column(name = "SG_SUBGRUPO")
    private String sigla;

    @Column(name = "DS_CULTURA_REPRESENTATIVA")
    private String culturaRepresentativa;

    @Column(name = "DS_CULTURA_MENOR")
    private String culturaMenor;


    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getCulturaRepresentativa() {
        return culturaRepresentativa;
    }

    public void setCulturaRepresentativa(String culturaRepresentativa) {
        this.culturaRepresentativa = culturaRepresentativa;
    }

    public String getCulturaMenor() {
        return culturaMenor;
    }

    public void setCulturaMenor(String culturaMenor) {
        this.culturaMenor = culturaMenor;
    }
}