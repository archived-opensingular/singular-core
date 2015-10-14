package br.net.mirante.singular.util.wicket.model;

public interface IBooleanModel extends IReadOnlyModel<Boolean> {

    default IBooleanModel not() {
        return () -> !Boolean.TRUE.equals(getObject());
    }
}
