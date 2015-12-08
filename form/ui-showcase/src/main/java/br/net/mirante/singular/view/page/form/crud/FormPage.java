package br.net.mirante.singular.view.page.form.crud;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.string.StringValue;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.wicketstuff.annotation.mount.MountPath;

import br.net.mirante.singular.view.template.Content;
import br.net.mirante.singular.view.template.Template;

@MountPath("form/edit")
@SuppressWarnings("serial")
public class FormPage extends Template {
    protected static final String TYPE_NAME = "type",
                                  MODEL_KEY = "key";

    @Override
    protected Content getContent(String id) {
        StringValue type = getPageParameters().get(TYPE_NAME),
                    key = getPageParameters().get(MODEL_KEY);
        return new FormContent(id,type, key);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(OnDomReadyHeaderItem.forScript("$('#_menuItemDemo').addClass('active');"));
    }

}
