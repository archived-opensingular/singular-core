package org.opensingular.studio.app.menu;


import com.google.common.collect.Lists;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.opensingular.form.SIComposite;
import org.opensingular.lib.commons.base.SingularUtil;
import org.opensingular.lib.commons.ui.Icon;
import org.opensingular.lib.wicket.util.datatable.BSDataTableBuilder;
import org.opensingular.studio.core.menu.ItemMenuEntry;
import org.opensingular.studio.core.menu.MenuEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.opensingular.studio.app.wicket.StudioApplication.STUDIO_ROOT_PATH;

public abstract class StudioMenuItem extends ItemMenuEntry {

    private final String repositoryBeanName;

    public StudioMenuItem(Icon icon, String name, String repositoryBeanName) {
        super(icon, name, null);
        this.repositoryBeanName = repositoryBeanName;
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

    public String getRepositoryBeanName() {
        return repositoryBeanName;
    }

    public abstract void configureTable(BSDataTableBuilder<SIComposite, String, IColumn<SIComposite, String>> dataTableBuilder);
}