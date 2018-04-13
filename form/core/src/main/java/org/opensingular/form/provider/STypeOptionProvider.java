package org.opensingular.form.provider;

import static java.util.stream.Collectors.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import org.opensingular.form.SInstance;
import org.opensingular.form.type.core.SIOption;
import org.opensingular.form.type.core.SIOption.Option;

public final class STypeOptionProvider<SI extends SInstance> implements Provider<SIOption.Option, SIOption<SI>> {
    private Function<SIOption<SI>, Collection<SI>> optionsFunction     = it -> null;
    private Function<SI, String>                   descriptionFunction = it -> Objects.toString(it.getValue(), "");

    public STypeOptionProvider() {}
    public STypeOptionProvider(Function<SIOption<SI>, Collection<SI>> optionsFunction, Function<SI, String> descriptionFunction) {
        setOptionsFunction(optionsFunction);
        setDescriptionFunction(descriptionFunction);
    }
    @Override
    public List<Option> load(ProviderContext<SIOption<SI>> context) {
        Collection<SI> list = getOptionsFunction().apply(context.getInstance());
        Stream<SI> stream = (list != null) ? list.stream() : Stream.empty();
        return stream
            .map(it -> new SIOption.Option(it.getId(), getDescriptionFunction().apply(it)))
            .collect(toList());
    }

    //@formatter:off
    public Function<SIOption<SI>, Collection<SI>> getOptionsFunction()     { return optionsFunction    ; }
    public Function<SI, String>                   getDescriptionFunction() { return descriptionFunction; }
    public STypeOptionProvider<SI> setOptionsFunction    (Function<SIOption<SI>, Collection<SI>> optionsFunction) { this.optionsFunction     = optionsFunction    ; return this; }
    public STypeOptionProvider<SI> setDescriptionFunction(Function<SI, String>               descriptionFunction) { this.descriptionFunction = descriptionFunction; return this; }
    //@formatter:on
}