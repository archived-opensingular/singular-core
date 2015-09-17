package br.net.mirante.singular.dsl;

import br.net.mirante.singular.flow.core.FlowMap;

public class TaskBuilder2 {

    public TaskBuilder2(Builder builder) {
    }

    public TaskBuilder2(TaskBuilder2 taskBuilder2) {
    }

    public JavaBuilder1 java(String key){
        return new JavaBuilder1(this);
    }

    public TransitionBuilder1 transition(String outcome){
        return new TransitionBuilder1(this);
    }

    public PeopleBuilder1 people(String aprovar) {
        return new PeopleBuilder1(this);
    }

    public TaskBuilder2 end(String fim) {
        return this;
    }

    public FlowMap build() {
        return new FlowMap(null);
    }
}
