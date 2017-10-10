package org.opensingular.studio.core.view;


import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.opensingular.lib.wicket.util.resource.IconeView;
import org.opensingular.lib.wicket.util.util.WicketUtils;
import org.opensingular.studio.core.menu.GroupMenuEntry;
import org.opensingular.studio.core.menu.MenuEntry;
import org.opensingular.studio.core.menu.StudioMenu;
import org.opensingular.studio.core.util.StudioWicketUtils;

import javax.inject.Inject;
import java.util.List;

import static org.opensingular.lib.wicket.util.util.WicketUtils.$b;

public class StudioPortalContent extends StudioContent {

    @Inject
    private StudioMenu studioMenu;

    public StudioPortalContent(String id, MenuEntry currentMenuEntry) {
        super(id, currentMenuEntry);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        MenuEntry currentEntry = getCurrentMenuEntry();
        if (currentEntry == null) {
            buildPortal(studioMenu.getChildren());
        }
        else if (currentEntry instanceof GroupMenuEntry) {
            buildPortal(((GroupMenuEntry) currentEntry).getChildren());
        }
    }

    private void buildPortal(List<MenuEntry> entries) {
        ListView<MenuEntry> listView = new ListView<MenuEntry>("entries", entries) {
            @Override
            protected void populateItem(ListItem<MenuEntry> listItem) {
                final MenuEntry entry = listItem.getModelObject();
                WebMarkupContainer bsDiv = new WebMarkupContainer("bsDiv") {
                };
                WebMarkupContainer anchor = new WebMarkupContainer("anchor") {
                    @Override
                    protected void onComponentTag(ComponentTag tag) {
                        super.onComponentTag(tag);
                        tag.put("href", entry.getEndpoint());
                    }
                };
                anchor.add(new IconeView("icon", WicketUtils.$m.ofValue(entry.getIcon()), null, null));
                anchor.add(new Label("label", entry.getName()));
                bsDiv.add(anchor);
                if (entries.size() == 1) {
                    bsDiv.add($b.classAppender("col-md-offset-3"));
                }
                listItem.add(bsDiv);
            }

        };
        add(listView);
    }

}