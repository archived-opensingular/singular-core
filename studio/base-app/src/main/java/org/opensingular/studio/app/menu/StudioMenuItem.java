package org.opensingular.studio.app.menu;


import com.google.common.collect.Lists;
import org.opensingular.lib.commons.base.SingularUtil;
import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.studio.app.definition.StudioDefinition;
import org.opensingular.studio.core.menu.ItemMenuEntry;
import org.opensingular.studio.core.menu.MenuEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.opensingular.studio.app.wicket.StudioApplication.STUDIO_ROOT_PATH;

public class StudioMenuItem extends ItemMenuEntry {

    private final StudioDefinition studioDefinition;

    public StudioMenuItem(Icon icon, String name, StudioDefinition studioDefinition) {
        super(icon, name, null);
        this.studioDefinition = studioDefinition;
    }

    @Override
    public String getEndpoint() {
        List<String> paths = new ArrayList<>();
        MenuEntry entry = this;
        while (entry != null) {
            paths.add(entry.getName());
            entry = entry.getParent();
        }
        return "/" + STUDIO_ROOT_PATH + "/" + Lists.reverse(paths).stream()
                .map(i -> SingularUtil.convertToJavaIdentity(i, true).toLowerCase())
                .collect(Collectors.joining("/"));
    }

    public StudioDefinition getStudioDefinition() {
        return studioDefinition;
    }
}