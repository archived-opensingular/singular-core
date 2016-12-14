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

package org.opensingular.form.exemplos.canabidiol.custom;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import org.opensingular.form.wicket.mapper.BooleanMapper;
import org.opensingular.form.wicket.model.AttributeModel;

/**
 * Mapper customizado para substituir os "\n"
 * do texto da label por "<br />"
 */
public class AceitoTudoMapper extends BooleanMapper {

    @Override
    protected Label buildLabel(String id, AttributeModel<String> labelModel) {
        String s = labelModel.getObject();
        s = s.replace("\n", "<br />");
        Label label = new Label(id, Model.of(s));
        label.setEscapeModelStrings(false);
        return label;
    }

}
