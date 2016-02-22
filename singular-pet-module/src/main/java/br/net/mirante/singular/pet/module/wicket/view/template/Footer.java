package br.net.mirante.singular.pet.module.wicket.view.template;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Footer extends Panel {

    private static final Logger LOGGER = LoggerFactory.getLogger(Footer.class);

    public Footer(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        WebMarkupContainer ownerLink = new WebMarkupContainer("ownerLink");
        ownerLink.add(new AttributeModifier("href", new ResourceModel("footer.product.owner.addr")));
        ownerLink.add(new AttributeModifier("title", new ResourceModel("footer.product.owner.title")));
        add(new Label("version", Model.of(getVersion())));
        add(ownerLink);
    }

    private String getVersion() {
        try (InputStream propsStream = getClass().getResourceAsStream("/module.properties")) {
            final Properties showCaseProperties = new Properties();
            showCaseProperties.load(propsStream);
            Object version = showCaseProperties.get("version");
            if (version != null) {
                return "Versão: "+String.valueOf(version);
            }
        } catch (IOException e) {
            LOGGER.error("Não foi possivel obter a versão do showcase", e);
        }
        return StringUtils.EMPTY;
    }
}
