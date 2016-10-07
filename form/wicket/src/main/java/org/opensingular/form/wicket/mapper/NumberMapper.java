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

import org.opensingular.form.SInstance;
import org.opensingular.form.type.basic.SPackageBasic;
import org.opensingular.form.wicket.WicketBuildContext;
import org.opensingular.form.wicket.behavior.InputMaskBehavior;
import org.opensingular.form.wicket.behavior.InputMaskBehavior.Masks;
import org.opensingular.form.wicket.model.SInstanceValueModel;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSControls;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

import java.util.HashMap;
import java.util.Optional;

public class NumberMapper<T extends Number> extends StringMapper {

    private static final int DEFAULT_SIZE = 9;

    private final Class<T> numberType;

    public NumberMapper(Class<T> numberType) {
        this.numberType = numberType;
    }

    @Override
    public Component appendInput(WicketBuildContext ctx, BSControls formGroup, IModel<String> labelModel) {

        final IModel<? extends SInstance> model = ctx.getModel();
        final Optional<Integer>           size  = Optional.ofNullable(model.getObject().getAttributeValue(SPackageBasic.ATR_MAX_LENGTH));
        final TextField<T>                comp  = new TextField<>(model.getObject().getName(), new SInstanceValueModel<>(model), numberType);

        formGroup
                .appendInputText(comp
                        .setLabel(labelModel)
                        .setOutputMarkupId(true)
                        .add(new InputMaskBehavior(Masks.NUMERIC, new HashMap<String, Object>() {{
                            put(InputMaskBehavior.MAX_LENGTH_ATTR, size.orElse(DEFAULT_SIZE));
                        }}))
                );

        return comp;
    }

}