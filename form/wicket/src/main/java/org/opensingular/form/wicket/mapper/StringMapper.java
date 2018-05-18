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

package org.opensingular.form.wicket.mapper;

import static org.opensingular.lib.wicket.util.util.WicketUtils.*;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.CountDownBehaviour;
import org.opensingular.form.wicket.behavior.InputMaskBehavior;
import org.opensingular.form.wicket.behavior.InputMaskBehavior.Masks;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;

public class StringMapper extends AbstractControlsFieldComponentMapper {

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {
        final IModel<? extends SInstance> model = ctx.getModel();

        FormComponent<?> comp = new TextField<>(model.getObject().getName(),
                new SInstanceValueModel<>(model), String.class).setLabel(labelModel);

        formGroup.appendInputText(comp);

        Optional<Integer> maxSize = Optional.ofNullable(
                model.getObject().getAttributeValue(SPackageBasic.ATR_MAX_LENGTH));
        if (maxSize.isPresent()) {
            comp.add($b.attr("maxlength", maxSize.get()));
            comp.add(new CountDownBehaviour());
        }

        Optional<String> basicMask = Optional.ofNullable(
                model.getObject().getAttributeValue(SPackageBasic.ATR_BASIC_MASK));
        if (basicMask.isPresent()) {
            comp.add(new InputMaskBehavior(Masks.valueOf(basicMask.get())));
            comp.setOutputMarkupId(true);
        }

        Optional.ofNullable(model.getObject()
                .getAttributeValue(SPackageBasic.ATR_UPPER_CASE_TEXT)
        ).filter(x -> x)
                .ifPresent(value -> {
                    comp.add($b.attrAppender("style", "text-transform: uppercase", ";"));
                });

        return comp;
    }

    @Override
    public String getReadOnlyFormattedText(WicketBuildContext ctx, IModel<? extends SInstance> model) {
        final SInstance mi = model.getObject();
        if ((mi != null) && (mi.getValue() != null)) {
            return String.valueOf(mi.getValue());
        }
        return StringUtils.EMPTY;
    }

}