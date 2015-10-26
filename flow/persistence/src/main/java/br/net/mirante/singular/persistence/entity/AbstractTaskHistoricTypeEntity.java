package br.net.mirante.singular.persistence.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.flow.core.entity.IEntityTaskHistoricType;

/**
 * The base persistent class for the TB_TIPO_HISTORICO_TAREFA database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractTaskHistoricTypeEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractTaskHistoricTypeEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 */
@MappedSuperclass
@Table(name = "TB_TIPO_HISTORICO_TAREFA")
public abstract class AbstractTaskHistoricTypeEntity extends BaseEntity implements IEntityTaskHistoricType {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_TIPO_HISTORICO_TAREFA";

    @Id
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    @Column(name = "CO_TIPO_HISTORICO_TAREFA")
    private Integer cod;

    @Column(name = "DS_TIPO_HISTORICO_TAREFA", length = 50)
    private String description;

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
