package br.net.mirante.singular.dsl;

import br.net.mirante.singular.flow.core.FlowMap;

import java.util.function.Supplier;

public class TaskBuilder2 {

    public TaskBuilder2(Builder builder) {
    }

    public TaskBuilder2(TaskBuilder2 taskBuilder2) {
    }

    public TaskBuilder2(JavaBuilder2 javaBuilder2) {
    }

    public JavaBuilder1 java(String key){
        return new JavaBuilder1(this);
    }

    public WaitBuilder1 wait(String key){
        return new WaitBuilder1(null);
    }

    public TransitionBuilder1 transition(String outcome){
        return new TransitionBuilder1(this);
    }

    public TransitionBuilder1 transition(Supplier<Boolean> outcome){
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

    public TransitionBuilder1 transition() {
        return null;
    }
}
