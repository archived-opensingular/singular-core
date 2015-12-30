package br.net.mirante.singular.form.wicket.panel;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
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
import br.net.mirante.singular.form.wicket.SingularFormContextWicket;
import br.net.mirante.singular.form.wicket.WicketBuildContext;
import br.net.mirante.singular.form.wicket.enums.ViewMode;
import br.net.mirante.singular.form.wicket.mapper.DefaultCompostoMapper;
import br.net.mirante.singular.form.wicket.model.MInstanciaCampoModel;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSGrid;

public class BSPanelGrid extends Panel {

    private Form<?> form = new Form<>("panel-form");
    private BSGrid container = new BSGrid("grid");
    private Map<String, List<String>> tabMap = new LinkedHashMap<>();

    private SingularFormContextWicket singularFormContextWicket;
    private WicketBuildContext ctx;

    public BSPanelGrid(String id) {
        super(id);
    }

    public void addTab(String headerText, List<String> subtree) {
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
        buildTabContent();
    }

    private Component buildTabControl() {

        return new ListView<String>("tab", tabMap.keySet().stream().collect(Collectors.toList())) {
            @Override
            protected void populateItem(ListItem<String> item) {

                final List<String> subtree = tabMap.get(item.getModelObject());
                String tabName = convertToJavaIdentity(item.getModelObject());

                if(item.getIndex() == 0){
                    item.add($b.classAppender("active"));
                }

                item.add($b.attr("data-tab-name", tabName));

                AjaxSubmitLink link = new AjaxSubmitLink("tabAnchor") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        buildTabContent();
                        for (String nomeTipo : subtree) {
                            MInstanciaCampoModel<MInstancia> subtree = new MInstanciaCampoModel<>(ctx.getModel(), nomeTipo);
                            WicketBuildContext child = ctx.createChild(getContainer().newGrid().newColInRow(), true, subtree);
                            child.init(ctx.getUiBuilderWicket(), ctx.getViewMode());
                            child.getUiBuilderWicket().build(child, child.getViewMode());
                        }

                        target.appendJavaScript("$('.nav-tabs li').removeClass('active');");
                        target.appendJavaScript("$('.nav-tabs li[data-tab-name=\"" + tabName + "\"]').addClass('active');");
                        target.add(form);

                    }

                };

                link.add(new Label("header-text", item.getModelObject()));

                item.add(link);
            }
        };
    }

    public static String convertToJavaIdentity(String original) {
        return convertToJavaIdentity(original, false, true);
    }

    public static String convertToJavaIdentity(String original, boolean firstCharacterUpperCase, boolean normalize) {
        if (normalize) {
            original = normalize(original);
        }
        StringBuilder sb = new StringBuilder(original.length());
        boolean nextUpper = false;
        for (char c : original.toCharArray()) {
            if (sb.length() == 0) {
                if (Character.isJavaIdentifierStart(c)) {
                    if (firstCharacterUpperCase) {
                        sb.append(Character.toUpperCase(c));
                    } else {
                        sb.append(Character.toLowerCase(c));
                    }
                }
            } else if (Character.isJavaIdentifierPart(c)) {
                if (nextUpper) {
                    c = Character.toUpperCase(c);
                    nextUpper = false;
                }
                sb.append(c);
            } else if (Character.isWhitespace(c)) {
                nextUpper = true;
            }
        }
        return sb.toString();
    }

    public static String normalize(String original) {
        return Normalizer.normalize(original, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    private void buildTabContent() {
        form.remove(container);
        container = new BSGrid("grid");
        form.add(container);

    }

    public BSGrid getContainer() {
        return container;
    }

    public SingularFormContextWicket getSingularFormContextWicket() {
        return singularFormContextWicket;
    }

    public void setSingularFormContextWicket(SingularFormContextWicket singularFormContextWicket) {
        this.singularFormContextWicket = singularFormContextWicket;
    }

    public WicketBuildContext getCtx() {
        return ctx;
    }

    public void setCtx(WicketBuildContext ctx) {
        this.ctx = ctx;
    }
}
