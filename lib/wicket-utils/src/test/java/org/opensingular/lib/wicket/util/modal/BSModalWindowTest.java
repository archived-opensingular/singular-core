package org.opensingular.lib.wicket.util.modal;

import static org.junit.Assert.*;

import java.util.NoSuchElementException;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.junit.Test;
import org.opensingular.lib.wicket.util.SingleFormDummyPage;
import org.opensingular.lib.wicket.util.WicketUtilTester;
import org.opensingular.lib.wicket.util.WicketUtilsDummyApplication;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxButton;
import org.opensingular.lib.wicket.util.ajax.ActionAjaxLink;
import org.opensingular.lib.wicket.util.bootstrap.layout.TemplatePanel;
import org.opensingular.lib.wicket.util.modal.BSModalBorder.ButtonStyle;
import org.opensingular.lib.wicket.util.modal.BSModalBorder.Size;

public class BSModalWindowTest {

    @Test
    public void test() {
        WicketUtilTester tester = new WicketUtilTester(new WicketUtilsDummyApplication());

        tester.startPage(new SingleFormDummyPage() {
            @Override
            protected Component newContentPanel(String contentId) {
                BSModalWindow modal = new BSModalWindow("modal");
                ActionAjaxLink<Void> openAction = new ActionAjaxLink<Void>("open") {
                    @Override
                    protected void onAction(AjaxRequestTarget target) {
                        modal.show(target);
                    }
                };
                modal
                    .setSize(Size.LARGE)
                    .setTitleText(Model.of("title"))
                    .setBody(new Label("label", "content"))
                    .addButton(ButtonStyle.DEFAULT, new ActionAjaxButton("button1") {
                        @Override
                        protected void onAction(AjaxRequestTarget target, Form<?> form) {
                            this.info("button1 clicked");
                            modal.hide(target);
                        }
                    })
                    .addButton(ButtonStyle.DEFAULT, Model.of("Button2"), new ActionAjaxButton("button2") {
                        @Override
                        protected void onAction(AjaxRequestTarget target, Form<?> form) {
                            this.info("button2 clicked");
                            modal.hide(target);
                        }
                    })
                    .addLink(ButtonStyle.LINK, new ActionAjaxLink<Void>("link1") {
                        @Override
                        protected void onAction(AjaxRequestTarget target) {
                            this.info("link1 clicked");
                            modal.hide(target);
                        }
                    })
                    .addLink(ButtonStyle.LINK, Model.of("Link2"), new ActionAjaxLink<Void>("link2") {
                        @Override
                        protected void onAction(AjaxRequestTarget target) {
                            this.info("link2 clicked");
                            modal.hide(target);
                        }
                    })
                    .setCloseIconCallback(target -> {
                        this.info("close icon clicked");
                        modal.hide(target);
                    });
                return new TemplatePanel(contentId, ""
                    + "<div wicket:id='modal'></div>"
                    + "<a wicket:id='open'>open</a>")
                        .add(modal)
                        .add(openAction);
            }
        });

        SingleFormDummyPage page = (SingleFormDummyPage) tester.getLastRenderedPage();
        BSModalWindow modal = page.childById(BSModalWindow.class, "modal").get();
        ActionAjaxLink<?> openLink = page.childById(ActionAjaxLink.class, "open").get();

        assertFalse(modal.getModalBorder().isVisible());
        tester.clickLink(openLink);
        assertTrue(modal.getModalBorder().isVisible());

        page.newFormTesterForTopFormInContent(tester).submit(page.childById("button1").get());
        assertFalse(modal.getModalBorder().isVisible());
        tester.assertInfoMessages("button1 clicked");
        tester.clickLink(openLink);
        tester.clearFeedbackMessages();

        page.newFormTesterForTopFormInContent(tester).submit(page.childById("button2").get());
        assertFalse(modal.getModalBorder().isVisible());
        tester.assertInfoMessages("button2 clicked");
        tester.clickLink(openLink);
        tester.clearFeedbackMessages();

        tester.clickLink(page.childById("link1").get());
        assertFalse(modal.getModalBorder().isVisible());
        tester.assertInfoMessages("link1 clicked");
        tester.clickLink(openLink);
        tester.clearFeedbackMessages();

        tester.clickLink(page.childById("link2").get());
        assertFalse(modal.getModalBorder().isVisible());
        tester.assertInfoMessages("link2 clicked");
        tester.clickLink(openLink);
        tester.clearFeedbackMessages();

        tester.clickLink(modal.getModalBorder().getCloseIcon());
        assertFalse(modal.getModalBorder().isVisible());
        tester.assertInfoMessages("close icon clicked");
        tester.clickLink(openLink);
        tester.clearFeedbackMessages();
        
    }

    @Test(expected = NoSuchElementException.class)
    public void test_nonForm() {
        WicketUtilTester tester = new WicketUtilTester(new WicketUtilsDummyApplication());

        tester.startPage(new SingleFormDummyPage() {
            @Override
            protected Component newContentPanel(String contentId) {
                BSModalWindow modal = new BSModalWindow("modal", false);
                return new TemplatePanel(contentId, ""
                    + "<div wicket:id='modal'></div>"
                    + "<a wicket:id='open'>open</a>")
                        .add(modal
                            .setSize(Size.LARGE)
                            .setTitleText(Model.of("title"))
                            .setBody(new Label("label", "content")))
                        .add(new ActionAjaxLink<Void>("open") {
                            @Override
                            protected void onAction(AjaxRequestTarget target) {
                                modal.show(target);
                            }
                        });
            }
        });

        SingleFormDummyPage page = (SingleFormDummyPage) tester.getLastRenderedPage();
        BSModalWindow modal = page.childById(BSModalWindow.class, "modal").get();
        ActionAjaxLink<?> openLink = page.childById(ActionAjaxLink.class, "open").get();

        assertFalse(modal.getModalBorder().isVisible());
        tester.clickLink(openLink);
        assertTrue(modal.getModalBorder().isVisible());

        page.topFormInContent();
    }

}
