package org.opensingular.form.wicket.mapper;

import org.apache.wicket.markup.html.form.HiddenField;
import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.model.SInstanceValueModel;

public class InputHiddenMapper implements IWicketComponentMapper {
    @Override
    public void buildView(WicketBuildContext ctx) {
        ctx.getContainer().appendTag("input", false, "type=\"hidden\"",new HiddenField<>("zoom", new SInstanceValueModel<>(ctx.getModel())));
    }
}
