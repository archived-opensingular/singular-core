package org.opensingular.studio.core.menu;

import com.google.common.collect.Lists;
import org.apache.wicket.markup.html.panel.Panel;
import org.opensingular.lib.commons.base.SingularUtil;
import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.studio.core.util.StudioWicketUtils;
import org.opensingular.studio.core.view.StudioContent;
import org.opensingular.studio.core.view.StudioPage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public interface MenuEntry extends Serializable {

    /**
     * @return The element icon
     */
    Icon getIcon();

    /**
     * @return the name
     */
    String getName();

    /**
     * @return the parent
     */
    MenuEntry getParent();

    /**
     * Set the parent of the entry
     *
     * @param parent
     */
    void setParent(MenuEntry parent);

    /**
     * Get the studio content
     *
     * @return
     */
    default StudioContent makeContent(String id) {
        return null;
    }

    default boolean isWithMenu() {
        return true;
    }

    /**
     * Get currente menupath
     *
     * @return
     */
    default String getMenuPath() {
        List<String> paths = new ArrayList<>();
        MenuEntry    entry = this;
        while (entry != null) {
            paths.add(entry.getName());
            entry = entry.getParent();
        }
        return Lists.reverse(paths).stream()
                .map(i -> SingularUtil.convertToJavaIdentity(i, true).toLowerCase())
                .collect(Collectors.joining("/"));
    }

    default String getEndpoint(){
        return StudioWicketUtils.getMergedPathIntoURL(StudioPage.class, getMenuPath());
    }
}