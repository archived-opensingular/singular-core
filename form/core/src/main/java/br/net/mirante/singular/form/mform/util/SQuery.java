package br.net.mirante.singular.form.mform.util;

import static java.util.stream.Collectors.*;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;

public abstract class SQuery<MI extends MInstancia> {

    protected final SQuery<? extends MInstancia> _parentQuery;

    private SQuery(SQuery<? extends MInstancia> parentQuery) {
        this._parentQuery = parentQuery;
    }
    //    public SQuery() {
    //        this._parentQuery = null;
    //    }

    //====================================================================================
    public abstract Stream<MI> stream();
    //====================================================================================

    public static <MI extends MInstancia> SQuery<MI> $ss(Supplier<Stream<MI>> supplier) {
        return new SQuery<MI>(null) {
            @Override
            public Stream<MI> stream() {
                return supplier.get();
            }
        };
    }
    public static <MI extends MInstancia> SQuery<MI> $(MI raiz) {
        return $ss(() -> Stream.of(raiz));
    }
    public static <MI extends MInstancia> SQuery<MI> $si(Supplier<MI> raiz) {
        return $ss(() -> Stream.of(raiz.get()));
    }

    //====================================================================================

    public SQuery<MInstancia> children() {
        return children((Predicate<MInstancia>) null);
    }
    @SuppressWarnings("unchecked")
    public <T extends MInstancia> SQuery<T> children(MTipo<T> type) {
        return children(inst -> inst.getMTipo() == type)
            .map(it -> (T) it);
    }
    /**
     * Get the children of each element in the set of matched elements, optionally filtered by a selector.
     */
    public SQuery<MInstancia> children(Predicate<MInstancia> selector) {
        return new SQuery<MInstancia>(this) {
            @Override
            public Stream<MInstancia> stream() {
                return (selector == null)
                    ? _parentQuery.stream().flatMap(SQuery::_children)
                    : _parentQuery.stream().flatMap(SQuery::_children).filter(selector);
            }
        };
    }

    @SuppressWarnings({ "unchecked" })
    private static Stream<MInstancia> _children(MInstancia o) {
        if (o instanceof MIComposto) {
            return _fields((MIComposto) o);
        } else if (o instanceof MILista) {
            return _elements((MILista<MInstancia>) o);
        } else {
            return Stream.empty();
        }
    }
    private static Stream<MInstancia> _fields(MIComposto composto) {
        return composto.getMTipo().getFields().stream()
            .map(f -> composto.getCampo(f.getNomeSimples()));
    }
    private static Stream<MInstancia> _elements(MILista<MInstancia> lista) {
        return lista.stream();
    }

    /**
     * Get the descendants of each element in the current set of matched elements.
     * @return
     */
    public SQuery<MInstancia> find() {
        return new SQuery<MInstancia>(this) {
            @Override
            public Stream<MInstancia> stream() {
                return _parentQuery.stream()
                    .flatMap(c -> Stream.concat(
                        $(c).children().stream(),
                        StreamSupport.stream(() -> $si(() -> c).children().find().stream().spliterator(), 0, false)));
            }
        };
    }
    public <T extends MInstancia> SQuery<T> find(MTipo<T> tipo) {
        return new SQuery<T>(this) {
            @Override
            @SuppressWarnings("unchecked")
            public Stream<T> stream() {
                return _parentQuery.find().stream()
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
    public <T extends MInstancia> SQuery<MI> has(MTipo<T> tipo, Predicate<T> test) {
        return new SQuery<MI>(this) {
            @Override
            @SuppressWarnings("unchecked")
            public Stream<MI> stream() {
                return (_parentQuery.find(tipo).filter(test).stream().findAny().isPresent())
                    ? (Stream<MI>) _parentQuery.stream()
                    : Stream.empty();
            }
        };
    }
    public SQuery<MI> filter(Predicate<MI> filter) {
        return new SQuery<MI>(this) {
            @Override
            @SuppressWarnings("unchecked")
            public Stream<MI> stream() {
                return _parentQuery.children().stream()
                    .filter((Predicate<? super MInstancia>) filter)
                    .map(it -> (MI) it);
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
                            $(c).parents().stream()))
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
    @SuppressWarnings("unchecked")
    public <O extends MInstancia> SQuery<O> map(Function<MI, O> function) {
        return new SQuery<O>(this) {
            @Override
            @SuppressWarnings("rawtypes")
            public Stream<O> stream() {
                return _parentQuery.stream()
                    .map((Function) function);
            }
        };
    }
    public SQuery<MI> first() {
        return $si(() -> stream().findFirst().orElse(null));
    }
    public <T> List<T> list(Function<MI, T> function) {
        return stream().map(function).collect(toList());
    }
    public List<MI> list() {
        return stream().collect(toList());
    }
    public SQuery<MI> each(Consumer<MI> consumer) {
        stream().forEach(consumer);
        return this;
    }
    public SQuery<MI> each(ObjIntConsumer<MI> consumer) {
        Iterator<? extends MI> it = stream().iterator();
        for (int i = 0; it.hasNext(); i++)
            consumer.accept(it.next(), i);
        return this;
    }
    public SQuery<MI> addNew() {
        return addNew(it -> {});
    }
    public SQuery<MI> addNew(Consumer<MInstancia> consumer) {
        map(it -> (MILista<?>) it).each(it -> {
            MInstancia novo = it.addNovo();
            consumer.accept(novo);
        });
        return this;
    }
    public SQuery<MI> addVal(Object value) {
        map(it -> (MILista<?>) it).each(it -> it.addValor(value));
        return this;
    }
    public Object val() {
        return stream().findFirst().map(it -> it.getValor()).orElse(null);
    }
    @SuppressWarnings("unchecked")
    public <T> T val(Class<T> type) {
        return stream().map(it -> it.getValor()).map(it -> (T) it).findFirst().orElse(null);
    }
    public <T> SQuery<MI> val(T value) {
        stream().forEach(it -> it.setValor(value));
        return this;
    }
    public long count() {
        return stream().count();
    }
}
