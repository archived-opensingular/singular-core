package br.net.mirante.singular.form.wicket.panel;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.context.SingularFormContext;
import br.net.mirante.singular.form.wicket.IWicketComponentMapper;
import br.net.mirante.singular.form.wicket.UIBuilderWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;

public class BSPanelGrid extends Panel {

    private Form<?> form = new Form<>("panel-form");
    private BSGrid container = new BSGrid("grid");
    private Map<String, String> tabMap = new LinkedHashMap<>();
    private String selectedSubtree;

    private SingularFormContext<UIBuilderWicket, IWicketComponentMapper> singularFormContext;
    private ViewMode viewMode;

    public BSPanelGrid(String id, IModel<MInstancia> model, SingularFormContext<UIBuilderWicket, IWicketComponentMapper> singularFormContext, ViewMode viewMode) {
        super(id, model);
        this.singularFormContext = singularFormContext;
        this.viewMode = viewMode;
        form.setOutputMarkupId(true);
        add(form.add(container));
    }

    public void addTab(String headerText, String subtree) {
        if (tabMap.isEmpty()) {
            selectedSubtree = subtree;
        }
        tabMap.put(headerText, subtree);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        rebuildForm();
    }

    private void rebuildForm() {
        add(form
                .add(buildTabControl()));
//        buildTabContent();
    }

    private Component buildTabControl() {

        return new ListView<String>("tab", tabMap.keySet().stream().collect(Collectors.toList())) {
            @Override
            protected void populateItem(ListItem<String> item) {

                final String subtree = tabMap.get(item.getModelObject());

                if(item.getIndex() == 0){
                    item.add($b.classAppender("active"));
                }

                item.add($b.attr("data-tab-name", subtree));

                AjaxSubmitLink link = new AjaxSubmitLink("tabAnchor") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        selectedSubtree = subtree;
                        buildTabContent();

                        target.appendJavaScript("$('.nav-tabs li').removeClass('active');");
                        target.appendJavaScript("$('.nav-tabs li[data-tab-name=\"" + subtree + "\"]').addClass('active');");
                        target.add(form);
                    }

                };

                link.add(new Label("header-text", item.getModelObject()));

                item.add(link);
            }
        };
    }

    private void buildTabContent() {
        form.remove(container);
        container = new BSGrid("grid");
        form.add(container);
        WicketBuildContext ctx = new WicketBuildContext(getContainer().newColInRow(), buildBodyContainer());
        singularFormContext.getUIBuilder().build(ctx, new MInstanciaCampoModel<>(getDefaultModel(), selectedSubtree), viewMode);
    }

    private BSContainer buildBodyContainer() {
        return (BSContainer) getParent().getParent().get("body-container");
    }

    public BSGrid getContainer() {
        return container;
    }
}
