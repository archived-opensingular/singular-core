package br.net.mirante.singular.form.mform.function;

import br.net.mirante.singular.form.mform.SInstance2;

@FunctionalInterface
public interface IBehavior<T extends SInstance2> {

    public void on(IBehaviorContext ctx, T instance);

    public default IBehavior<T> andThen(IBehavior<T> next) {
        return (ctx, instance) -> {
            this.on(ctx, instance);
            if (next != null)
                next.on(ctx, instance);
        };
    }

    public static IBehavior<SInstance2> noop() {
        return (c, i) -> {};
    }
    public static IBehavior<SInstance2> noopIfNull(IBehavior<SInstance2> behavior) {
        return (behavior != null) ? behavior : noop();
    }
}
