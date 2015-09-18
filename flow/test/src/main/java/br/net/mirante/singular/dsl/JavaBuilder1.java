package br.net.mirante.singular.dsl;

import br.net.mirante.singular.definicao.InstanciaPeticao;

public class JavaBuilder1 {

    TaskBuilder taskBuilder;
    TaskBuilder2 taskBuilder2;

    public JavaBuilder1(TaskBuilder taskBuilder) {
        this.taskBuilder = taskBuilder;

    }

    public JavaBuilder1(TaskBuilder2 taskBuilder2) {
        this.taskBuilder2 = taskBuilder2;
    }

    public JavaBuilder2 call(TaskExecutor t){
        return new JavaBuilder2(this);
    }



    @FunctionalInterface
    public static  interface TaskExecutor{

        String execute(InstanciaPeticao instancia);
    }
}
