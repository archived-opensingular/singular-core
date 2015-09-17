package br.net.mirante.singular.dsl;

import br.net.mirante.singular.InstanciaDefinicao;
import org.springframework.core.task.TaskExecutor;

public class JavaBuilder1 {

    TaskBuilder taskBuilder;
    TaskBuilder2 taskBuilder2;

    public JavaBuilder1(TaskBuilder taskBuilder) {
        this.taskBuilder = taskBuilder;

    }

    public JavaBuilder1(TaskBuilder2 taskBuilder2) {
        this.taskBuilder2 = taskBuilder2;
    }

    public TaskBuilder call(TaskExecutor t){
        return taskBuilder;
    }


    @FunctionalInterface
    public static  interface TaskExecutor{

        String execute(InstanciaDefinicao instancia);
    }
}
