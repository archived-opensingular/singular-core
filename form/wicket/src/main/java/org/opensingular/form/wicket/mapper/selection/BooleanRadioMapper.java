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

package org.opensingular.form.wicket.mapper.selection;

import org.opensingular.form.SInstance;
import org.opensingular.form.view.SViewBooleanByRadio;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.model.IModel;

public class BooleanRadioMapper extends RadioMapper {

    public String getReadOnlyFormattedText(IModel<? extends SInstance> model) {
        final SInstance mi = model.getObject();
        Boolean valor = mi.getValue(Boolean.class);
        if (valor != null) {
            SViewBooleanByRadio booleanRadioView = (SViewBooleanByRadio) mi.getType().getView();
            if (valor) {
                return booleanRadioView.labelTrue();
            } else {
                return booleanRadioView.labelFalse();
            }
        }
        return StringUtils.EMPTY;
    }
}
