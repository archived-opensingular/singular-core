package br.net.mirante.singular.exemplos.ggtox.primariasimplificada.domain;

import br.net.mirante.singular.support.persistence.entity.BaseEntity;
import br.net.mirante.singular.support.persistence.util.Constants;

import javax.persistence.*;

@Entity
@Table(name = "TD_CULTURA", schema = Constants.PPSTOX)
public class CulturaEntity extends BaseEntity<Long> {

    @Id
    @Column(name = "CO_SEQ_CULTURA")
    private Long cod;

    @ManyToOne
    @JoinColumn(name = "CO_SUBGRUPO")
    private SubgrupoEntity subgrupo;

    @Column(name = "NO_CULTURA")
    private String nome;

    @Override
    public Long getCod() {
        return cod;
    }

    public void setCod(Long cod) {
        this.cod = cod;
    }

    public SubgrupoEntity getSubgrupo() {
        return subgrupo;
    }

    public void setSubgrupo(SubgrupoEntity subgrupo) {
        this.subgrupo = subgrupo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
