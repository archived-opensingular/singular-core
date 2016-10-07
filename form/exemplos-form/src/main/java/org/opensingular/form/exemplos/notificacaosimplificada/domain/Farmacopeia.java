package org.opensingular.form.exemplos.notificacaosimplificada.domain;

import org.opensingular.lib.support.persistence.enums.SimNao;
import org.opensingular.form.exemplos.notificacaosimplificada.domain.generic.VocabularioControlado;

import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "farmacopeia", namespace="http://www.anvisa.gov.br/reg-med/schema/domains")
@XmlType(name = "farmacopeia", namespace="http://www.anvisa.gov.br/reg-med/schema/domains")
@Entity
@Table(name="TB_FARMACOPEIA", schema="DBMEDICAMENTO")
@PrimaryKeyJoinColumn(name="CO_FARMACOPEIA", referencedColumnName="CO_SEQ_VOCABULARIO_CONTROLADO")
@NamedQuery(name="Farmacopeia.findAll", query = "Select farmacopeia From Farmacopeia as farmacopeia where farmacopeia.ativa = 'S'  Order by farmacopeia.descricao  ")
public class Farmacopeia extends VocabularioControlado {

    private static final long serialVersionUID = -4993627813276557221L;

    public Farmacopeia() {}

    public Farmacopeia(Long id, String descricao, SimNao ativa) {
        this.id = id;
        this.descricao = descricao;
        this.ativa = ativa;
    }

}
