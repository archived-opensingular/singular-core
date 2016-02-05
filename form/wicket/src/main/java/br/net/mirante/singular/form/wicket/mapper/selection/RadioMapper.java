package br.net.mirante.singular.form.wicket.mapper.selection;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.value.IValueMap;
import org.apache.wicket.util.value.ValueMap;

import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorRadioView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;

public class RadioMapper extends SelectMapper {

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    protected RadioChoice retrieveChoices(IModel<? extends SInstance> model,
                                          final IModel<? extends List<SelectOption>> opcoesValue, MView view) {
        if (!(view instanceof MSelecaoPorRadioView)) {
            throw new SingularFormException("View não suportada");
        }
        MSelecaoPorRadioView radioView = (MSelecaoPorRadioView) view;
        MSelectionInstanceModel opcoesModel = new MSelectionInstanceModel<SelectOption>(model);
        String id = model.getObject().getNome();
        return new RadioChoice<SelectOption>(id,
                (IModel) opcoesModel, opcoesValue, rendererer()) {
            @Override
            protected IValueMap getAdditionalAttributesForLabel(int index,
                                                                SelectOption choice) {

                IValueMap map = new ValueMap();
                if (radioView.getLayout() == MSelecaoPorRadioView.Layout.HORIZONTAL) {
                    map.put("class", "radio-inline");
                    map.put("style", "position:relative;top:-1px;padding-left:3px;padding-right:10px;");
                } else if (radioView.getLayout() == MSelecaoPorRadioView.Layout.VERTICAL) {
                    map.put("style", "position:relative;top:-1px;padding-left:3px;padding-right:10px;display:table-cell;");
                }

                return map;
            }

            @Override
            protected IValueMap getAdditionalAttributes(int index,
                                                        SelectOption choice) {
                IValueMap map = new ValueMap();
                map.put("style", "left:20px;");
                return map;
            }

            @Override
            protected void onConfigure() {
                this.setVisible(!opcoesModel.getObject().toString().isEmpty());
            }
        };
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Component formGroupAppender(BSControls formGroup, IModel<? extends SInstance> model,
                                          final IModel<? extends List<SelectOption>> opcoesValue, MView view) {
        if (!(view instanceof MSelecaoPorRadioView)) {
            throw new SingularFormException("View não suportada");
        }
        MSelecaoPorRadioView radioView = (MSelecaoPorRadioView) view;
        final RadioChoice<String> choices = retrieveChoices(model, opcoesValue, view);
        if (radioView.getLayout() == MSelecaoPorRadioView.Layout.HORIZONTAL) {
            choices.setPrefix("<span style=\"display: inline-block;white-space: nowrap;\">");
            choices.setSuffix("</span>");
        } else if (radioView.getLayout() == MSelecaoPorRadioView.Layout.VERTICAL) {
            choices.setPrefix("<span style='display: table;padding: 4px 0;'>");
            choices.setSuffix("</span>");
        }
        formGroup.appendRadioChoice(choices);
        return choices;
    }
}
