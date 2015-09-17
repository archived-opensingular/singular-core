package br.net.mirante.singular.view.page.processo;

import java.util.Iterator;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;

import br.net.mirante.singular.flow.core.dto.IDefinitionDTO;
import br.net.mirante.singular.service.ProcessDefinitionService;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.template.Content;

public class ProcessosContent extends Content implements SingularWicketContainer<ProcessosContent, Void> {

    @Inject
    private ProcessDefinitionService processDefinitionService;

    private final Form<?> diagramForm = new Form<>("diagramForm");
    private final BSModalBorder diagramModal = new BSModalBorder("diagramModal");
    private final WebMarkupContainer diagram = new WebMarkupContainer("diagram");

    public ProcessosContent(String id, boolean withSideBar) {
        super(id, false, withSideBar, false, true);
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
                                String[] siglas = ((ServletWebRequest) attributes.getRequest())
                                        .getContainerRequest().getParameterMap().get("sigla");
                                String sigla = siglas[siglas.length - 1];
                                return processDefinitionService.retrieveProcessDiagram(sigla);
                            }
                        };
                        dir.setFormat("image/png");
                        return dir;
                    }
                };

        BaseDataProvider<IDefinitionDTO, String> dataProvider = new BaseDataProvider<IDefinitionDTO, String>() {
            @Override
            public Iterator<? extends IDefinitionDTO> iterator(int first, int count,
                    String sortProperty, boolean ascending) {
                return processDefinitionService.retrieveAll(first, count, sortProperty, ascending).iterator();
            }

            @Override
            public long size() {
                return processDefinitionService.countAll();
            }
        };

        queue(new BSDataTableBuilder<>(dataProvider)
                .appendPropertyColumn(getMessage("label.table.column.code"), "cod", IDefinitionDTO::getCod)
                .appendPropertyColumn(getMessage("label.table.column.name"), "name", IDefinitionDTO::getNome)
                .appendPropertyColumn(getMessage("label.table.column.category"), "category", IDefinitionDTO::getCategoria)
                .appendPropertyColumn(getMessage("label.table.column.quantity"), "quantity", IDefinitionDTO::getQuantidade)
                .appendPropertyColumn(getMessage("label.table.column.time"), "time", IDefinitionDTO::getTempoMedioString)
                .appendPropertyColumn(getMessage("label.table.column.throu"), "throu", IDefinitionDTO::getThroughput)
                .appendPropertyColumn(getMessage("label.table.column.version"), "version", IDefinitionDTO::getVersion)
                .appendColumn(new BSActionColumn<IDefinitionDTO, String>(WicketUtils.$m.ofValue(""))
                        .appendAction(getMessage("label.table.column.view"), Icone.EYE, (target, model) -> {
                            getPage().getPageParameters().add("sigla", model.getObject().getSigla());
                            /* FIXME: Verificar como detectar o fim da carga! */
                            //target.appendJavaScript("Metronic.blockUI({target:'.modal-body',animate:true});");
                            diagramModal.show(target);
                        })
                        .appendAction(getMessage("label.table.column.detail"), Icone.REDO, (target, model) -> {
                            setResponsePage(ProcessosPage.class,
                                    new PageParameters().add(
                                            ProcessosPage.PROCESS_DEFINITION_ID_PARAM, model.getObject().getCod()));
                        }))
                .build("processos"));

        diagramModal.setSize(BSModalBorder.Size.FIT);
        diagramModal.setTitleText(getMessage("label.modal.title"));
        diagramModal.addButton(BSModalBorder.ButtonStyle.DEFAULT, getMessage("label.modal.button.close"),
                new ActionAjaxButton("close", diagramForm) {
                    @Override
                    protected void onAction(AjaxRequestTarget target, Form<?> form) {
                        diagramModal.hide(target);
                    }
                });

        queue(diagramForm);
        queue(diagramModal.add(diagram.add(new NonCachingImage("image", imageModel))));
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
