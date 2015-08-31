package br.net.mirante.singular.view.page.processo;

import java.util.Iterator;

import javax.inject.Inject;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.resource.DynamicImageResource;

import br.net.mirante.singular.dao.DefinitionDTO;
import br.net.mirante.singular.dao.InstanceDTO;
import br.net.mirante.singular.service.ProcessDefinitionService;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxLink;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.template.Content;

public class InstanciasContent extends Content implements SingularWicketContainer<InstanciasContent, Void> {

    @Inject
    private ProcessDefinitionService processDefinitionService;

    private DefinitionDTO processDefinition;

    private final Form<?> diagramForm = new Form<>("diagramForm");
    private final BSModalBorder diagramModal = new BSModalBorder("diagramModal");
    private final WebMarkupContainer diagram = new WebMarkupContainer("diagram");

    public InstanciasContent(String id, boolean withSideBar, Long processDefinitionId) {
        super(id, false, withSideBar, true);
        this.processDefinition = processDefinitionService.retrieveById(processDefinitionId);
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

        queue(new Label("processNameTitle", processDefinition.getNome()));
        queue(new ActionAjaxLink<Void>("showDiagramButton") {
            @Override
            protected void onAction(AjaxRequestTarget target) {
                getPage().getPageParameters().add("sigla", processDefinition.getSigla());
                /* FIXME: Verificar como detectar o fim da carga! */
                //target.appendJavaScript("Metronic.blockUI({target:'.modal-body',animate:true});");
                diagramModal.show(target);
            }
        });

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
    protected String getContentTitlelKey() {
        return "label.content.title";
    }

    @Override
    protected String getContentSubtitlelKey() {
        return "label.content.subtitle";
    }
}
