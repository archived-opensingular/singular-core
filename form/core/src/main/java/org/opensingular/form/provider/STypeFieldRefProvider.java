package org.opensingular.form.provider;

import static java.util.stream.Collectors.*;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.opensingular.form.SInstance;
import org.opensingular.form.type.core.SIFieldRef;
import org.opensingular.form.type.core.SIFieldRef.Option;

public final class STypeFieldRefProvider<SI extends SInstance>
    implements Provider<SIFieldRef.Option, SIFieldRef<SI>> {

    private Function<SIFieldRef<SI>, Collection<SI>> optionsFunction     = it -> null;
    private Function<SI, String>                     descriptionFunction = it -> it.toStringDisplay();

    public STypeFieldRefProvider() {}
    public STypeFieldRefProvider(Function<SIFieldRef<SI>, Collection<SI>> optionsFunction, Function<SI, String> descriptionFunction) {
        setOptionsFunction(optionsFunction);
        setDescriptionFunction(descriptionFunction);
    }
    @Override
    public List<Option> load(ProviderContext<SIFieldRef<SI>> context) {
        Collection<SI> list = getOptionsFunction().apply(context.getInstance());
        Stream<SI> stream = (list != null) ? list.stream() : Stream.empty();
        return stream
            .map(it -> new SIFieldRef.Option(it.getId(), getDescriptionFunction().apply(it)))
            .collect(toList());
    }

    //@formatter:off
    public Function<SIFieldRef<SI>, Collection<SI>> getOptionsFunction()     { return optionsFunction    ; }
    public Function<SI, String>                     getDescriptionFunction() { return descriptionFunction; }
    public STypeFieldRefProvider<SI> setOptionsFunction    (Function<SIFieldRef<SI>, Collection<SI>> optionsFunction) { this.optionsFunction     = optionsFunction    ; return this; }
    public STypeFieldRefProvider<SI> setDescriptionFunction(Function<SI, String>                 descriptionFunction) { this.descriptionFunction = descriptionFunction; return this; }
    //@formatter:on
}