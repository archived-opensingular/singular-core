package br.net.mirante.singular.util.wicket.bootstrap.layout;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.border.Border;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

public class BSWellBorder extends Border {

    public final static String LARGE = "padding: 12px";
    public final static String SMALL = "padding: 6px;";

    public BSWellBorder(String id) {
        this(id, null);
    }

    public BSWellBorder(String id, String sizeClass) {
        super(id);
        addToBorder(buildWell(sizeClass));
    }

    private WebMarkupContainer buildWell(String sizeClass) {
        return new WebMarkupContainer("well") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                add($b.attrAppender("style", sizeClass, StringUtils.SPACE));
            }
        };
    }

    public static BSWellBorder large(String id) {
        return new BSWellBorder(id, LARGE);
    }

    public static BSWellBorder small(String id) {
        return new BSWellBorder(id, SMALL);
    }

}
