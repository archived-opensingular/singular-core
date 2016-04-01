package br.net.mirante.singular.exemplos.notificacaosimplificada.domain;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.enums.SimNao;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.generic.VocabularioControlado;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;


@Entity
@Table(name = "TB_NATUREZA_MEDICAMENTO", schema = "DBMEDICAMENTO")
@PrimaryKeyJoinColumn(name = "CO_NATUREZA_MEDICAMENTO", referencedColumnName = "CO_SEQ_VOCABULARIO_CONTROLADO")
@NamedQueries({
        @NamedQuery(name = "NaturezaMedicamento.findAll", query = "Select naturezaMedicamento From NaturezaMedicamento as naturezaMedicamento where naturezaMedicamento.ativa = 'S'  Order by naturezaMedicamento.descricao  ")})
public class NaturezaMedicamento extends VocabularioControlado {

    private static final long serialVersionUID = -1890354175760895205L;

    public NaturezaMedicamento() {
    }

    public NaturezaMedicamento(Long id, String descricao, SimNao ativa) {
        this.id = id;
        this.descricao = descricao;
        this.ativa = ativa;
    }
}
