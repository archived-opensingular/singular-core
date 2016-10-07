package br.net.mirante.singular.exemplos.notificacaosimplificada.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.net.mirante.singular.support.persistence.entity.BaseEntity;


@Entity
@Table(name = "TB_TIPO_UNIDADE_MEDICAMENTO", schema = "DBMEDICAMENTO")
public class TipoMedida extends BaseEntity<Long> implements Serializable {

    private static final long serialVersionUID = 1762089876181396422L;

    private Long   id;
    private String descricao;

    @Id
    @Column(name = "CO_SEQ_TIPO_UNID_MEDICAMENTO")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "DS_TIPO_UNIDADE_MEDICAMENTO")
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    @Transient
    public Long getCod() {
        return id;
    }

    public void setCod(Long id) {
        this.id = id;
    }

}
