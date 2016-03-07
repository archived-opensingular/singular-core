package br.net.mirante.singular.dsl;


import br.net.mirante.singular.flow.core.ProcessInstance;

public class WaitBuilder1 {
    public WaitBuilder1(PeopleBuilder2 peopleBuilder2) {
    }

    public WaitBuilder2 until(WaitPredicate predicate) {
        return new WaitBuilder2();
    }

    @FunctionalInterface
    public static interface WaitPredicate<T extends ProcessInstance> {

        String execute(ProcessInstance i);
    }
}