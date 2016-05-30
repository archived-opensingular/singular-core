package br.net.mirante.singular.server.commons.form;

import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SPackage;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.spring.SpringTypeLoader;
import br.net.mirante.singular.server.commons.config.SingularServerConfiguration;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class SingularServerSpringTypeLoader extends SpringTypeLoader<String> {

    private final Map<String, Supplier<SType<?>>> entries = new LinkedHashMap<>();
    @Inject
    private SingularServerConfiguration singularServerConfiguration;

    public SingularServerSpringTypeLoader() {
    }

    @PostConstruct
    private void init() {
        singularServerConfiguration.getFormPackagesTypeMap().entrySet().forEach(e -> add(e.getKey(), e.getValue()));
    }

    private void add(Class<? extends SPackage> packageClass, String typeName) {
        String simpleName = StringUtils.defaultIfBlank(StringUtils.substringAfterLast(typeName, "."), typeName);
        add(typeName, simpleName, () -> {
            SDictionary d = SDictionary.create();
            d.loadPackage(packageClass);
            return d.getType(typeName);
        });
    }

    private void add(String typeName, String displayName, Supplier<SType<?>> typeSupplier) {
        entries.put(typeName, typeSupplier);
    }

    @Override
    protected Optional<SType<?>> loadTypeImpl(String typeId) {
        return Optional.ofNullable(entries.get(typeId)).map(Supplier::get);
    }
}
