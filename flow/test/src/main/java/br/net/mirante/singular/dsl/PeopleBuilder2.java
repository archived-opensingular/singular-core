package br.net.mirante.singular.dsl;

import br.net.mirante.singular.flow.core.FlowMap;

public class PeopleBuilder2 {

    public PeopleBuilder2(PeopleBuilder1 peopleBuilder1) {
        super();
    }

    public PeopleBuilder2(TransitionBuilder1 transitionBuilder1) {

    }

    public TransitionBuilder1 transition(String aprovado) {
        return new TransitionBuilder1(this);
    }

    public WaitBuilder1 wait(String s) {
        return new WaitBuilder1(this);
    }
}
