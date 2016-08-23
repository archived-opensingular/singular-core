package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.domain;

import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import br.net.mirante.singular.support.persistence.util.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TD_TIPO_FORMULACAO", schema = Constants.PPSTOX)
public class TipoFormulacaoEntity extends BaseEntity<Long> {

    @Id
    @Column(name = "CO_SEQ_TIPO_FORMULACAO")
    private Long cod;

    @Column(name = "SG_TIPO_FORMULACAO")
    private String sigla;

    @Column(name = "NO_TIPO_FORMULACAO")
    private String nome;

    @Column(name = "DS_TIPO_FORMULACAO")
    private String descricao;

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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}