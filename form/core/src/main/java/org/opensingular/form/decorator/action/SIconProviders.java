package org.opensingular.form.decorator.action;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;

import org.opensingular.lib.commons.base.SingularProperties;
import org.opensingular.lib.commons.lambda.IFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public final class SIconProviders {

    private static final Logger        log              = LoggerFactory.getLogger(SIconProviders.class);

    private static List<SIconProvider> CACHED_PROVIDERS = loadProviders();

    private SIconProviders() {}

    static SIcon resolve(String s) {
        for (SIconProvider provider : getProviders()) {
            SIcon icon = provider.resolve(s);
            if (icon != null)
                return icon;
        }
        return new SIconImpl(s, s);
    }

    private static List<SIconProvider> getProviders() {
        if (SingularProperties.get().isTrue(SingularProperties.SINGULAR_DEV_MODE)) {
            CACHED_PROVIDERS = loadProviders();
        }
        return CACHED_PROVIDERS;
    }

    private static List<SIconProvider> loadProviders() {
        ServiceLoader<SIconProvider> serviceLoader = ServiceLoader.load(SIconProvider.class, SIconProviders.class.getClassLoader());
        List<SIconProvider> providers = Lists.newArrayList(serviceLoader.iterator());
        Collections.sort(providers, Comparator.comparingInt(SIconProvider::order));
        log.debug("{}", providers);
        return providers;
    }

    public static class SimpleLineIconProvider extends AbstractSIconProvider {
        private static Properties VALID = loadValidClasses(SimpleLineIconProvider.class);
        public SimpleLineIconProvider() {
            super(100, VALID, s -> "icon-" + s);
        }
    }
    public static class FontAwesomeIconProvider extends AbstractSIconProvider {
        private static Properties VALID = loadValidClasses(FontAwesomeIconProvider.class);
        public FontAwesomeIconProvider() {
            super(200, VALID, s -> "fa fa-" + s);
        }
    }
    public static class GlyphiconsIconProvider extends AbstractSIconProvider {
        private static Properties VALID = loadValidClasses(GlyphiconsIconProvider.class);
        public GlyphiconsIconProvider() {
            super(300, VALID, s -> "glyphicon glyphicon-" + s);
        }
    }

    private static Properties loadValidClasses(Class<? extends SIconProvider> clazz) {
        Properties props = new Properties();
        try (
            InputStream input = clazz.getResourceAsStream("SIconProviders_" + clazz.getSimpleName() + ".properties");
            InputStreamReader reader = new InputStreamReader(input)) {
            props.load(reader);
        } catch (IOException ex) {
            log.warn("Couldn't load valid classes for " + clazz.getName(), ex);
        }
        return props;
    }

    private static class SIconImpl implements SIcon {
        private final String id;
        private final String cssClass;
        public SIconImpl(String id, String cssClass) {
            this.id = id;
            this.cssClass = cssClass;
        }
        @Override
        public String getId() {
            return id;
        }
        @Override
        public String getCssClass() {
            return cssClass;
        }
    }
    private static abstract class AbstractSIconProvider implements SIconProvider {
        private final int                       order;
        private final Properties                validClasses;
        private final IFunction<String, String> function;
        public AbstractSIconProvider(int order, Properties validClasses, IFunction<String, String> function) {
            this.order = order;
            this.validClasses = validClasses;
            this.function = function;
        }
        @Override
        public int order() {
            return order;
        }
        @Override
        public SIcon resolve(String s) {
            final String baseCssClass = s.toLowerCase().replaceAll("[^a-z0-9 _]", "-");
            if (!validClasses.containsKey(baseCssClass))
                return null;
            return new SIconImpl(s, function.apply(baseCssClass));
        }
    }
}
