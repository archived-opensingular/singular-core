package br.net.mirante.singular.view.component;

import javax.inject.Inject;

import java.util.Set;

import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import br.net.mirante.singular.bamclient.portlet.PortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletContext;
import br.net.mirante.singular.bamclient.portlet.PortletQuickFilter;
import br.net.mirante.singular.flow.core.authorization.AccessLevel;
import br.net.mirante.singular.service.FlowMetadataFacade;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import br.net.mirante.singular.wicket.UIAdminSession;

public class PortletView<C extends PortletConfig> extends Panel {

    @Inject
    private FlowMetadataFacade flowMetadataFacade;

    private final IModel<C> config;
    private final IModel<PortletContext> context = Model.of(new PortletContext());

    public PortletView(String id, C config, String processDefinitionCode) {
        super(id);
        this.config = Model.of(config);
        context.getObject().setProcessDefinitionCode(processDefinitionCode);
        context.getObject().setProcessDefinitionKeysWithAccess(getProcesseDefinitionsKeysWithAcess());
    }

    protected ViewResult buildViewResult() {
        return PortletViewConfigResolver.newViewResult("portletContent", config, context);
    }

    private Set<String> getProcesseDefinitionsKeysWithAcess() {
        return flowMetadataFacade.listProcessDefinitionKeysWithAccess(UIAdminSession.get().getUserId(),
                AccessLevel.LIST);
    }

    @Override
    protected void onInitialize() {
        final PortletConfig config = this.config.getObject();
        super.onInitialize();
        add(buildQuickFilters());
        add(new Label("title", Model.of(config.getTitle())));
        add(new Label("subtitle", Model.of(config.getSubtitle())));
        add(buildViewResult());
        add($b.classAppender(config.getPortletSize().getBootstrapSize()));
        setOutputMarkupId(true);
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
                item.add(new Behavior() {
                    @Override
                    public void onConfigure(Component component) {
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
                        super.onConfigure(component);
                    }
                });
                item.setOutputMarkupId(true);
                item.add(new Label("filterLabel", Model.of(item.getModelObject().getLabel())));
            }
        };
    }
}
