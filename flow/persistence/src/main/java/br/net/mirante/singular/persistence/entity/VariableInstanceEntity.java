package br.net.mirante.singular.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableType;
import br.net.mirante.singular.persistence.util.Constants;
import br.net.mirante.singular.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

/**
 * The persistent class for the TB_VARIAVEL database table.
 */
@Entity
@Table(name = "TB_VARIAVEL", schema = Constants.SCHEMA)
public class VariableInstanceEntity extends BaseEntity implements IEntityVariableInstance {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CO_VARIAVEL")
    @GeneratedValue(generator = "singular")
    @GenericGenerator(name = "singular", strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
    private Integer cod;

    @Column(name = "NO_VARIAVEL", nullable = false)
    private String name;

    @Column(name = "VL_VARIAVEL")
    private String value;

    //bi-directional many-to-one association to ProcessInstance
    @ManyToOne
    @JoinColumn(name = "CO_INSTANCIA_PROCESSO", nullable = false)
    private ProcessInstanceEntity processInstance;

    //uni-directional many-to-one association to VariableType
    @ManyToOne
    @JoinColumn(name = "CO_TIPO_VARIAVEL", nullable = false)
    private VariableTypeInstance type;

    public VariableInstanceEntity() {
    }

    @Override
    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public ProcessInstanceEntity getProcessInstance() {
        return processInstance;
    }

    public void setProcessInstance(ProcessInstanceEntity processInstance) {
        this.processInstance = processInstance;
    }

    @Override
    public VariableTypeInstance getType() {
        return type;
    }

    @Override
    public void setType(IEntityVariableType type) {
        setType((VariableTypeInstance) type);
    }

    public void setType(VariableTypeInstance type) {
        this.type = type;
    }
}