package org.opensingular.lib.wicket.util.modal;

import static org.opensingular.lib.wicket.util.util.Shortcuts.*;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.head.internal.HeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Response;
import org.apache.wicket.response.NullResponse;
import org.apache.wicket.util.tester.WicketTester;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.opensingular.lib.commons.lambda.IConsumer;
import org.opensingular.lib.wicket.util.SingleFormDummyPage;
import org.opensingular.lib.wicket.util.WicketUtilsDummyApplication;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSLabel;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.modal.BSModalBorder.Size;
import org.opensingular.lib.wicket.util.util.WicketEventUtils;
import org.opensingular.lib.wicket.util.util.WicketUtils;

public class BSModalBorderTest {

    @Test
    public void test() {
        WicketTester tester = new WicketTester(new WicketUtilsDummyApplication());

        tester.startPage(new SingleFormDummyPage() {
            @Override
            protected Component newContentPanel(String contentId) {
                BSModalBorder modal = new BSModalBorder("modal");
                return new TemplatePanel(contentId, ""
                    + "<a wicket:id='open'>Open</a>"
                    + "<div wicket:id='modal'>"
                    + "  <form wicket:id='innerForm'>"
                    + "    <span wicket:id='label'></span>"
                    + "  </form>"
                    + "  <input type='text' wicket:id='text'>"
                    + "</div>")
                        .add(modal
                            .addButton(BSModalBorder.ButtonStyle.DEFAULT, new AjaxButton("focus") {
                                @Override
                                protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                                    modal.focusFirstComponent(target);
                                    modal.isCloseIconVisible();
                                    modal.isWithAutoFocus();
                                    modal.getHideJavaScriptCallback();
                                    modal.removeButtons();
                                    modal.refreshContent(target);
                                }
                            })
                            .addLink(BSModalBorder.ButtonStyle.LINK, new AjaxLink<Void>("refresh") {
                                @Override
                                public void onClick(AjaxRequestTarget target) {
                                    WicketEventUtils.sendAjaxErrorEvent(this, target);
                                }
                            })
                            .setCloseIconCallback(IConsumer.noop())
                            .setCloseIconVisible(true)
                            .setDismissible(true)
                            .setMinimizable(true)
                            .setSize(Size.SMALL)
                            .setRenderModalBodyTag(true)
                            .setRenderModalFooterTag(true)
                            .setWithAutoFocus(true)
                            .setTitleText($m.ofValue("Title"))
                            .setDefaultModel(Model.of())
                            .add(new Form<>("innerForm")
                                .add(new BSLabel("label", "?")))
                            .addOrReplace(new TextField<>("text", Model.of())))
                        .add(new AjaxLink<Void>("open") {
                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                modal.show(target);
                            }
                        });
            }
        });

        BSModalBorder modal = WicketUtils.findFirstChild(tester.getLastRenderedPage(), BSModalBorder.class).get();

        Assert.assertNotNull(modal.getModalHeader());
        Assert.assertNotNull(modal.getModalBody());
        Assert.assertNotNull(modal.getModalFooter());

        String formPath = ((Form<?>) WicketUtils.findFirstChild(tester.getLastRenderedPage(), Form.class).get()).getPageRelativePath();
        String openPath = WicketUtils.findFirstChild(tester.getLastRenderedPage(), AjaxLink.class, it -> it.getId().equals("open")).get().getPageRelativePath();
        String focusPath = WicketUtils.findFirstChild(modal.getModalFooter(), AjaxButton.class, it -> it.getId().equals("focus")).get().getPageRelativePath().substring(formPath.length() + 1);
        String refreshPath = WicketUtils.findFirstChild(modal.getModalFooter(), AjaxLink.class, it -> it.getId().equals("refresh")).get().getPageRelativePath();
        String labelPath = WicketUtils.findFirstChild(modal.getModalBody(), BSLabel.class, it -> it.getId().equals("label")).get().getPageRelativePath();
        String closePath = modal.getCloseIcon().getPageRelativePath();

        tester.assertInvisible(labelPath);
        tester.clickLink(openPath);
        tester.getLastRenderedPage().visitChildren(new RenderHeadVisitor());
        tester.assertVisible(labelPath);
        tester.clickLink(refreshPath);
        tester.newFormTester(formPath).submit(focusPath);
        Assert.assertNull(tester.getLastRenderedPage().get(focusPath));
        Assert.assertNull(tester.getLastRenderedPage().get(refreshPath));
        tester.clickLink(closePath);
        tester.assertInvisible(labelPath);

    }

    private static class RenderHeadVisitor implements IVisitor<Component, Void> {
        @Override
        public void component(Component object, IVisit<Void> visit) {
            object.getBehaviors().stream().forEach(it -> it.renderHead(object, new HeaderResponse() {
                @Override
                protected Response getRealResponse() {
                    return NullResponse.getInstance();
                }
            }));
        }
    }
}
