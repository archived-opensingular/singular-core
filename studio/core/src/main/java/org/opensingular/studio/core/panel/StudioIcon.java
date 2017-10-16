package org.opensingular.studio.core.panel;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.opensingular.lib.commons.ui.Icon;

import java.util.HashSet;
import java.util.Set;

public class StudioIcon extends WebMarkupContainer {

    private final IModel<Icon> iconModel;

    public StudioIcon(String id, IModel<Icon> iconModel) {
        super(id);
        this.iconModel = iconModel;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
        add(new ClassAttributeModifier() {
            @Override
            protected Set<String> update(Set<String> oldClasses) {
                Icon iconModelObject = iconModel.getObject();
                if (iconModelObject != null) {
                    Set<String> newClasses = new HashSet<>();
                    newClasses.add(iconModelObject.getCssClass());
                    return newClasses;
                }
                return oldClasses;
            }
        });
    }

}
