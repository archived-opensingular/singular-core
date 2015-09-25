package br.net.mirante.singular.dsl;

public class TaskBuilder {

    public TaskBuilder(Builder builder) {
        /* CONSTRUTOR VAZIO */
    }

    public JavaBuilder1 java(String key) {
        return new JavaBuilder1(this);
    }

    public PeopleBuilder1 people(String aprovar) {
        return new PeopleBuilder1(this);
    }

    public TransitionBuilder1 transition() {
        return new TransitionBuilder1(this);
    }

    public TransitionBuilder1 transition(String aprovado) {
        return null;
    }
}
