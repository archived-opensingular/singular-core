package br.net.mirante.singular.view.page.processo;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

import java.util.Iterator;
import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.NonCachingImage;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.DynamicImageResource;

import br.net.mirante.singular.flow.core.authorization.AccessLevel;
import br.net.mirante.singular.flow.core.dto.IDefinitionDTO;
import br.net.mirante.singular.util.wicket.ajax.ActionAjaxButton;
import br.net.mirante.singular.util.wicket.datatable.BSDataTableBuilder;
import br.net.mirante.singular.util.wicket.datatable.BaseDataProvider;
import br.net.mirante.singular.util.wicket.datatable.column.BSActionColumn;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.resource.Icone;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import br.net.mirante.singular.view.SingularWicketContainer;
import br.net.mirante.singular.view.page.dashboard.DashboardPage;
import br.net.mirante.singular.view.template.Content;

public class ProcessosContent extends Content implements SingularWicketContainer<ProcessosContent, Void> {

    private final Form<?> diagramForm = new Form<>("diagramForm");
    private final BSModalBorder diagramModal = new BSModalBorder("diagramModal");
    private final WebMarkupContainer diagram = new WebMarkupContainer("diagram");

    public ProcessosContent(String id, boolean withSideBar) {
        super(id, false, withSideBar, false, true);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        Set<String> processCodeWithAccess = flowMetadataFacade.listProcessDefinitionKeysWithAccess(getUserId(), AccessLevel.LIST);
        if(processCodeWithAccess.isEmpty()){
            error(getString("error.user.without.access.to.process"));
        }
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
                        
                        return flowMetadataFacade.processDefinitionDiagram(uiAdminFacade.retrieveDefinitionByKey(sigla));
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
                return uiAdminFacade.retrieveAllDefinition(first, count, sortProperty, ascending, processCodeWithAccess).iterator();
            }
            
            @Override
            public long size() {
                return processCodeWithAccess.isEmpty()? 0L: uiAdminFacade.countAllDefinition(processCodeWithAccess);
            }
        };
        
        queue(new BSDataTableBuilder<>(dataProvider)
            .appendPropertyColumn(getMessage("label.table.column.code"), "cod", IDefinitionDTO::getCod)
            .appendPropertyColumn(getMessage("label.table.column.name"), "name", IDefinitionDTO::getNome)
            .appendPropertyColumn(getMessage("label.table.column.category"), "category", IDefinitionDTO::getCategoria)
            .appendPropertyColumn(getMessage("label.table.column.quantity"), "quantity", IDefinitionDTO::getQuantidade)
            .appendPropertyColumn(getMessage("label.table.column.time"), "time", IDefinitionDTO::getTempoMedioString)
            .appendPropertyColumn(getMessage("label.table.column.throu"), IDefinitionDTO::getThroughput)
            .appendPropertyColumn(getMessage("label.table.column.version"), IDefinitionDTO::getVersion)
            .appendColumn(new BSActionColumn<IDefinitionDTO, String>(WicketUtils.$m.ofValue(""))
                .appendAction(getMessage("label.table.column.view"), Icone.EYE, (target, model) -> {
                    getPage().getPageParameters().add("sigla", model.getObject().getSigla());
                    /* FIXME: Verificar como detectar o fim da carga! */
                    //target.appendJavaScript("App.blockUI({target:'.modal-body',animate:true});");
                    diagramModal.show(target);
                })
                .appendAction(getMessage("label.table.column.detail"), Icone.REDO, (target, model) -> {
                    setResponsePage(ProcessosPage.class,
                        new PageParameters().add(Content.PROCESS_DEFINITION_COD_PARAM, model.getObject().getSigla()));
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
        RepeatingView breadCrumb = new RepeatingView(id);
        
        breadCrumb.add(createBreadCrumbLink(breadCrumb.newChildId(), 
            urlFor(DashboardPage.class, new PageParameters()).toString(), 
            getString("breadcrumb.statistics")));
        return breadCrumb;
    }

    @Override
    protected IModel<?> getContentTitlelModel() {
        return new ResourceModel("label.content.title");
    }

    @Override
    protected IModel<?> getContentSubtitlelModel() {
        return $m.ofValue();
    }
}
