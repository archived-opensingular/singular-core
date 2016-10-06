package org.opensingular.singular.form.wicket.mapper.selection;

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