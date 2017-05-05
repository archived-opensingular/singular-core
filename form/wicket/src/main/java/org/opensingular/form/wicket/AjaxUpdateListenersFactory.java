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

package org.opensingular.form.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.IModel;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.behavior.AjaxUpdateChoiceBehavior;
import org.opensingular.form.wicket.behavior.AjaxUpdateInputBehavior;
import org.opensingular.form.wicket.behavior.AjaxUpdateSingularFormComponentPanel;
import org.opensingular.form.wicket.component.SingularFormComponentPanel;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class AjaxUpdateListenersFactory {

    /**
     * Evento javascript padrão para ativar uma requisição ajax para validação do campo
     */
    public static final String SINGULAR_VALIDATE_EVENT = "singular:validate";

    /**
     * Evento javascript padrão para ativar uma requisição ajax para processamento do campo
     */
    public static final String SINGULAR_PROCESS_EVENT = "singular:process";

    public List<Behavior> getBehaviorsForm(Component component, IModel<SInstance> model, IAjaxUpdateListener listener) {
        List<Behavior> list = new ArrayList<>();
        if (isChoice(component)) {
            list.add(new AjaxUpdateChoiceBehavior(model, listener));
        } else if (isSingularFormComponentPanel(component)) {
            list.add(new AjaxUpdateSingularFormComponentPanel<>(model, listener));
        } else if (isNotFormComponentPanel(component)) {
            list.add(new AjaxUpdateInputBehavior(SINGULAR_VALIDATE_EVENT, model, true, listener));
            list.add(new AjaxUpdateInputBehavior(SINGULAR_PROCESS_EVENT, model, false, listener));
        } else {
            LoggerFactory.getLogger(AjaxUpdateListenersFactory.class).warn("Atualização ajax não suportada para {}", component);
        }
        return list;
    }

    public boolean isSingularFormComponentBehavior(Behavior b) {
        return b instanceof AjaxUpdateInputBehavior;
    }

    public boolean isSingularFormComponentPanelBehavior(Behavior b) {
        return b instanceof AjaxUpdateSingularFormComponentPanel;
    }

    public boolean isSingularChoiceBehavior(Behavior b) {
        return b instanceof AjaxUpdateChoiceBehavior;
    }

    public boolean isNotFormComponentPanel(Component component) {
        return !(component instanceof FormComponentPanel<?>);
    }

    public boolean isSingularFormComponentPanel(Component component) {
        return component instanceof SingularFormComponentPanel;
    }

    public boolean isChoice(Component component) {
        return (component instanceof RadioChoice) ||
                (component instanceof CheckBoxMultipleChoice) ||
                (component instanceof RadioGroup) ||
                (component instanceof CheckGroup);
    }


    public boolean isSingularBehavior(Behavior b) {
        return isSingularChoiceBehavior(b) || isSingularFormComponentBehavior(b) || isSingularFormComponentPanelBehavior(b);
    }
}
