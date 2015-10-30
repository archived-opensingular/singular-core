package br.net.mirante.singular.form.mform.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

import br.net.mirante.singular.form.mform.ICompositeInstance;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;

public abstract class MInstanciaUtils {
    private MInstanciaUtils() {}

    public static <P extends MInstancia & ICompositeInstance> P getAncestor(MInstancia node, MTipo<P> ancestorType) {
        return findAncestor(node, ancestorType).get();
    }

    @SuppressWarnings("unchecked")
    public static <A extends MInstancia & ICompositeInstance> Optional<A> findAncestor(MInstancia node, MTipo<A> ancestorType) {
        for (MInstancia parent = node.getPai(); parent != null; parent = parent.getPai()) {
            if (parent.getMTipo() == ancestorType) {
                return Optional.of((A) parent);
            }
        }
        return Optional.empty();
    }

    public static <D extends MInstancia> D getDescendant(MInstancia node, MTipo<D> descendantType) {
        return findDescendant(node, descendantType).get();
    }

    @SuppressWarnings("unchecked")
    public static <D extends MInstancia> Optional<D> findDescendant(MInstancia instancia, MTipo<D> descendantType) {
        final Deque<MInstancia> deque = new ArrayDeque<>();
        deque.add(instancia);
        while (!deque.isEmpty()) {
            final MInstancia node = deque.removeFirst();
            if (node.getMTipo() == descendantType) {
                return Optional.of((D) node);
            } else {
                deque.addAll(children(node));
            }
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public static <D extends MInstancia> List<D> listAllDescendants(MInstancia instancia, MTipo<D> descendantType) {
        List<D> result = new ArrayList<>();
        final Deque<MInstancia> deque = new ArrayDeque<>();
        deque.add(instancia);
        while (!deque.isEmpty()) {
            final MInstancia node = deque.removeFirst();
            if (node.getMTipo() == descendantType) {
                result.add((D) node);
            } else {
                deque.addAll(children(node));
            }
        }
        return result;
    }

    private static Collection<MInstancia> children(MInstancia node) {
        List<MInstancia> result = new ArrayList<>();
        if (node instanceof MIComposto) {
            final MIComposto composite = (MIComposto) node;
            @SuppressWarnings("unchecked")
            final MTipoComposto<MIComposto> nodeType = (MTipoComposto<MIComposto>) node.getMTipo();
            for (MTipo<?> fieldType : ((MTipoComposto<?>) nodeType).getFields())
                result.add(composite.getField(fieldType.getNomeSimples(), fieldType.getClasseInstancia()));

        } else if (node instanceof MILista<?>) {
            final MILista<?> list = (MILista<?>) node;
            result.addAll(list.getChildren());
        }
        return result;
    }
}
