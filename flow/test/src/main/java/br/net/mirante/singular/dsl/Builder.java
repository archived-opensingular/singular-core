package br.net.mirante.singular.dsl;

public class Builder {

    public TaskBuilder task() {
        return new TaskBuilder(this);
    }
}
