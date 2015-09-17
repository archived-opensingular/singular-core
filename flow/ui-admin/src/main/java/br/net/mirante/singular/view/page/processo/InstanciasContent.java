package br.net.mirante.singular.view.page.processo;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.DynamicImageResource;

import br.net.mirante.singular.dao.MetaDataDTO;
import br.net.mirante.singular.flow.core.dto.IDefinitionDTO;
import br.net.mirante.singular.flow.core.dto.IInstanceDTO;
import br.net.mirante.singular.service.ProcessDefinitionService;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.lambda.IFunction;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.wicket.UIAdminWicketFilterContext;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public class InstanciasContent extends Content implements SingularWicketContainer<InstanciasContent, Void> {

    @Inject
    private UIAdminWicketFilterContext uiAdminWicketFilterContext;

    @Inject
    private ProcessDefinitionService processDefinitionService;

    private IDefinitionDTO processDefinition;

    public InstanciasContent(String id, boolean withSideBar, Long processDefinitionId) {
        super(id, false, withSideBar, false, true);
        processDefinition = processDefinitionService.retrieveById(processDefinitionId);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        final AbstractReadOnlyModel<DynamicImageResource> imageModel =
                new AbstractReadOnlyModel<DynamicImageResource>() {
                    @Override
                    public DynamicImageResource getObject() {
                        DynamicImageResource dir = new DynamicImageResource() {
                            @Override
                            protected byte[] getImageData(Attributes attributes) {
                                return processDefinitionService.retrieveProcessDiagram(processDefinition.getSigla());
                            }
                        };
                        dir.setFormat("image/png");
                        return dir;
                    }
                };

        BaseDataProvider<IInstanceDTO, String> dataProvider = new BaseDataProvider<IInstanceDTO, String>() {
            @Override
            public Iterator<? extends IInstanceDTO> iterator(int first, int count,
                    String sortProperty, boolean ascending) {
                return processDefinitionService.retrieveAll(first, count, sortProperty, ascending,
                        processDefinition.getCod()).iterator();
            }

            @Override
            public long size() {
                return processDefinitionService.countAll(processDefinition.getCod());
            }
        };

        queue(new BSDataTableBuilder<>(dataProvider)
                .appendPropertyColumn(getMessage("label.table.column.description"), "description", IInstanceDTO::getDescricao)
                .appendPropertyColumn(getMessage("label.table.column.time"), "delta", IInstanceDTO::getDeltaString)
                .appendPropertyColumn(getMessage("label.table.column.date"), "date", IInstanceDTO::getDataInicialString)
                .appendPropertyColumn(getMessage("label.table.column.delta"), "deltas", IInstanceDTO::getDeltaAtividadeString)
                .appendPropertyColumn(getMessage("label.table.column.dates"), "dates", IInstanceDTO::getDataAtividadeString)
                .appendPropertyColumn(getMessage("label.table.column.user"), "user", IInstanceDTO::getUsuarioAlocado)
                .build("processos"));
        queue(new NonCachingImage("tabImage", imageModel));
        queue(mountMetadatas());
    }

    private RepeatingView mountMetadatas() {
        final List<MetaDataDTO> metadatas = processDefinitionService.retrieveMetaData(processDefinition.getCod());
        final RepeatingView metadatasRow = new RepeatingView("metadatasRow");
        for (MetaDataDTO metadata : metadatas) {
            int max = Math.max(metadata.getTransactions().size(), 1);
            for (int i = 0; i < max; i++) {
                final WebMarkupContainer metadataRow = new WebMarkupContainer(metadatasRow.newChildId());
                metadataRow.add(createMetadatasCol(metadata, i));
                metadatasRow.add(metadataRow);
            }
        }
        return metadatasRow;
    }

    private RepeatingView createMetadatasCol(MetaDataDTO metadata, int index) {
        final RepeatingView metadatasCol = new RepeatingView("metadatasCol");
        addRowWithSpan(metadatasCol, metadata, index, MetaDataDTO::getTask);
        addRowWithSpan(metadatasCol, metadata, index, MetaDataDTO::getType);
        addRowWithSpan(metadatasCol, metadata, index, MetaDataDTO::getExecutor);
        /* Transaction */
        WebMarkupContainer metadataCol = new WebMarkupContainer(metadatasCol.newChildId());
        if (metadata.getTransactions().size() > index) {
            metadataCol.add(new Label("metadataLabel",
                    metadata.getTransactions().get(index).getSource()
                            .concat(" → ")
                            .concat(metadata.getTransactions().get(index).getTarget())));
        } else {
            metadataCol.add(new Label("metadataLabel", ""));
        }
        metadatasCol.add(metadataCol);
        /* Parameter */
        metadataCol = new WebMarkupContainer(metadatasCol.newChildId());
        if (metadata.getTransactions().size() > index
                && !metadata.getTransactions().get(index).getParameters().isEmpty()) {
            MetaDataDTO.TransactionDTO transaction = metadata.getTransactions().get(index);
            final RepeatingView parametersCol = new RepeatingView("metadataLabel");
            for (MetaDataDTO.ParameterDTO parameter : transaction.getParameters()) {
                WebMarkupContainer parameterFragment = new Fragment(parametersCol.newChildId(), "parameterFragment", this);
                parameterFragment.add(new Label("parameterLabel", parameter.getName())
                        .add($b.attrAppender("class", (parameter.isRequired()
                                ? "label-danger" : "label-success"), " ")));
                parametersCol.add(parameterFragment);
            }
            metadataCol.add(parametersCol);
        } else {
            metadataCol.add(new Label("metadataLabel", ""));
        }
        metadatasCol.add(metadataCol);
        return metadatasCol;
    }

    private void addRowWithSpan(RepeatingView metadatasCol, MetaDataDTO metadata, int index,
            IFunction<MetaDataDTO, String> fValue) {
        if (index == 0) {
            WebMarkupContainer metadataCol = new WebMarkupContainer(metadatasCol.newChildId());
            metadataCol.add(new Label("metadataLabel", fValue.apply(metadata)));
            metadatasCol.add(metadataCol);
            if (metadata.getTransactions().size() > 1) {
                metadataCol.add($b.attr("rowspan", metadata.getTransactions().size()));
            }
        }
    }

    @Override
    protected WebMarkupContainer getBreadcrumbLinks(String id) {
        WebMarkupContainer breadcrumb = new Fragment(id, "breadcrumbProcess", this);
        breadcrumb.add(new WebMarkupContainer("processListLink")
                .add($b.attr("href", uiAdminWicketFilterContext.getRelativeContext().concat("process"))));
        return breadcrumb;
    }

    @Override
    protected IModel<?> getContentTitlelModel() {
        return $m.ofValue(processDefinition.getNome());
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return new ResourceModel("label.content.subtitle");
    }
}
