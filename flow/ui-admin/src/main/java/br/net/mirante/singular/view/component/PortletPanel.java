package br.net.mirante.singular.view.component;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import br.net.mirante.singular.bamclient.portlet.FilterConfig;
import br.net.mirante.singular.bamclient.portlet.PortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletContext;
import br.net.mirante.singular.bamclient.portlet.PortletQuickFilter;
import br.net.mirante.singular.bamclient.portlet.filter.AggregationPeriod;
import br.net.mirante.singular.flow.core.authorization.AccessLevel;
import br.net.mirante.singular.form.mform.MDicionario;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.ServiceRef;
import br.net.mirante.singular.form.mform.core.attachment.handlers.InMemoryAttachmentPersitenceHandler;
import br.net.mirante.singular.form.mform.document.SDocument;
import br.net.mirante.singular.form.mform.options.MFixedOptionsSimpleProvider;
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.wicket.component.BelverSaveButton;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.service.FlowMetadataFacade;
import br.net.mirante.singular.spring.SpringServiceRegistry;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import br.net.mirante.singular.wicket.UIAdminSession;

public class PortletPanel<C extends PortletConfig> extends Panel {

    @Inject
    private FlowMetadataFacade flowMetadataFacade;

    @Inject
    private SpringServiceRegistry springServiceRegistry;

    private final IModel<C> config;
    private final IModel<PortletContext> context;
    private final IModel<String> footerLabel;
    private final BSModalBorder modalBorder = new BSModalBorder("filter");
    private final Form portletForm = new Form("porletForm");

    public PortletPanel(String id, C config, String processDefinitionCode, int portletIndex) {
        this(id, config, processDefinitionCode, portletIndex, null);
    }

    public PortletPanel(String id, C config, String processDefinitionCode, int portletIndex, String footer) {
        super(id);
        Objects.requireNonNull(config, "Configuração é obrigatória");
        final PortletContext portletContext = new PortletContext();
        portletContext.setPortletIndex(portletIndex);
        portletContext.setProcessDefinitionCode(processDefinitionCode);
        portletContext.setProcessDefinitionKeysWithAccess(getProcesseDefinitionsKeysWithAcess());
        portletContext.setFilterClassName(config.getFilterClassName());
        this.config = Model.of(config);
        this.context = Model.of(portletContext);
        footerLabel = Model.of(buildFooterLabel(footer));
    }

    protected ViewResultPanel buildViewResult() {
        return PortletViewConfigResolver.newViewResult("portletContent", config, context);
    }

    private Set<String> getProcesseDefinitionsKeysWithAcess() {
        return flowMetadataFacade.listProcessDefinitionKeysWithAccess(UIAdminSession.get().getUserId(), AccessLevel.LIST);
    }

    @Override
    protected void onInitialize() {
        final PortletConfig config = this.config.getObject();
        super.onInitialize();

        portletForm.add(new Label("title", Model.of(config.getTitle())));
        portletForm.add(new Label("subtitle", Model.of(config.getSubtitle())));

        portletForm.add(buildQuickFilters());
        portletForm.add(buildOpenFilterButton());
        portletForm.add(buildViewResult());

        buildFilters();

        modalBorder.setTitleText(Model.of(String.format("Filtrar %s", config.getTitle().toLowerCase())));
        portletForm.add(modalBorder);

        portletForm.add($b.classAppender(config.getPortletSize().getBootstrapSize()));

        add(portletForm);
        portletForm.setOutputMarkupId(true);
        setOutputMarkupId(true);
        queue(new WebMarkupContainer("footer")
                .add(new Label("footerLabel", footerLabel)));
    }

    private String buildFooterLabel(String footer) {
        if (footer == null) {
            return "";
        } else {
            return String.format("%s: %s", getString("label.chart.process"), footer);
        }
    }

    private void buildFilters() {
        final SingularFormPanel panel = new SingularFormPanel("singularFormPanel", springServiceRegistry) {
            @Override
            protected MTipo<?> getTipo() {
                final PacoteBuilder builder = MDicionario.create().criarNovoPacote("pacote");
                final MTipoComposto<? extends MIComposto> filtro = builder.createTipoComposto("filtro");
                appendFilters(filtro, config.getObject().getFilterConfigs());
                return filtro;
            }

            @Override
            protected void bindDefaultServices(SDocument document) {
                document.setAttachmentPersistenceHandler(ServiceRef.of(new InMemoryAttachmentPersitenceHandler()));
                document.addServiceRegistry(getServiceRegistry());
            }
        };

        final BelverSaveButton saveButton = new BelverSaveButton("filterButton") {
            @Override
            protected void handleSaveXML(AjaxRequestTarget target, MElement xml) {
                if (xml != null) {
                    System.out.println(xml);
                    final String jsonSource = xml.toJSONString();
                    final JSONObject filtro = new JSONObject(jsonSource).getJSONObject("filtro");
                    System.out.println(filtro);
                    if (filtro != null) {
                        context.getObject().setSerializedJSONFilter(filtro.toString());
                    } else {
                        context.getObject().setSerializedJSONFilter(null);
                    }
                } else {
                    context.getObject().setSerializedJSONFilter(null);
                }
                modalBorder.hide(target);
            }

            @Override
            public IModel<? extends MInstancia> getCurrentInstance() {
                return panel.getRootInstance();
            }
        };
        saveButton.add($b.attr("value", "Filtrar"));
        modalBorder.addButton(BSModalBorder.ButtonStyle.PRIMARY, Model.of("Filtrar"), saveButton);
        modalBorder.add(panel);
    }

