package br.net.mirante.singular.persistence.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.flow.core.entity.IEntityVariableType;

/**
 * The base persistent class for the TB_TIPO_VARIAVEL database table.
 * <p>
 * Must declare a {@link GenericGenerator} with name
 * {@link AbstractVariableTypeEntity#PK_GENERATOR_NAME}.
 * </p>
 * <code>@GenericGenerator(name = AbstractVariableTypeEntity.PK_GENERATOR_NAME, strategy = "org.hibernate.id.IdentityGenerator")</code>
 */
@MappedSuperclass
@Table(name = "TB_TIPO_VARIAVEL")
public class AbstractVariableTypeEntity extends BaseEntity<Integer> implements IEntityVariableType {

    public static final String PK_GENERATOR_NAME = "GENERATED_CO_TIPO_VARIAVEL";

    @Id
    @Column(name = "CO_TIPO_VARIAVEL")
    @GeneratedValue(generator = PK_GENERATOR_NAME)
    private Integer cod;

    @Column(name = "NO_CLASSE_JAVA")
    private String typeClassName;

    @Column(name = "DS_TIPO_VARIAVEL")
    private String description;

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    public String getTypeClassName() {
        return typeClassName;
    }

    public void setTypeClassName(String typeClassName) {
        this.typeClassName = typeClassName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
