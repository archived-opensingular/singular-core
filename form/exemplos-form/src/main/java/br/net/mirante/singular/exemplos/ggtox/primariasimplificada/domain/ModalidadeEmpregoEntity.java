package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.domain;

import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import br.net.mirante.singular.support.persistence.util.Constants;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TD_MODALIDADE_EMPREGO", schema = Constants.PPSTOX)
public class ModalidadeEmpregoEntity extends BaseEntity<Long> {

    @Id
    @Column(name = "CO_SEQ_MODALIDADE_EMPREGO")
    private Long cod;

    @Column(name = "NO_MODALIDADE_EMPREGO")
    private String nome;

    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}