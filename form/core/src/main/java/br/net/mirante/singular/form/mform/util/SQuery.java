package br.net.mirante.singular.form.mform.util;

import java.util.Arrays;
import java.util.Optional;
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

public interface SQuery<I extends MInstancia> {

    //====================================================================================
    public Stream<I> stream();
    //====================================================================================

    public static <I extends MInstancia> SQuery<I> $ss(Supplier<Stream<I>> stream) {
        return () -> stream.get();
    }
    public static <I extends MInstancia> SQuery<I> $i(I raiz) {
        return $si(() -> raiz);
    }
    public static <I extends MInstancia> SQuery<I> $si(Supplier<I> raiz) {
        return () -> StreamSupport.stream(
            () -> Spliterators.spliterator(Arrays.asList(raiz.get()), Spliterator.IMMUTABLE | Spliterator.CONCURRENT),
            Spliterator.IMMUTABLE | Spliterator.CONCURRENT,
            true);
    }

    //====================================================================================

    @SuppressWarnings("unchecked")
    default SQuery<MInstancia> children() {
        return () -> {
			Stream<MInstancia> outter = (Stream<MInstancia>) this.stream();
			return outter
			    .flatMap(o -> {
			    	Stream<MInstancia> inner = (o instanceof MIComposto)
			            ? ((MIComposto) o).getCampos().stream()
			            : (o instanceof MILista)
			                ? ((MILista<MInstancia>) o).stream()
			                : Stream.empty();
					return inner;
			    });
		};
    }
    default SQuery<MInstancia> findAll() {
        return () -> this.stream()
            .flatMap(c -> Stream.concat(
                $i(c).children().stream(),
                StreamSupport.stream(() -> $si(() -> c).children().findAll().stream().spliterator(), 0, false)));
    }
    default <T extends MInstancia> SQuery<T> find(MTipo<T> tipo) {
        return findAll().filter(tipo);
    }
    @SuppressWarnings("unchecked")
    default <T extends MInstancia> SQuery<T> filter(MTipo<T> tipo) {
        return () -> stream()
            .filter(it -> it.getMTipo() == tipo)
            .map(it -> (T) it);
    }
    default <T extends MInstancia> SQuery<T> find(Class<T> type) {
        return () -> findAll().stream()
            .filter(it -> type.isAssignableFrom(it.getClass()))
            .map(type::cast);
    }
    default <T extends I> SQuery<T> filter(Class<T> type) {
        return () -> children().stream()
            .filter(it -> type.isAssignableFrom(it.getClass()))
            .map(type::cast);
    }
    @SuppressWarnings("unchecked")
    default SQuery<I> filter(Predicate<I> filter) {
        return () -> children().stream()
            .filter((Predicate<? super MInstancia>) filter)
            .map(it -> (I) it);
    }
    default <T extends MInstancia> SQuery<T> siblings(Class<T> type) {
        return () -> children().stream()
            .flatMap(it -> Stream.of(it.getPai()))
            .flatMap(it -> $i(it).children().stream()
                .filter(child -> child != this)
                .filter(child -> type.isAssignableFrom(child.getClass())))
            .map(type::cast);
    }
    default SQuery<MInstancia> parent() {
        return () -> stream()
            .map(it -> it.getPai());
    }
    default SQuery<MInstancia> parents() {
        return () -> stream()
            .map(it -> it.getPai())
            .flatMap(c -> (c == null) //
                ? Stream.empty() //
                : Stream.concat(
                    Stream.of(c),
                    $i(c).parents().stream()))
            .distinct();
    }
    default <T extends MInstancia> SQuery<T> parents(Class<T> type, Predicate<T> filter) {
        return () -> parents().stream()
            .filter(it -> type.isAssignableFrom(it.getClass()))
            .map(type::cast)
            .filter(filter);
    }

    default void each(Consumer<I> consumer) {
        stream().forEach(consumer);
    }
    default Optional<?> val() {
        return stream().findFirst();
    }
    @SuppressWarnings("unchecked")
    default <T> Optional<T> val(Class<T> type) {
        return stream().map(it -> (T) it).findFirst();
    }
}
