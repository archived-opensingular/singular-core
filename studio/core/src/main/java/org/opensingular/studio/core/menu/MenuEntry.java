package org.opensingular.studio.core.menu;

import com.google.common.collect.Lists;
import org.opensingular.lib.commons.base.SingularUtil;
import org.opensingular.lib.commons.ui.Icon;

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
     * @param parent
     */
    void setParent(MenuEntry parent);


    default String getEndpoint(){
        MenuView view = getView();
        if(view != null) {
            return view.getEndpoint(getMenuPath());
        }
        return null;
    }

    default String getMenuPath(){
        List<String> paths = new ArrayList<>();
        MenuEntry entry = this;
        while (entry != null) {
            paths.add(entry.getName());
            entry = entry.getParent();
        }
        return Lists.reverse(paths).stream()
                .map(i -> SingularUtil.convertToJavaIdentity(i, true).toLowerCase())
                .collect(Collectors.joining("/"));
    }

    MenuView getView();
}