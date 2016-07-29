/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.commons.lambda.IConsumer;
import br.net.mirante.singular.form.SInstance;
import br.net.mirante.singular.form.type.basic.SPackageBasic;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.behavior.DisabledClassBehavior;
import br.net.mirante.singular.form.wicket.model.AttributeModel;
import br.net.mirante.singular.form.wicket.model.SInstanceValueModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSWellBorder;
import br.net.mirante.singular.util.wicket.bootstrap.layout.TemplatePanel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.feedback.ErrorLevelFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.model.IModel;

import static br.net.mirante.singular.form.wicket.mapper.SingularEventsHandlers.FUNCTION.ADD_MOUSEDOWN_HANDLERS;
import static br.net.mirante.singular.form.wicket.mapper.SingularEventsHandlers.FUNCTION.ADD_TEXT_FIELD_HANDLERS;

public class BooleanMapper implements IWicketComponentMapper {

    public void buildView(WicketBuildContext ctx) {

        final IModel<? extends SInstance> model = ctx.getModel();
        final BSControls formGroup = ctx.getContainer().newFormGroup();
        final AttributeModel<String> labelModel = new AttributeModel<>(model, SPackageBasic.ATR_LABEL);

        switch (ctx.getViewMode()) {
            case READ_ONLY:
                buildForVisualization(model, formGroup, labelModel);
                break;
            case EDIT:
                buildForEdition(ctx, model, formGroup, labelModel);
                break;
        }
    }

    private void buildForEdition(WicketBuildContext ctx, IModel<? extends SInstance> model, BSControls formGroup,
                                 AttributeModel<String> labelModel) {

        final CheckBox input = new CheckBox(model.getObject().getName(), new SInstanceValueModel<>(model));
        final Label label = buildLabel("_", labelModel);
        adjustJSEvents(label);
        formGroup.appendCheckbox(input, label);
        input.add(DisabledClassBehavior.getInstance());
        formGroup.appendFeedback(formGroup, new ErrorLevelFeedbackMessageFilter(FeedbackMessage.WARNING), IConsumer.noop());
        ctx.configure(this, input);
    }

    private void buildForVisualization(IModel<? extends SInstance> model, BSControls formGroup,
                                       AttributeModel<String> labelModel) {
        final Boolean checked;

        final SInstance mi = model.getObject();
        if ((mi != null) && (mi.getValue() != null)) {
            checked = (Boolean) mi.getValue();
        } else {
            checked = false;
        }

        String clazz = checked ? "fa fa-check-square" : "fa fa-square-o";
        String idSuffix = (mi != null) ? mi.getName() : StringUtils.EMPTY;
        TemplatePanel tp = formGroup.newTemplateTag(t -> ""
            + "<div wicket:id='" + "_well" + idSuffix + "'>"
            + "   <i class='" + clazz + "'></i> <span wicket:id='label'></span> "
            + " </div>");
        final BSWellBorder wellBorder = BSWellBorder.small("_well" + idSuffix);
        tp.add(wellBorder.add(buildLabel("label", labelModel)));
    }

    protected Label buildLabel(String id, AttributeModel<String> labelModel) {
        return (Label) new Label(id, labelModel.getObject())
            .setEscapeModelStrings(false);
    }

    @Override
    public void adjustJSEvents(Component comp) {
        comp.add(new SingularEventsHandlers(ADD_TEXT_FIELD_HANDLERS, ADD_MOUSEDOWN_HANDLERS));
    }

}