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

    private final Map<String, TemplateEntry> entries = new LinkedHashMap<>();

    public SingularServerSpringTypeLoader() {}

    @Inject
    private SingularServerConfiguration singularServerConfiguration;

    @PostConstruct
    private void init(){
        singularServerConfiguration.getFormPackagesTypeMap().entrySet().forEach( e -> add(e.getKey(), e.getValue()));
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
        entries.put(typeName, new TemplateEntry(displayName, typeSupplier));
    }

    public TemplateEntry findEntryByType(String type) {
        for (TemplateEntry t : entries.values()) {
            if (t.getType().getName().equals(type)) {
                return t;
            }
        }
        return null;
    }

    @Override
    protected Optional<SType<?>> loadTypeImpl(String typeId) {
        return Optional.ofNullable(findEntryByType(typeId)).map(TemplateEntry::getType);
    }

    public static class TemplateEntry {

        private final String displayName;
        private final Supplier<SType<?>> typeSupplier;

        public TemplateEntry(String displayName, Supplier<SType<?>> typeSupplier) {
            this.displayName = displayName;
            this.typeSupplier = typeSupplier;
        }

        public String getDisplayName() {
            return displayName;
        }

        public SType<?> getType() {
            return typeSupplier.get();
        }

        public SDictionary getDictionary() {
            return getType().getDictionary();
        }
    }
}
