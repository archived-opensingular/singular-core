package br.net.mirante.singular.dsl;

import br.net.mirante.singular.flow.core.FlowMap;
import br.net.mirante.singular.flow.core.MTaskPeople;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class PeopleBuilder2 {

    public PeopleBuilder2(PeopleBuilder1 peopleBuilder1) {
        super();
    }

    public PeopleBuilder2(TransitionBuilder1 transitionBuilder1) {

    }

    public TransitionBuilder1 transition(String aprovado) {
        return new TransitionBuilder1(this);
    }

    public TransitionBuilder1 transition() {
        return new TransitionBuilder1(this);
    }


    public TransitionBuilder1 transition(Supplier<Boolean> sup) {
        return new TransitionBuilder1(this);
    }

    public WaitBuilder1 wait(String s) {
        return new WaitBuilder1(this);
    }

    public TaskBuilder extraConfig(Consumer<MTaskPeople> people) {
        return null;
    }

}
