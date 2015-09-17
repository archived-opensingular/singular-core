package br.net.mirante.singular.dsl;

public class WaitBuilder2 {
    public TransitionBuilder1 transition() {
        return new TransitionBuilder1(this);
    }
}