    protected Component buildOpenFilterButton() {
        return new AjaxButton("openFilterButton") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                super.onSubmit(target, form);
                modalBorder.show(target);
            }

            @Override
            protected void onConfigure() {
                setVisible(!config.getObject().getFilterConfigs().isEmpty());
                super.onConfigure();
            }
        };
    }

    private Component buildQuickFilters() {
        return new ListView<PortletQuickFilter>("quickFilterOptions", config.getObject().getQuickFilter()) {
            @Override
            protected void populateItem(ListItem<PortletQuickFilter> item) {
                item.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        target.add(getParent());
                        context.getObject().setQuickFilter(item.getModelObject());
                    }
                });
                item.add(WicketUtils.$b.onConfigure(component -> {
                    component.add(new ClassAttributeModifier() {
                        @Override
                        protected Set<String> update(Set<String> oldClasses) {
                            if (item.getModelObject().equals(context.getObject().getQuickFilter())) {
                                oldClasses.add("active");
                            } else {
                                oldClasses.remove("active");
                            }
                            return oldClasses;
                        }
                    });
                }));
                item.setOutputMarkupId(true);
                item.add(new Label("filterLabel", Model.of(item.getModelObject().getLabel())));
            }
        };
    }

    private void appendFilters(MTipoComposto root, List<FilterConfig> filterConfigs) {
        filterConfigs.forEach(fc -> {
            MTipoSimples field = null;
            switch (fc.getFieldType()) {
                case BOOLEAN:
                    field = root.addCampoBoolean(fc.getIdentifier());
                    break;
                case INTEGER:
                    field = root.addCampoInteger(fc.getIdentifier());
                    break;
                case TEXT:
                    field = root.addCampoString(fc.getIdentifier());
                    break;
                case TEXTAREA:
                    field = root.addCampoString(fc.getIdentifier()).withTextAreaView();
                    break;
                case DATE:
                    field = root.addCampoData(fc.getIdentifier());
                    break;
                case SELECTION:
                    field = root.addCampoString(fc.getIdentifier());
                    final MFixedOptionsSimpleProvider selectionProvider = field.withSelection();
                    if (!StringUtils.isEmpty(fc.getRestEndpoint())) {
                        final String connectionURL = getGroupConnectionURL(context.getObject().getProcessDefinitionCode());
                        if (!StringUtils.isEmpty(connectionURL)) {
                            final String fullConnectionPoint = connectionURL + fc.getRestEndpoint();
                            switch (fc.getRestReturnType()) {
                                case VALUE:
                                    fillValueOptions(selectionProvider, fullConnectionPoint);
                                    break;
                                case KEY_VALUE:
                                    fillKeyValueOptions(selectionProvider, fullConnectionPoint);
                                    break;
                            }
                        }
                    } else if (fc.getOptions() != null && fc.getOptions().length > 0) {
                        Arrays.asList(fc.getOptions()).forEach(selectionProvider::add);
                    }
                    break;
                case AGGREGATION_PERIOD:
                    field = root.addCampoString(fc.getIdentifier());
                    final MFixedOptionsSimpleProvider aggregationProvider = field.withSelection();
                    Arrays.asList(AggregationPeriod.values()).forEach(ap -> aggregationProvider.add(ap, ap.getDescription()));
                    break;
            }
            if (field != null) {
                field.asAtrBasic().label(fc.getLabel());
                field.asAtrBootstrap().colPreference(fc.getSize());
            }
        });
    }

    public void fillValueOptions(MFixedOptionsSimpleProvider provider, String endpoint) {
        final RestTemplate restTemplate = new RestTemplate();
        final List<String> list = restTemplate.getForObject(endpoint, List.class);
        if (list != null) {
            list.forEach(provider::add);
        }
    }

    public void fillKeyValueOptions(MFixedOptionsSimpleProvider provider, String endpoint) {
        final RestTemplate restTemplate = new RestTemplate();
        final Map<String, String> map = restTemplate.getForObject(endpoint, Map.class);
        if (map != null) {
            map.forEach(provider::add);
        }
    }

    private String getGroupConnectionURL(String processAbbreviation) {
        return flowMetadataFacade.retrieveGroupByProcess(processAbbreviation).getConnectionURL();
    }


}