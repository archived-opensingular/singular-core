package br.net.mirante.singular.form.wicket;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.basic.view.MListaMultiPanelView;
import br.net.mirante.singular.form.mform.basic.view.MListaSimpleTableView;
import br.net.mirante.singular.form.mform.basic.view.MTabView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;
import br.net.mirante.singular.form.mform.util.comuns.MTipoAnoMes;
import br.net.mirante.singular.form.wicket.UIBuilderWicket.BooleanMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket.CompostoMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket.DateMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket.DefaultListaMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket.IntegerMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket.ListaMultiPanelMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket.ListaSimpleTableMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket.StringMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket.YearMonthMapper;

public class WicketMapperRegistry {

    private final List<MapperEntry> MAPPERS;
    {
        MAPPERS = new ArrayList<>(ImmutableList.<MapperEntry> builder()
            .add(new MapperEntry(MTipoBoolean.class, MView.class, BooleanMapper::new))
            .add(new MapperEntry(MTipoInteger.class, MView.class, IntegerMapper::new))
            .add(new MapperEntry(MTipoString.class, MView.class, StringMapper::new))
            .add(new MapperEntry(MTipoData.class, MView.class, DateMapper::new))
            .add(new MapperEntry(MTipoAnoMes.class, MView.class, YearMonthMapper::new))

            .add(new MapperEntry(MTipoComposto.class, MView.class, CompostoMapper::new))
            .add(new MapperEntry(MTipoComposto.class, MTabView.class, CompostoMapper::new))

            .add(new MapperEntry(MTipoLista.class, MView.class, DefaultListaMapper::new))
            .add(new MapperEntry(MTipoLista.class, MListaSimpleTableView.class, ListaSimpleTableMapper::new))
            .add(new MapperEntry(MTipoLista.class, MListaMultiPanelView.class, ListaMultiPanelMapper::new))
            .build());
    }

    public void registerMapper(
        Class<?> tipoType,
        Class<?> viewType,
        Supplier<IWicketComponentMapper> factory) {
        MAPPERS.add(new MapperEntry(tipoType, viewType, factory));
    }

    public Optional<IWicketComponentMapper> getMapper(MInstancia instancia) {
        MTipo<?> tipo = instancia.getMTipo();
        MView view = instancia.getMTipo().getView();
        int bestScore = MAPPERS.stream()
            .filter(it -> it.tipoType.isAssignableFrom(tipo.getClass()))
            .filter(it -> it.viewType.isAssignableFrom(view.getClass()))
            .mapToInt(it -> it.score(tipo, view))
            .min().orElse(Integer.MAX_VALUE);
        return MAPPERS.stream()
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
