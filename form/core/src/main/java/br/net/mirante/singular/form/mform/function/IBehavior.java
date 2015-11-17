package br.net.mirante.singular.form.mform.function;

import br.net.mirante.singular.form.mform.MInstancia;

@FunctionalInterface
public interface IBehavior<T extends MInstancia> {

    public void on(IBehaviorContext ctx, T instance);

    public default IBehavior<T> andThen(IBehavior<T> next) {
        return (ctx, instance) -> {
            this.on(ctx, instance);
            if (next != null)
                next.on(ctx, instance);
        };
    }

    public static IBehavior<MInstancia> noop() {
        return (c, i) -> {};
    }
    public static IBehavior<MInstancia> noopIfNull(IBehavior<MInstancia> behavior) {
        return (behavior != null) ? behavior : noop();
    }
}
