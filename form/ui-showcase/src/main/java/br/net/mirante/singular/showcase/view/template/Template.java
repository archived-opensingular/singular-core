package br.net.mirante.singular.showcase.view.template;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeAction;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.resource.PackageResourceReference;

import br.net.mirante.singular.showcase.view.skin.SkinOptions;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

@AuthorizeAction(action = Action.RENDER, roles = Roles.ADMIN)
public abstract class Template extends WebPage {

    private List<String> initializerJavascripts = Collections.singletonList("App.init();");
    private SkinOptions option;

    static {
        SkinOptions.addSkin(new SkinOptions.Skin("Vermelho",
                CssReferenceHeaderItem.forUrl("/singular-static/resources/metronic/layout4/css/themes/default.css"),
                CssReferenceHeaderItem.forUrl("resources/custom/css/red.css")));
        SkinOptions.addSkin(new SkinOptions.Skin("Verde",
                CssReferenceHeaderItem.forUrl("/singular-static/resources/metronic/layout4/css/themes/default.css"),
                CssReferenceHeaderItem.forUrl("resources/custom/css/green.css")));
        SkinOptions.addSkin(new SkinOptions.Skin("Anvisa",
                CssReferenceHeaderItem.forUrl("/singular-static/resources/anvisa/anvisa.css")));
    }

    @Override
    protected void onInitialize() {
        this.option = SkinOptions.op();

        super.onInitialize();
        add(new Label("pageTitle", new ResourceModel(getPageTitleLocalKey())));
        add(new WebMarkupContainer("pageBody")
                .add($b.attrAppender("class", "page-full-width", " ", $m.ofValue(!withMenu()))));
        queue(new Header("_Header", withMenu(), withTopAction(), withSideBar(), option));
//        queue(new WebMarkupContainer("_Menu"));
        queue(withMenu() ? new Menu("_Menu") : new WebMarkupContainer("_Menu"));
        queue(configureContent("_Content"));
        queue(new Footer("_Footer"));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(Template.class, "Template.css")));
        if (withSideBar()) {
            addQuickSidebar(response);
        }
        for (String script : initializerJavascripts) {
            response.render(OnDomReadyHeaderItem.forScript(script));
        }

        setSkin(response);
    }

    private void setSkin(IHeaderResponse response) {
        Optional<SkinOptions.Skin> skin = option.currentSkin();
        if (skin.isPresent()) {
            skin.get().refs.forEach(response::render);
        }
    }

    protected boolean withTopAction() {
        return false;
    }

    protected boolean withSideBar() {
        return false;
    }

    protected boolean withMenu() {
        return true;
    }

    protected String getPageTitleLocalKey() {
        return "label.page.title.local";
    }

    protected abstract Content getContent(String id);

    private Content configureContent(String contentId) {
        if (withSideBar()) {
            return getContent(contentId).addSideBar();
        } else {
            return getContent(contentId);
        }
    }

    private void addQuickSidebar(IHeaderResponse response) {
        response.render(JavaScriptReferenceHeaderItem.forUrl("/singular-static/resources/metronic/layout4/scripts/quick-sidebar.js"));
        StringBuilder script = new StringBuilder();
        script.append("jQuery(document).ready(function () {\n")
                .append("    QuickSidebar.init(); // init quick sidebar\n")
                .append("});");
        response.render(OnDomReadyHeaderItem.forScript(script));
    }

    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);
        Object payload = event.getPayload();
        if (payload instanceof AjaxRequestTarget) {
            AjaxRequestTarget target = (AjaxRequestTarget) payload;
            target.addListener(new AjaxRequestTarget.IListener() {
                @Override
                public void onBeforeRespond(Map<String, Component> map, AjaxRequestTarget target) {
                }

                @Override
                public void onAfterRespond(Map<String, Component> map, AjaxRequestTarget.IJavaScriptResponse response) {
                    if (!map.isEmpty()) {
                        initializerJavascripts.forEach(response::addJavaScript);
                    }
                }

                @Override
                public void updateAjaxAttributes(AbstractDefaultAjaxBehavior behavior, AjaxRequestAttributes attributes) {

                }
            });
        }
    }

}
