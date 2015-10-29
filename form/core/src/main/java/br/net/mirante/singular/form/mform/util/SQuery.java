package br.net.mirante.singular.form.mform.util;

import static java.util.stream.Collectors.*;

import java.util.Arrays;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;

public abstract class SQuery<I extends MInstancia> {

    protected final SQuery<? extends MInstancia> _parentQuery;

    private SQuery(SQuery<? extends MInstancia> parentQuery) {
        this._parentQuery = parentQuery;
    }
    //    public SQuery() {
    //        this._parentQuery = null;
    //    }

    //====================================================================================
    public abstract Stream<? extends I> stream();
    //====================================================================================

    public static <I extends MInstancia> SQuery<I> $ss(Supplier<Stream<I>> supplier) {
        return new SQuery<I>(null) {
            @Override
            public Stream<I> stream() {
                return supplier.get();
            }
        };
    }
    public static <I extends MInstancia> SQuery<I> $i(I raiz) {
        return $si(() -> raiz);
    }
    public static <I extends MInstancia> SQuery<I> $si(Supplier<I> raiz) {
        return new SQuery<I>(null) {
            @Override
            public Stream<I> stream() {
                return StreamSupport.stream(
                    () -> Spliterators.spliterator(Arrays.asList(raiz.get()), Spliterator.IMMUTABLE | Spliterator.CONCURRENT),
                    Spliterator.IMMUTABLE | Spliterator.CONCURRENT,
                    true);
            }
        };
    }

    //====================================================================================

    public SQuery<MInstancia> children() {
        return new SQuery<MInstancia>(this) {
            @Override
            @SuppressWarnings("unchecked")
            public Stream<MInstancia> stream() {
                return _parentQuery.stream()
                    .flatMap(o -> {
                    return (o instanceof MIComposto)
                        ? ((MIComposto) o).getCampos().stream()
                        : (o instanceof MILista)
                            ? ((MILista<MInstancia>) o).stream()
                            : Stream.empty();
                });
            }
        };
    }
    public SQuery<MInstancia> findAll() {
        return new SQuery<MInstancia>(this) {
            @Override
            public Stream<MInstancia> stream() {
                return _parentQuery.stream()
                    .flatMap(c -> Stream.concat(
                        $i(c).children().stream(),
                        StreamSupport.stream(() -> $si(() -> c).children().findAll().stream().spliterator(), 0, false)));
            }
        };
    }
    public <T extends MInstancia> SQuery<T> find(MTipo<T> tipo) {
        return new SQuery<T>(this) {
            @Override
            @SuppressWarnings("unchecked")
            public Stream<T> stream() {
                return _parentQuery.findAll().stream()
                    .filter(it -> it.getMTipo() == tipo)
                    .map(it -> (T) it);
            }
        };
    }
    public <T extends MInstancia> SQuery<T> filter(MTipo<T> tipo) {
        return new SQuery<T>(this) {
            @Override
            @SuppressWarnings("unchecked")
            public Stream<T> stream() {
                return _parentQuery.stream()
                    .filter(it -> it.getMTipo() == tipo)
                    .map(it -> (T) it);
            }
        };
    }
    public <T extends MInstancia> SQuery<T> find(Class<T> type) {
        return new SQuery<T>(this) {
            @Override
            public Stream<T> stream() {
                return _parentQuery.findAll().stream()
                    .filter(it -> type.isAssignableFrom(it.getClass()))
                    .map(type::cast);
            }
        };
    }
    public <T extends I> SQuery<T> filter(Class<T> type) {
        return new SQuery<T>(this) {
            @Override
            public Stream<T> stream() {
                return _parentQuery.children().stream()
                    .filter(it -> type.isAssignableFrom(it.getClass()))
                    .map(type::cast);
            }
        };
    }
    public SQuery<I> filter(Predicate<I> filter) {
        return new SQuery<I>(this) {
            @Override
            @SuppressWarnings("unchecked")
            public Stream<I> stream() {
                return _parentQuery.children().stream()
                    .filter((Predicate<? super MInstancia>) filter)
                    .map(it -> (I) it);
            }
        };
    }
    public <T extends MInstancia> SQuery<T> siblings(Class<T> type) {
        return new SQuery<T>(this) {
            @Override
            public Stream<T> stream() {
                return _parentQuery.children().stream()
                    .flatMap(it -> Stream.of(it.getPai()))
                    .flatMap(it -> $i(it).children().stream()
                        .filter(child -> child != it)
                        .filter(child -> type.isAssignableFrom(child.getClass())))
                    .map(type::cast);
            }
        };
    }
    public SQuery<MInstancia> parent() {
        return new SQuery<MInstancia>(this) {
            @Override
            public Stream<MInstancia> stream() {
                return _parentQuery.stream()
                    .map(it -> it.getPai());
            }
        };
    }
    public SQuery<MInstancia> parents() {
        return new SQuery<MInstancia>(this) {
            @Override
            public Stream<MInstancia> stream() {
                return _parentQuery.stream()
                    .map(it -> it.getPai())
                    .flatMap(c -> (c == null) //
                        ? Stream.empty() //
                        : Stream.concat(
                            Stream.of(c),
                            $i(c).parents().stream()))
                    .distinct();
            }
        };
    }
    public <T extends MInstancia> SQuery<T> parents(Class<T> type, Predicate<T> filter) {
        return new SQuery<T>(this) {
            @Override
            public Stream<T> stream() {
                return _parentQuery.parents().stream()
                    .filter(it -> type.isAssignableFrom(it.getClass()))
                    .map(type::cast)
                    .filter(filter);
            }
        };
    }
    public SQuery<? extends MInstancia> end() {
        return _parentQuery;
    }
    public List<MInstancia> list() {
        return stream().collect(toList());
    }
    public SQuery<I> each(Consumer<I> consumer) {
        stream().forEach(consumer);
        return this;
    }
    public Object val() {
        return stream().findFirst().orElse(null);
    }
    @SuppressWarnings("unchecked")
    public <T> T val(Class<T> type) {
        return stream().map(it -> (T) it).findFirst().orElse(null);
    }
}
