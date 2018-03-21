/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.wicket.util.datatable;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.navigation.paging.IPageableItems;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;
import static org.opensingular.lib.wicket.util.util.WicketUtils.$m;

public class BSPaginationPanel extends Panel {

    private final IPageableItems pageable;

    public <P extends Component & IPageableItems> BSPaginationPanel(String id, P pageable) {
        super(id);
        this.pageable = pageable;

        add(new OffsetLink("previous", getPageable(), -1));

        add(new FirstLink("first", getPageable()));
        add(new WebMarkupContainer("firstEllipse")
            .add($b.visibleIf($m.get(() -> (getCurrentPage() - getMiddlePagesRadius()) > 1))));

        add(new RefreshingView<Long>("pages") {
            @Override
            protected Iterator<IModel<Long>> getItemModels() {
                return getMiddlePagesRange().stream().map($m::ofValue).map(m -> (IModel<Long>)m).iterator();
            }

            @Override
            protected void populateItem(Item<Long> item) {
                item
                        .add(newNumberedPageLink(item))
                        .add($b.classAppender("active", $m.get(() -> item.getModelObject() == getCurrentPage())));
            }

        });

        add(new WebMarkupContainer("lastEllipse")
            .add($b.visibleIf($m.get(() -> (getCurrentPage() + getMiddlePagesRadius()) < getLastPage() - 1))));
        add(new LastLink("last", getPageable()));

        add(new OffsetLink("next", getPageable(), +1));
    }

    protected NumberedPageLink newNumberedPageLink(ListItem<Long> item) {
        NumberedPageLink link = new NumberedPageLink("page", getPageable(), item.getModel());
        link.setBody($m.get(() -> 1 + item.getModelObject()));
        return link;
    }

    @SuppressWarnings("unchecked")
    public <P extends Component & IPageableItems> P getPageable() {
        return (P) pageable;
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        checkComponentTag(tag, "nav");
    }

    private int getMiddlePagesRadius() {
        return 2;
    }

    private List<Long> getMiddlePagesRange() {
        long rangeLength = Math.min(1 + (getMiddlePagesRadius() * 2L), getPageCount());
        List<Long> list = new ArrayList<>();
        long start = Math.min(Math.max(0, getCurrentPage() - getMiddlePagesRadius()), getPageCount() - rangeLength);
        for (long i = 0; i < rangeLength; i++)
            list.add(start++);
        return list;
    }

    private long getLastPage() {
        return getPageCount() - 1;
    }
    private long getPageCount() {
        return getPageable().getPageCount();
    }
    private long getCurrentPage() {
        return getPageable().getCurrentPage();
    }

    private final class LastLink extends PageNavLink {
        private <P extends Component & IPageableItems> LastLink(String id, P pageable) {
            super(id, pageable);
            setBody($m.get(() -> getLastPage() + 1));
        }
        @Override
        protected long getTargetPage() {
            return getLastPage();
        }
        @Override
        protected boolean isLinkVisible() {
            return !getMiddlePagesRange().contains(getTargetPage());
        }
    }

    private final class FirstLink extends PageNavLink {
        private <P extends Component & IPageableItems> FirstLink(String id, P pageable) {
            super(id, pageable);
        }
        @Override
        protected long getTargetPage() {
            return 0;
        }
        @Override
        protected boolean isLinkVisible() {
            return !getMiddlePagesRange().contains(getTargetPage());
        }
    }

    private final class OffsetLink extends PageNavLink {
        private final int pageDelta;
        private <P extends Component & IPageableItems> OffsetLink(String id, P pageable, int pageDelta) {
            super(id, pageable);
            this.pageDelta = pageDelta;
        }
        @Override
        protected long getTargetPage() {
            return this.getPageable().getCurrentPage() + ((long) pageDelta);
        }
        @Override
        public boolean isEnabledInHierarchy() {
            return super.isEnabledInHierarchy() && (getTargetPage() >= 0) && (getTargetPage() <= getLastPage());
        }
    }

    public static final class NumberedPageLink extends PageNavLink {
        private final IModel<Long> pageIndex;
        private <P extends Component & IPageableItems> NumberedPageLink(String id, P pageable, IModel<Long> pageIndex) {
            super(id, pageable);
            this.pageIndex = pageIndex;
        }
        @Override
        protected long getTargetPage() {
            return pageIndex.getObject();
        }
    }

    private static abstract class PageNavLink extends AjaxLink<Void> {
        private final IPageableItems pageable;
        private <P extends Component & IPageableItems> PageNavLink(String id, P pageable) {
            super(id);
            this.pageable = pageable;
        }
        protected abstract long getTargetPage();
        protected boolean isLinkVisible() {
            return true;
        }
        protected IPageableItems getPageable() { return pageable; }
        protected Component getPageableComponent() { return (Component) getPageable(); }

        @Override
        public void onClick(AjaxRequestTarget target) {
            if (isEnabledInHierarchy()) {
                getPageable().setCurrentPage(getTargetPage());
                target.add(getPageableComponent());
            }
        }
        @Override
        protected void onConfigure() {
            super.onConfigure();
            setVisible(isLinkVisible());
            setEnabled(pageable.getCurrentPage() != getTargetPage());
        }
    }

}
