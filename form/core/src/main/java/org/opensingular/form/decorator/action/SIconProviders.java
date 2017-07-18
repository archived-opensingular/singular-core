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

/**
 * Implementa a lógica de carga de implementações de SIconProvider. Provê três implementações padrão:
 * Simple Line, Font Awesome, e Glyphicons.
 */
final class SIconProviders {

    /**
     * Implementação de SIconProvider que provê os ícones do conjunto Simple Line (http://simplelineicons.com).
     */
    public static class SimpleLineIconProvider extends AbstractSIconProvider {
        private static Properties VALID = loadValidClasses(SimpleLineIconProvider.class);
        public SimpleLineIconProvider() {
            super(100, VALID, s -> "icon-" + s);
        }
    }

    /**
     * Implementação de SIconProvider que provê os ícones do conjunto Font Awesome (http://fontawesome.io).
     */
    public static class FontAwesomeIconProvider extends AbstractSIconProvider {
        private static Properties VALID = loadValidClasses(FontAwesomeIconProvider.class);
        public FontAwesomeIconProvider() {
            super(200, VALID, s -> "fa fa-" + s);
        }
    }

    /**
     * Implementação de SIconProvider que provê os ícones do conjunto Glyphicons fornecido em conjunto
     * com o Bootstrap (http://getbootstrap.com/components/).
     */
    public static class GlyphiconsIconProvider extends AbstractSIconProvider {
        private static Properties VALID = loadValidClasses(GlyphiconsIconProvider.class);
        public GlyphiconsIconProvider() {
            super(300, VALID, s -> "glyphicon glyphicon-" + s);
        }
    }

    private static final Logger              log              = LoggerFactory.getLogger(SIconProviders.class);

    private static final List<SIconProvider> CACHED_PROVIDERS = loadProviders();

    private SIconProviders() {}

    static SIcon resolve(String code) {
        // recarrega toda vez se em dev mode
        List<SIconProvider> providers = (SingularProperties.get().isTrue(SingularProperties.SINGULAR_DEV_MODE))
            ? loadProviders()
            : CACHED_PROVIDERS;

        for (SIconProvider provider : providers) {
            SIcon icon = provider.resolve(code);
            if (icon != null)
                return icon;
        }
        return new SIcon().setIconCssClasses(code);
    }

    private static List<SIconProvider> loadProviders() {
        ServiceLoader<SIconProvider> serviceLoader = ServiceLoader.load(SIconProvider.class, SIconProvider.class.getClassLoader());
        List<SIconProvider> providers = Lists.newArrayList(serviceLoader.iterator());
        Collections.sort(providers,
            Comparator.comparingInt(SIconProvider::order)
                .thenComparing(Comparator.comparing(it -> it.getClass().getName())));
        log.debug("{}", providers);
        return providers;
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

            return new SIcon().setIconCssClasses(function.apply(baseCssClass));
        }
        protected static Properties loadValidClasses(Class<? extends SIconProvider> clazz) {
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
    }
}
