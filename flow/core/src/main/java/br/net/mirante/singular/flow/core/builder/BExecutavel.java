package br.net.mirante.singular.flow.core.builder;

import br.net.mirante.singular.flow.core.IExecutionDateStrategy;
import br.net.mirante.singular.flow.core.ITaskPageStrategy;
import br.net.mirante.singular.flow.core.MTaskExecutavel;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskAccessStrategy;
import br.net.mirante.singular.flow.core.IExecutionDateStrategy;
import br.net.mirante.singular.flow.core.ITaskPageStrategy;
import br.net.mirante.singular.flow.core.MTaskExecutavel;
import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.TaskAccessStrategy;

public interface BExecutavel<SELF extends BExecutavel<SELF, TASK>, TASK extends MTaskExecutavel<?>> extends BuilderTaskSelf<SELF, TASK> {

    @Override
    public default SELF addAccessStrategy(TaskAccessStrategy<?> estrategiaAcesso) {
        getTask().addAccessStrategy(estrategiaAcesso);
        return self();
    }

    @Override
    public default SELF addVisualizeStrategy(TaskAccessStrategy<?> estrategiaAcesso) {
        getTask().addVisualizeStrategy(estrategiaAcesso);
        return self();
    }

    public default SELF paginaExecucao(ITaskPageStrategy paginaExecucao) {
        getTask().setExecutionPage(paginaExecucao);
        return self();
    }

    public default SELF aposTarefaVaiParaPagina(ITaskPageStrategy paginaAposTarefa) {
        getTask().setPageAfterTask(paginaAposTarefa);
        return self();
    }

    public default SELF voltarVaiParaPagina(ITaskPageStrategy paginaVoltar) {
        getTask().setBackPage(paginaVoltar);
        return self();
    }

    public default <T extends ProcessInstance> SELF comDataAlvo(IExecutionDateStrategy<T> estrategiaDataAlvo) {
        getTask().withTargetDate(estrategiaDataAlvo);
        return self();
    }
}