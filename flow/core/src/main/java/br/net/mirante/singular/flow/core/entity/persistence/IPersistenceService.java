package br.net.mirante.singular.flow.core.entity.persistence;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityCategory;
import br.net.mirante.singular.flow.core.entity.IEntityProcess;
import br.net.mirante.singular.flow.util.vars.VarInstance;
import br.net.mirante.singular.flow.util.vars.VarInstanceMap;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MUser;
import br.net.mirante.singular.flow.core.ProcessDefinition;
import br.net.mirante.singular.flow.core.TaskHistoricLog;
import br.net.mirante.singular.flow.core.entity.IEntityByCod;
import br.net.mirante.singular.flow.core.entity.IEntityProcessInstance;
import br.net.mirante.singular.flow.core.entity.IEntityTaskDefinition;
import br.net.mirante.singular.flow.core.entity.IEntityTaskInstance;
import br.net.mirante.singular.flow.core.entity.IEntityVariableInstance;

public interface IPersistenceService<DEFINITION_CATEGORY extends IEntityCategory, PROCESS_DEFINITION extends IEntityProcess, PROCESS_INSTANCE extends IEntityProcessInstance, TASK extends IEntityTaskInstance, TASK_STATE extends IEntityTaskDefinition, INSTANCE_VARIABLE extends IEntityVariableInstance> {

    PROCESS_INSTANCE criarInstancia(@NotNull PROCESS_DEFINITION definicao, @NotNull TASK_STATE situacaoInicial);

    PROCESS_INSTANCE salvarInstancia(@NotNull PROCESS_INSTANCE instancia);

    TASK adicionarTarefaInstancia(@NotNull PROCESS_INSTANCE instancia, @NotNull TASK_STATE situacao);

    void encerrarTarefaInstancia(@NotNull TASK tarefa, @Nullable String siglaTransicao, @Nullable MUser user);

    void definirInstanciaPai(@NotNull PROCESS_INSTANCE instancia, @NotNull PROCESS_INSTANCE instanciaPai);

    void definirPapelPessoa(@NotNull PROCESS_DEFINITION definicao, @NotNull PROCESS_INSTANCE instancia, String papel, MUser pessoa);

    void removerPapelPessoa(@NotNull PROCESS_INSTANCE instancia, String papel);

    Integer updateVariableValue(@NotNull ProcessInstance instancia, @NotNull VarInstance mVariavel, Integer dbVariableCod);

    void associarInstanciaTarefaPai(@NotNull PROCESS_INSTANCE instanciaFilha, @NotNull TASK tarefaPai);

    void atualizarTarefa(@NotNull TASK tarefa);

    DEFINITION_CATEGORY recuperarOuCriarCategoriaDefinicao(@NotNull String nome);

    PROCESS_DEFINITION recuperarDefinicaoProcessoPorCod(@NotNull Serializable cod);

    PROCESS_DEFINITION recuperarDefinicaoProcessoPorSigla(@NotNull String sigla);

    PROCESS_DEFINITION recuperarOuCriarDefinicaoProcesso(@NotNull ProcessDefinition<?> definicao);

    PROCESS_INSTANCE recuperarInstanciaPorCod(@NotNull Serializable cod);

    TASK_STATE recuperarSituacaoInstanciaPorCod(@NotNull Serializable cod);

    TASK_STATE recuperarOuCriarSituacaoInstancia(@NotNull PROCESS_DEFINITION definicao, @NotNull MTask<?> mTask);

    void atualizarDefinicao(@NotNull PROCESS_DEFINITION definicao);

    int apagarInstanciasProcesso(@NotNull List<TASK_STATE> situacoes, int tempo, @NotNull TimeUnit timeUnit);

    TaskHistoricLog salvarlogHistorico(@NotNull TASK tarefa, String tipoHistorico, String detalhamento, MUser alocada,
            MUser autor, Date dataHora, PROCESS_INSTANCE demandaFilha);

    void salvarHistoricoVariavel(Date dataHora, PROCESS_INSTANCE instancia, TASK tarefaOrigem, TASK tarefaDestino, VarInstanceMap<?> paramIn);

    List<? extends MUser> consultarPessoasPorCod(Collection<Integer> cods);

    void refreshModel(IEntityByCod model);

    void flushSession();

    void commitTransaction();

    // Consultas
    List<PROCESS_INSTANCE> consultarInstanciasPorSituacao(@NotNull Collection<? extends TASK_STATE> situacoesAlvo);

    List<PROCESS_INSTANCE> consultarInstanciasPorSituacao(@NotNull PROCESS_DEFINITION definicao, @Nullable Date minDataInicio, @Nullable Date maxDataInicio,
            @Nullable Collection<? extends TASK_STATE> situacoesAlvo);

    List<PROCESS_INSTANCE> consultarInstanciasPorPessoaCriadora(@NotNull PROCESS_DEFINITION definicao, @Nullable MUser pessoaCriadora,
            @Nullable Boolean ativas);
}
