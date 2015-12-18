package br.net.mirante.singular.form.wicket.mapper.selection;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MISimples;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.view.MSelecaoPorModalBuscaView;
import br.net.mirante.singular.form.mform.options.MISelectItem;
import br.net.mirante.singular.form.mform.options.MTipoSelectItem;
import com.google.common.base.Throwables;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.Response;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.mapper.ControlsFieldComponentMapper;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSControls;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.modal.BSModalWindow;

@SuppressWarnings({"serial","rawtypes","unchecked"})
public class SelectModalBuscaMapper implements ControlsFieldComponentMapper {


    public Component appendInput(MView view, BSContainer bodyContainer, 
        BSControls formGroup, IModel<? extends MInstancia> model, 
        IModel<String> labelModel) {
        if(view instanceof MSelecaoPorModalBuscaView){
            return formGroupAppender(formGroup, bodyContainer, model, (MSelecaoPorModalBuscaView) view);
        }
        throw new RuntimeException("SelectModalBuscaMapper only works with a MSelecaoPorModalBuscaView.");
    }

    protected Component formGroupAppender(BSControls formGroup, BSContainer modalContainer,
                                          IModel<? extends MInstancia> model,
                                          MSelecaoPorModalBuscaView view) {
        SelectInputModalContainer panel = new SelectInputModalContainer(
                                                model.getObject().getNome() + "inputGroup",
                                                formGroup,modalContainer,model,view);
        return panel.build();
    }


    @Override
    public String getReadOnlyFormattedText(IModel<? extends MInstancia> model) {
        final MInstancia mi = model.getObject();
        if (mi != null){
            if(mi instanceof MISimples && mi.getValor() != null) {
                return String.valueOf(mi.getValor());
            }else if(mi instanceof MISelectItem) {
                return ((MISelectItem)mi).getFieldValue();
            }
        }
        return StringUtils.EMPTY;
    }
}

