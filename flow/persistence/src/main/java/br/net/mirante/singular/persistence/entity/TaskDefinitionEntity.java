package br.net.mirante.singular.persistence.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.net.mirante.singular.persistence.util.Constants;
import br.net.mirante.singular.persistence.util.HybridIdentityOrSequenceGenerator;
import org.hibernate.annotations.GenericGenerator;

/**
 * The persistent class for the TB_DEFINICAO_TAREFA database table.
 */
@Entity
@GenericGenerator(name = AbstractTaskDefinitionEntity.PK_GENERATOR_NAME, strategy = HybridIdentityOrSequenceGenerator.CLASS_NAME)
@Table(name = "TB_DEFINICAO_TAREFA", schema = Constants.SCHEMA)
public class TaskDefinitionEntity extends AbstractTaskDefinitionEntity<ProcessDefinitionEntity, TaskVersionEntity> {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to TaskRight
    @OneToMany(mappedBy = "taskDefinition")
    private List<TaskRight> permissoesTarefas;

    public List<TaskRight> getPermissoesTarefas() {
        return permissoesTarefas;
    }

    public void setPermissoesTarefas(List<TaskRight> permissoesTarefas) {
        this.permissoesTarefas = permissoesTarefas;
    }

}
