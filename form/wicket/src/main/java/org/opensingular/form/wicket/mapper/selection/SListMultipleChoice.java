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

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.AppendingStringBuffer;
import org.apache.wicket.util.string.Strings;

import java.util.Collection;
import java.util.List;

public class SListMultipleChoice<T> extends ListMultipleChoice<T> {

    public SListMultipleChoice(String id) {
        super(id);
    }

    public SListMultipleChoice(String id, List<? extends T> choices) {
        super(id, choices);
    }

    public SListMultipleChoice(String id, List<? extends T> choices, int maxRows) {
        super(id, choices, maxRows);
    }

    public SListMultipleChoice(String id, List<? extends T> choices, IChoiceRenderer<? super T> renderer) {
        super(id, choices, renderer);
    }

    public SListMultipleChoice(String id, IModel<? extends Collection<T>> object, List<? extends T> choices) {
        super(id, object, choices);
    }

    public SListMultipleChoice(String id, IModel<? extends Collection<T>> object, List<? extends T> choices, IChoiceRenderer<? super T> renderer) {
        super(id, object, choices, renderer);
    }

    public SListMultipleChoice(String id, IModel<? extends List<? extends T>> choices) {
        super(id, choices);
    }

    public SListMultipleChoice(String id, IModel<? extends Collection<T>> model, IModel<? extends List<? extends T>> choices) {
        super(id, model, choices);
    }

    public SListMultipleChoice(String id, IModel<? extends List<? extends T>> choices, IChoiceRenderer<? super T> renderer) {
        super(id, choices, renderer);
    }

    public SListMultipleChoice(String id, IModel<? extends Collection<T>> model, IModel<? extends List<? extends T>> choices, IChoiceRenderer<? super T> renderer) {
        super(id, model, choices, renderer);
    }

    @Override
    public List<? extends T> getChoices() {
        if (!isEnabled()) {
            return (List<? extends T>) getModel().getObject();
        } else {
            return super.getChoices();
        }
    }

    @Override
    protected void setOptionAttributes(AppendingStringBuffer buffer, T choice, int index, String selected) {

        if (isEnabled() && isSelected(choice, index, selected)) {
            buffer.append("selected=\"selected\" ");
        }

        if (isDisabled(choice, index, selected)) {
            buffer.append("disabled=\"disabled\" ");
        }

        buffer.append("value=\"");
        buffer.append(Strings.escapeMarkup(getChoiceRenderer().getIdValue(choice, index)));
        buffer.append('"');

    }

}