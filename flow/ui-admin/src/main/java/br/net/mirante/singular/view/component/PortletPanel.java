package br.net.mirante.singular.view.component;

import javax.inject.Inject;

import java.util.List;
import java.util.Set;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.json.JSONObject;

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
import br.net.mirante.singular.form.util.xml.MElement;
import br.net.mirante.singular.form.wicket.component.BelverSaveButton;
import br.net.mirante.singular.form.wicket.panel.SingularFormPanel;
import br.net.mirante.singular.service.FlowMetadataFacade;
import br.net.mirante.singular.spring.SpringServiceRegistry;
import br.net.mirante.singular.util.wicket.modal.BSModalBorder;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import br.net.mirante.singular.wicket.UIAdminSession;

public class PortletPanel<C extends PortletConfig> extends Panel {

    @Inject
    private FlowMetadataFacade flowMetadataFacade;

    @Inject
    private SpringServiceRegistry springServiceRegistry;

    private final IModel<C> config;
    private final IModel<PortletContext> context;
    private final BSModalBorder modalBorder = new BSModalBorder("filter");
    private final Form portletForm = new Form("porletForm");

    public PortletPanel(String id, C config, String processDefinitionCode, int portletIndex) {
        super(id);
        final PortletContext portletContext = new PortletContext();
        portletContext.setPortletIndex(portletIndex);
        portletContext.setProcessDefinitionCode(processDefinitionCode);
        portletContext.setProcessDefinitionKeysWithAccess(getProcesseDefinitionsKeysWithAcess());
        portletContext.setFilterClassName(config.getFilterClassName());
        this.config = Model.of(config);
        this.context = Model.of(portletContext);
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
                    final String jsonSource = xml.toJSONString();
                    final JSONObject filtro = new JSONObject(jsonSource).getJSONObject("filtro");
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
                    field = root.addCampoBoolean(fc.getIdentificador());
                    break;
                case INTEGER:
                    field = root.addCampoInteger(fc.getIdentificador());
                    break;
                case TEXT:
                    field = root.addCampoString(fc.getIdentificador());
                    break;
                case TEXTAREA:
                    field = root.addCampoString(fc.getIdentificador()).withTextAreaView();
                    break;
                case DATE:
                    field = root.addCampoData(fc.getIdentificador());
                    break;
                case AGGREGATION_PERIOD:
                    field = root.addCampoString(fc.getIdentificador());
                    field.withSelection()
                            .add(AggregationPeriod.WEEKLY, AggregationPeriod.BIMONTHLY.getDescription())
                            .add(AggregationPeriod.MONTHLY, AggregationPeriod.MONTHLY.getDescription())
                            .add(AggregationPeriod.BIMONTHLY, AggregationPeriod.BIMONTHLY.getDescription())
                            .add(AggregationPeriod.YEARLY, AggregationPeriod.YEARLY.getDescription())
                    ;
                    break;
            }
            if (field != null) {
                field.asAtrBasic().label(fc.getLabel());
                field.asAtrBootstrap().colPreference(fc.getSize());
            }
        });
    }

}