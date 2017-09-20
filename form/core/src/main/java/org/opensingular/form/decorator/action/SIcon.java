package org.opensingular.form.decorator.action;

import static java.util.Collections.*;
import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.StringUtils.*;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Ícone, de forma independente do framework de apresentação.
 */
public class SIcon implements Serializable {

    private static final Pattern SPACE               = Pattern.compile("[\\s]+");
    private final Set<String>    iconCssClasses      = new LinkedHashSet<>();
    private final Set<String>    containerCssClasses = new LinkedHashSet<>();
    private String               fgColor;
    private String               bgColor;

    public SIcon() {}

    public SIcon setIconCssClasses(String... cssClases) {
        return setIconCssClasses(Arrays.asList(cssClases));
    }
    public SIcon setIconCssClasses(Collection<String> cssClases) {
        this.iconCssClasses.clear();
        this.iconCssClasses.addAll(cssClases.stream()
            .flatMap(it -> SPACE.splitAsStream(it))
            .collect(toSet()));
        return this;
    }
    
    /**
     * Classes CSS do ícone.
     */
    public Set<String> getIconCssClasses() {
        return unmodifiableSet(iconCssClasses);
    }
    /**
     * Classes CSS do ícone.
     */
    public String getIconCssClassesString() {
        return getIconCssClasses().stream()
            .collect(joining(" "));
    }

    /**
     * Estilos CSS do ícone.
     */
    public Map<String, String> getIconCssStyles() {
        return emptyMap();
    }

    /**
     * Estilos CSS do ícone.
     */
    public String getIconCssStylesString() {
        return getIconCssStyles().entrySet().stream()
            .map(it -> it.getKey() + ":" + it.getValue())
            .collect(joining(";"));
    }

    public SIcon setContainerCssClasses(String... cssClases) {
        return setContainerCssClasses(Arrays.asList(cssClases));
    }
    public SIcon setContainerCssClasses(Collection<String> cssClases) {
        this.containerCssClasses.clear();
        this.containerCssClasses.addAll(cssClases.stream()
            .flatMap(it -> SPACE.splitAsStream(it))
            .collect(toSet()));
        return this;
    }
    
    /**
     * Classes CSS do elemento que contém o ícone.
     */
    public Set<String> getContainerCssClasses() {
        Set<String> classes = new HashSet<>();
        classes.addAll(containerCssClasses);
        toCssClass(fgColor).ifPresent(classes::add);
        toCssClass(bgColor).ifPresent(classes::add);
        return unmodifiableSet(classes);
    }

    /**
     * Classes CSS do elemento que contém o ícone.
     */
    public String getContainerCssClassesString() {
        return getContainerCssClasses().stream()
            .collect(joining(" "));
    }

    /**
     * Estilos CSS do elemento que contém o ícone.
     */
    public Map<String, String> getContainerCssStyles() {
        Map<String, String> styles = new HashMap<>();
        toCssStyle(fgColor).ifPresent(it -> styles.put("color", it));
        toCssStyle(bgColor).ifPresent(it -> styles.put("background-color", it));
        return unmodifiableMap(styles);
    }

    /**
     * Estilos CSS do elemento que contém o ícone.
     */
    public String getContainerCssStylesString() {
        return getContainerCssStyles().entrySet().stream()
            .map(it -> it.getKey() + ":" + it.getValue())
            .collect(joining(";"));
    }

    //@formatter:off
    public String getFgColor()  { return fgColor; }
    public String getBgColor()  { return bgColor; }
    public SIcon setFgColor(String fgColor) { this.fgColor = fgColor; return this; }
    public SIcon setBgColor(String bgColor) { this.bgColor = bgColor; return this; }
    //@formatter:on

    public SIcon setColors(String fg, String bg) {
        return setFgColor(fg).setBgColor(bg);
    }

    /**
     * @return nova instância de SIcon correspondente ao código
     */
    public static SIcon resolve(String code) {
        return SIconProviders.resolve(code);
    }

    private static Optional<String> toCssClass(String s) {
        return ((s != null) && (s.startsWith(".")))
            ? Optional.of(removeStart(s, "."))
            : Optional.empty();
    }

    private static Optional<String> toCssStyle(String s) {
        return ((s != null) && (!s.startsWith(".")))
            ? Optional.of(s)
            : Optional.empty();
    }
}
