package br.net.mirante.singular.form.wicket.util;

import java.util.Optional;
import java.util.stream.Stream;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.wicket.model.IMInstanciaAwareModel;
import br.net.mirante.singular.util.wicket.util.WicketUtils;

public abstract class WicketFormUtils {
    private WicketFormUtils() {}

    public static Optional<Component> findChildByInstance(MarkupContainer root, MInstancia instance) {
        return streamDescendants(root)
            .filter(c -> instanciaIfAware(c.getDefaultModel()) == instance)
            .findAny();
    }
    private static MInstancia instanciaIfAware(IModel<?> model) {
        return (model instanceof IMInstanciaAwareModel<?>)
            ? ((IMInstanciaAwareModel<?>) model).getMInstancia()
            : null;
    }

    public static Stream<MarkupContainer> streamAscendants(Component root) {
        return WicketUtils.listParents(root).stream();
    }

    @SuppressWarnings("unchecked")
    public static Stream<Component> streamDescendants(Component root) {
        return Stream.of(root)
            .flatMap(WicketUtils.$L.recursiveIterable(c -> (c instanceof Iterable<?>) ? (Iterable<Component>) c : null));
    }
}
