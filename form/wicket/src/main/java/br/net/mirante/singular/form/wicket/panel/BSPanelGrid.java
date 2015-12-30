package br.net.mirante.singular.form.wicket.panel;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
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

import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;

public abstract class BSPanelGrid extends Panel {

    private Form<?> form = new Form<>("panel-form");
    private BSGrid container = new BSGrid("grid");
    private Map<String, BSTab> tabMap = new LinkedHashMap<>();

    public BSPanelGrid(String id) {
        super(id);
    }

    public void addTab(String id, String headerText, List<String> subtree) {
        tabMap.put(id, new BSTab(headerText, subtree));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        rebuildForm();
    }

    private void rebuildForm() {
        add(form
                .add(buildTabControl()));
        buildTabContent();
    }

    private Component buildTabControl() {

        return new ListView<String>("tab", tabMap.keySet().stream().collect(Collectors.toList())) {
            @Override
            protected void populateItem(ListItem<String> item) {

                String id = item.getModelObject();
                final BSTab tab = tabMap.get(id);

                if(item.getIndex() == 0){
                    item.add($b.classAppender("active"));
                }

                item.add($b.attr("data-tab-name", id));

                AjaxSubmitLink link = new AjaxSubmitLink("tabAnchor") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        buildTabContent();
                        updateTab(tab.getSubtree());

                        target.appendJavaScript("$('.nav-tabs li').removeClass('active');");
                        target.appendJavaScript("$('.nav-tabs li[data-tab-name=\"" + id + "\"]').addClass('active');");
                        target.add(form);

                    }

                };

                link.add(new Label("header-text", tab.getHeaderText()));

                item.add(link);
            }
        };
    }

    public abstract void updateTab(List<String> subtree);

    public void buildTabContent() {
        form.remove(container);
        container = new BSGrid("grid");
        form.add(container);

    }

    public BSGrid getContainer() {
        return container;
    }

    private static final class BSTab implements Serializable {
        private String headerText;
        private List<String> subtree;

        public BSTab(String headerText, List<String> subtree) {
            this.headerText = headerText;
            this.subtree = subtree;
        }

        public String getHeaderText() {
            return headerText;
        }

        public List<String> getSubtree() {
            return subtree;
        }
    }
}
