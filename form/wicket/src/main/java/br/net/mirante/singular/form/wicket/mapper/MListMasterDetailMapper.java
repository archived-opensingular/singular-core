package br.net.mirante.singular.form.wicket.mapper;

import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MILista;
import br.net.mirante.singular.form.mform.MISimples;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.SingularFormException;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.basic.view.MListMasterDetailView;
import br.net.mirante.singular.form.mform.basic.view.MView;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.components.MetronicPanel;
import br.net.mirante.singular.form.wicket.model.AtributoModel;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.form.wicket.model.MInstanciaItemListaModel;
import br.net.mirante.singular.form.wicket.model.MTipoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.datatable.BSDataTable;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;
import com.google.common.base.Strings;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;

import java.util.Iterator;
import java.util.Map;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;
import static br.net.mirante.singular.util.wicket.util.Shortcuts.$m;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@SuppressWarnings("serial")
public class MListMasterDetailMapper implements IWicketComponentMapper {

    @Override
    public void buildView(WicketBuildContext ctx, MView view, IModel<? extends MInstancia> model, ViewMode mode) {
        if (!(view instanceof MListMasterDetailView)) {
            throw new SingularFormException("Error: Mapper " +
                    MListMasterDetailMapper.class.getSimpleName() +
                    " must be associated with a view  of type" +
                    MListMasterDetailView.class.getName() +
                    ".");
        }

        final IModel<String> label = newLabelModel(model);

        ctx.getContainer().appendTag("div", true, null, new MetronicPanel("panel") {

            @Override
            protected void buildHeading(BSContainer<?> heading, Form<?> form) {
                heading.appendTag("span", new Label("_title", label));
                heading.add($b.visibleIf($m.get(() -> !Strings.isNullOrEmpty(label.getObject()))));
            }

            @Override
            protected void buildFooter(BSContainer<?> footer, Form<?> form) {
                footer.setVisible(false);
            }

            @Override
            protected void buildContent(BSContainer<?> content, Form<?> form) {
                content.appendTag("table", true, null, id -> buildTable(id, model, (MListMasterDetailView) view));
            }
        });
    }

    @SuppressWarnings("unchecked")
    private IModel<String> newLabelModel(IModel<? extends MInstancia> model) {
        IModel<MILista<MInstancia>> listaModel = $m.get(() -> (MILista<MInstancia>) model.getObject());
        MILista<?> iLista = listaModel.getObject();
        return $m.ofValue(trimToEmpty(iLista.as(MPacoteBasic.aspect()).getLabel()));
    }

    @SuppressWarnings("unchecked")
    private BSDataTable buildTable(String id, IModel<? extends MInstancia> model, MListMasterDetailView view) {

        BSDataTableBuilder builder = new BSDataTableBuilder<>(newDataProvider(model));

        configureColumns(view.getColumns(), builder, model);

        return builder.build(id);
    }


    private BaseDataProvider newDataProvider(final IModel<? extends MInstancia> model) {
        return new BaseDataProvider() {

            @Override
            public Iterator iterator(int first, int count, Object sortProperty, boolean ascending) {
                return ((MILista<MInstancia>) model.getObject()).iterator();
            }

            @Override
            public long size() {
                return ((MILista<MInstancia>) model.getObject()).size();
            }

            @Override
            public IModel model(Object object) {
                IModel<MILista<MInstancia>> listaModel = $m.get(() -> (MILista<MInstancia>) model.getObject());
                return new MInstanciaItemListaModel<>(listaModel, listaModel.getObject().indexOf((MInstancia) object));
            }
        };
    }

    private void configureColumns(Map<String, String> mapColumns, BSDataTableBuilder<MInstancia, ?, ?> builder, IModel<? extends MInstancia> model) {
        for (Map.Entry<String, String> entry : mapColumns.entrySet()) {

            MTipo mtipo = model.getObject().getDicionario().getTipo(entry.getKey());

            IModel<String> labelModel;
            String label = entry.getValue();

            if (label != null) {
                labelModel = $m.ofValue(label);
            } else {
                labelModel = $m.ofValue((String) mtipo.getValorAtributo(MPacoteBasic.ATR_LABEL.getNomeCompleto()));
            }

            propertyColumnAppender(builder, labelModel, new MTipoModel(mtipo));
        }
    }

    private void propertyColumnAppender(BSDataTableBuilder<MInstancia, ?, ?> builder, IModel<String> labelModel, IModel<MTipo<?>> mTipoModel) {
        builder.appendPropertyColumn(labelModel, o -> {
            MIComposto composto = (MIComposto) o;
            MTipoSimples mtipo = (MTipoSimples) mTipoModel.getObject();
            MISimples instancia = ((MISimples) composto.findDescendant(mtipo).get());
            return instancia.getValor();
        });
    }


}
