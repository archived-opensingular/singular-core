package br.net.mirante.singular.dsl;

public class PeopleBuilder1 {
    public PeopleBuilder1(TaskBuilder taskBuilder) {

    }

    public PeopleBuilder1(TaskBuilder2 taskBuilder2) {
    }

    public PeopleBuilder2 url(String s){
        return new PeopleBuilder2(this);
    }

    public PeopleBuilder1 right(String diretor) {
        return this;
    }
}
