package br.net.mirante.singular.dsl;

import br.net.mirante.singular.flow.core.ProcessInstance;
import br.net.mirante.singular.flow.core.builder.BJava;

public class JavaBuilder1 {

    TaskBuilder taskBuilder;
    TaskBuilder2 taskBuilder2;

    public JavaBuilder1(TaskBuilder taskBuilder) {
        this.taskBuilder = taskBuilder;

    }

    public JavaBuilder1(TaskBuilder2 taskBuilder2) {
        this.taskBuilder2 = taskBuilder2;
    }


    public TaskBuilder2 call(Whatever impl) {
        return new TaskBuilder2(new JavaBuilder2(this));
    }

    @FunctionalInterface
    public interface Whatever {
        void execute(Object ... objects);
    }
}
