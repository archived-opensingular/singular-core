package br.net.mirante.singular.view.page.processo;

import java.util.Iterator;

import javax.inject.Inject;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.DynamicImageResource;

import br.net.mirante.singular.dao.DefinitionDTO;
import br.net.mirante.singular.dao.InstanceDTO;
import br.net.mirante.singular.service.ProcessDefinitionService;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.template.Content;

public class InstanciasContent extends Content implements SingularWicketContainer<InstanciasContent, Void> {

    @Inject
    private ProcessDefinitionService processDefinitionService;

    private DefinitionDTO processDefinition;

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

        queue(new Label("processNameTitle", processDefinition.getNome()));

        BaseDataProvider<InstanceDTO, String> dataProvider = new BaseDataProvider<InstanceDTO, String>() {
            @Override
            public Iterator<? extends InstanceDTO> iterator(int first, int count,
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
                .appendPropertyColumn(getMessage("label.table.column.description"), "description", InstanceDTO::getDescricao)
                .appendPropertyColumn(getMessage("label.table.column.time"), "delta", InstanceDTO::getDeltaString)
                .appendPropertyColumn(getMessage("label.table.column.date"), "date", InstanceDTO::getDataInicialString)
                .appendPropertyColumn(getMessage("label.table.column.delta"), "deltas", InstanceDTO::getDeltaAtividadeString)
                .appendPropertyColumn(getMessage("label.table.column.dates"), "dates", InstanceDTO::getDataAtividadeString)
                .appendPropertyColumn(getMessage("label.table.column.user"), "user", InstanceDTO::getUsuarioAlocado)
                .build("processos"));
        queue(new NonCachingImage("tabImage", imageModel));
    }

    @Override
    protected WebMarkupContainer getBreadcrumbLinks(String id) {
        return new Fragment(id, "breadcrumbProcess", this);
    }

    @Override
    protected IModel<?> getContentTitlelModel() {
        return new ResourceModel("label.content.title");
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return new ResourceModel("label.content.subtitle");
    }
}
