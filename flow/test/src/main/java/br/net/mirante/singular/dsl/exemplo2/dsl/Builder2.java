package br.net.mirante.singular.dsl.exemplo2.dsl;

import br.net.mirante.singular.dsl.PeopleBuilder2;
import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.MTask;
import br.net.mirante.singular.flow.core.MTaskJava;
import br.net.mirante.singular.flow.core.MTaskPeople;

import java.util.function.Consumer;

public class Builder2 {
    public Builder2 javaTask(String TASK_ID) {
        return null;
    }

    public Builder2 config(Consumer<MTask> config) {
        return null;
    }


    public Builder2 transition() {
        return null;
    }

    public Builder2 peopleTask(String TASK_ID) {
        return null;
    }

    public Builder2 transitionTo(String TASK_ID, String TRANSITION_NAME) {
        return null;
    }

    public Builder2 waitTask(String TASK_ID) {
        return null;
    }

    public Builder2 endTask(String TASK_ID) {
        return null;
    }

    public Builder2 transition(String TRANSITION_NAME) {
        return null;
    }

    public FlowMap build() {
        return null;
    }
}
