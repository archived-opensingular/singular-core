package br.net.mirante.singular.form.mform.basic.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.lambda.ISupplier;

/**
 * <p>
 * Criar um registro cruzando MTipo e MView. Ao solicitar a view para um dado
 * tipo, procura a mais próxima (ou adequada) de acordo com as informações
 * passadas.
 * </p>
 * <p>
 * Em geral é usado internamente pelo geradores de interface para encontrar o
 * gerados de um dado tipo e view.
 * </p>
 *
 * @author Daniel C. Bordin
 */
public class ViewMapperRegistry<T> implements Serializable {

    private final HashMap<Class<? extends SType>, List<RegisterEntry<T>>> registry = new HashMap<>();

    /**
     * Registra o fornecedor para o tipo para quando não for solicitado um view
     * especifica. Seria a factory default.
     * @return 
     */
    public ViewMapperRegistry<T> register(Class<? extends SType> type, ISupplier<T> factory) {
        return register(type, null, factory);
    }

    /**
     * Registra o fornecedor default (se viewType == null) ou o fornecedor
     * específico para uma view em particular.
     *
     * @param viewType
     *            Pode ser null
     * @return 
     */
    public ViewMapperRegistry<T> register(Class<? extends SType> type, Class<? extends MView> viewType, ISupplier<T> factory) {
        Objects.requireNonNull(factory);
        List<RegisterEntry<T>> list = registry.get(Objects.requireNonNull(type));
        if (list == null) {
            list = new ArrayList<>(1);
            registry.put(type, list);
        }
        list.add(new RegisterEntry<T>(viewType, factory, 100));
        return this;
    }

    /**
     * <p>
     * Tenta encontrar o dados mais adequado a instancia e view informados.
     * </p>
     * <p>
     * Faz a busca na seguinte ordem:
     * <ul>
     * <li>Procura para o tipo da intância se existe algum registro para a view
     * informada (se view != null).</li>
     * <li>Não encontrando, procura nos tipos pai do tipo se há um registro para
     * a view informada (se view != null)</li>
     * <li>Não encontrando, começa a procura do inicio (do tipo da instância),
     * mas agora procurando pela view default.</li>
     * <li>Não encontrando, avança na procura da view default a partir do tipo
     * pai.</li>
     * </ul>
     * </p>
     *
     * @param view
     *            Pode ser null
     */
    public Optional<T> getMapper(SInstance instance, MView view) {
        Class<? extends SType> type = instance.getType().getClass();
        if (view.getClass() == MView.class) {
            view = null;
        }
        T mapper = getMapper(type, view);
        if (mapper == null && view != null) {
            mapper = getMapper(type, null);
        }
        return Optional.ofNullable(mapper);
    }

    private T getMapper(Class<?> type, MView view) {
        RegisterEntry<T> selected = null;
        int score = -1;
        while (type != SType.class) {
            List<RegisterEntry<T>> list = registry.get(type);
            if (list != null) {
                for (RegisterEntry<T> entry : list) {
                    if (entry.isCompatible(view)) {
                        int newScore = entry.scoreFor(view);
                        if (selected == null || newScore > score) {
                            selected = entry;
                            score = newScore;
                        }
                    }
                }
                if (selected != null) {
                    return selected.factory.get();
                }
            }
            type = type.getSuperclass();
        }
        return null;
    }

    /**
     * Representa um mapeamento de View e suas respectiva factory para um tipo
     * específico.
     *
     * @author Daniel C. Bordin
     */
    private static final class RegisterEntry<T> implements Serializable {
        final Class<? extends MView> view;
        final ISupplier<T> factory;
        final int priority;

        RegisterEntry(Class<? extends MView> view, ISupplier<T> factory, int priority) {
            this.view = view;
            this.factory = factory;
            this.priority = priority;
        }

        public int scoreFor(MView target) {
            int score = priority * 100;
            if (target != null) {
                Class<?> v = view;
                while (v != target.getClass()) {
                    score--;
                    v = v.getSuperclass();
                }
            }
            return score;
        }

        public boolean isCompatible(MView target) {
            if (target == null) {
                return view == null;
            } else if (view != null) {
                return target.getClass().isAssignableFrom(view);
            }
            return false;
        }

    }

}
