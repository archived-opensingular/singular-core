/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.opensingular.lib.wicket.util.bootstrap.layout;
import static org.opensingular.lib.wicket.util.bootstrap.layout.IBSGridCol.InternoNaoUse.*;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.AbstractReadOnlyModel;


@SuppressWarnings("unchecked")
public interface IBSGridCol<THIS extends Component> {

    public static final int MAX_COLS = 12;

    public enum BSGridSize {
        XS(IBSGridCol::xs, IBSGridCol::xsOffset, IBSGridCol::xsHidden),
        SM(IBSGridCol::sm, IBSGridCol::smOffset, IBSGridCol::smHidden),
        MD(IBSGridCol::md, IBSGridCol::mdOffset, IBSGridCol::mdHidden),
        LG(IBSGridCol::lg, IBSGridCol::lgOffset, IBSGridCol::lgHidden);
        private final BiConsumer<IBSGridCol<? extends Component>, Integer> col;
        private final BiConsumer<IBSGridCol<? extends Component>, Integer> offset;
        private final Consumer<IBSGridCol<? extends Component>> hidden;
        private BSGridSize(
            BiConsumer<IBSGridCol<? extends Component>, Integer> col,
            BiConsumer<IBSGridCol<? extends Component>, Integer> offset,
            Consumer<IBSGridCol<? extends Component>> hidden) {
            this.col = col;
            this.offset = offset;
            this.hidden = hidden;
        }
        public <C extends Component & IBSGridCol<C>> C col(C comp, int cols) {
            col.accept(comp, cols);
            return comp;
        }
        public <C extends Component & IBSGridCol<C>> C offset(C comp, int cols) {
            offset.accept(comp, cols);
            return comp;
        }
        public <C extends Component & IBSGridCol<C>> C hidden(C comp) {
            hidden.accept(comp);
            return comp;
        }
    }

    default AttributeAppender newBSGridColBehavior() {
        return new AttributeAppender("class", new AbstractReadOnlyModel<CharSequence>() {
            public CharSequence getObject() {
                StringBuilder sb = new StringBuilder();

                if (xs() > 0)
                    sb.append(" col-xs-").append(xs());
                if (sm() > 0)
                    sb.append(" col-sm-").append(sm());
                if (md() > 0)
                    sb.append(" col-md-").append(md());
                if (lg() > 0)
                    sb.append(" col-lg-").append(lg());

                if (xsOffset() > 0)
                    sb.append(" col-xs-offset-").append(xsOffset());
                if (smOffset() > 0)
                    sb.append(" col-sm-offset-").append(smOffset());
                if (mdOffset() > 0)
                    sb.append(" col-md-offset-").append(mdOffset());
                if (lgOffset() > 0)
                    sb.append(" col-lg-offset-").append(lgOffset());

                if (xsHidden())
                    sb.append(" hidden-xs");
                if (smHidden())
                    sb.append(" hidden-sm");
                if (mdHidden())
                    sb.append(" hidden-md");
                if (lgHidden())
                    sb.append(" hidden-lg");

                return sb;
            }
        }, " ");
    }

    default THIS xs(int colspan) {
        assert colspan <= MAX_COLS;
        return $.set(this, XS, colspan);
    }

    default THIS sm(int colspan) {
        assert colspan <= MAX_COLS;
        return $.set(this, SM, colspan);
    }

    default THIS md(int colspan) {
        assert colspan <= MAX_COLS;
        return $.set(this, MD, colspan);
    }

    default THIS lg(int colspan) {
        assert colspan <= MAX_COLS;
        return $.set(this, LG, colspan);
    }

    default int xs() {
        return $.get(this, XS);
    }

    default int sm() {
        return $.get(this, SM);
    }

    default int md() {
        return $.get(this, MD);
    }

    default int lg() {
        return $.get(this, LG);
    }

    default THIS xsOffset(int colspan) {
        assert colspan <= MAX_COLS;
        return $.set(this, XS_OFFSET, colspan);
    }

    default THIS smOffset(int colspan) {
        assert colspan <= MAX_COLS;
        return $.set(this, SM_OFFSET, colspan);
    }

    default THIS mdOffset(int colspan) {
        assert colspan <= MAX_COLS;
        return $.set(this, MD_OFFSET, colspan);
    }

    default THIS lgOffset(int colspan) {
        assert colspan <= MAX_COLS;
        return $.set(this, LG_OFFSET, colspan);
    }

    default int xsOffset() {
        return $.get(this, XS_OFFSET);
    }

    default int smOffset() {
        return $.get(this, SM_OFFSET);
    }

    default int mdOffset() {
        return $.get(this, MD_OFFSET);
    }

    default int lgOffset() {
        return $.get(this, LG_OFFSET);
    }

    default THIS xsHidden(boolean hidden) {
        return $.set(this, XS_HIDDEN, hidden ? 1 : null);
    }

    default THIS smHidden(boolean hidden) {
        return $.set(this, SM_HIDDEN, hidden ? 1 : null);
    }

    default THIS mdHidden(boolean hidden) {
        return $.set(this, MD_HIDDEN, hidden ? 1 : null);
    }

    default THIS lgHidden(boolean hidden) {
        return $.set(this, LG_HIDDEN, hidden ? 1 : null);
    }

    default boolean xsHidden() {
        return $.get(this, XS_HIDDEN) == 1;
    }

    default boolean smHidden() {
        return $.get(this, SM_HIDDEN) == 1;
    }

    default boolean mdHidden() {
        return $.get(this, MD_HIDDEN) == 1;
    }

    default boolean lgHidden() {
        return $.get(this, LG_HIDDEN) == 1;
    }

    enum InternoNaoUse {
        $;
        static final String LG = "lg";
        static final String MD = "md";
        static final String SM = "sm";
        static final String XS = "xs";
        static final String LG_OFFSET = "lg-offset";
        static final String MD_OFFSET = "md-offset";
        static final String SM_OFFSET = "sm-offset";
        static final String XS_OFFSET = "xs-offset";
        static final String LG_HIDDEN = "hidden-lg";
        static final String MD_HIDDEN = "hidden-md";
        static final String SM_HIDDEN = "hidden-sm";
        static final String XS_HIDDEN = "hidden-xs";

        final MetaDataKey<HashMap<String, Integer>> COL_DATA = new MetaDataKey<HashMap<String, Integer>>() {
        };
        HashMap<String, Integer> getColData(Component comp) {
            HashMap<String, Integer> colData = comp.getMetaData(InternoNaoUse.$.COL_DATA);
            if (colData == null) {
                colData = new HashMap<>(1);
                comp.setMetaData(InternoNaoUse.$.COL_DATA, colData);
            }
            return colData;
        }
        int get(Object comp, String key) {
            return ObjectUtils.defaultIfNull(getColData((Component) comp).get(key), 0);
        }
        <C extends Component> C set(Object comp, String key, Integer colspan) {
            HashMap<String, Integer> colData = getColData((C) comp);
            if (colspan == null || colspan < 0)
                colData.remove(key);
            else
                colData.put(key, colspan);
            return (C) comp;
        }
    }
}
