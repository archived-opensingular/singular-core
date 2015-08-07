package br.net.mirante.singular.flow.core.builder;

import java.util.function.Consumer;

import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.StartedTaskListener;
import br.net.mirante.singular.flow.core.TaskAccessStrategy;
import br.net.mirante.singular.flow.core.StartedTaskListener;

public interface BuilderTaskSelf<SELF extends BuilderTaskSelf<SELF, TASK>, TASK extends MTask<?>> extends BTask {

    @Override
    public TASK getTask();

    public default SELF with(Consumer<SELF> consumer) {
        consumer.accept(self());
        return self();
    }

    public default SELF self() {
        return (SELF) this;
    }

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

    @Override
    public default SELF addStartedTaskListener(StartedTaskListener listenerInicioTarefa) {
        getTask().addStartedTaskListener(listenerInicioTarefa);
        return self();
    }

    public default SELF setApareceNoPainelAtividades(Boolean valor) {
        getTask().setApareceNoPainelAtividades(valor);
        return self();
    }
}