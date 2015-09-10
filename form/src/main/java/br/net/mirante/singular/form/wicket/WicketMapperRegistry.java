package br.net.mirante.singular.form.wicket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.collect.ComparisonChain;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.basic.view.MView;

public class WicketMapperRegistry {

    private final List<MapperEntry> mappers = new ArrayList<>();

    public void registerMapper(
        Class<?> tipoType,
        Class<?> viewType,
        Supplier<IWicketComponentMapper> factory) {
        mappers.add(new MapperEntry(tipoType, viewType, factory));
    }

    public Optional<IWicketComponentMapper> getMapper(MInstancia instancia) {
        MTipo<?> tipo = instancia.getMTipo();
        MView view = instancia.getMTipo().getView();
        int bestScore = mappers.stream()
            .filter(it -> it.tipoType.isAssignableFrom(tipo.getClass()))
            .filter(it -> it.viewType.isAssignableFrom(view.getClass()))
            .mapToInt(it -> it.score(tipo, view))
            .min().orElse(Integer.MAX_VALUE);
        return mappers.stream()
            .filter(it -> it.tipoType.isAssignableFrom(tipo.getClass()))
            .filter(it -> it.viewType.isAssignableFrom(view.getClass()))
            .filter(it -> it.score(tipo, view) == bestScore)
            .sorted((a, b) -> ComparisonChain.start()
                .compare(a.tipoType.getName(), b.tipoType.getName())
                .compare(a.viewType.getName(), b.viewType.getName())
                .result())
            .findFirst()
            .map(it -> it.factory.get());
    }

    private static final class MapperEntry implements Comparable<MapperEntry> {
        final Class<?>                         tipoType;
        final Class<?>                         viewType;
        final Supplier<IWicketComponentMapper> factory;
        MapperEntry(
            Class<?> tipoType,
            Class<?> viewType,
            Supplier<IWicketComponentMapper> factory)
        {
            this.tipoType = tipoType;
            this.viewType = viewType;
            this.factory = factory;
        }
        int score(MTipo<?> tipo, MView view) {
            return (1 * score(this.viewType, view.getClass()))
                + (10 * score(this.tipoType, tipo.getClass()));
        }
        static int score(Class<?> candidate, Class<?> instanceType) {
            if (instanceType == candidate)
                return 0;
            if (instanceType.isAssignableFrom(candidate))
                return Short.MAX_VALUE;
            int s;
            for (s = 0; candidate.isAssignableFrom(instanceType); s++)
                instanceType = instanceType.getSuperclass();
            return s;
        }
        @Override
        public int compareTo(MapperEntry o) {
            return ComparisonChain.start()
                .compare(this.tipoType.getName(), o.tipoType.getName())
                .compare(this.viewType.getName(), o.viewType.getName())
                .result();
        }
    }
}
