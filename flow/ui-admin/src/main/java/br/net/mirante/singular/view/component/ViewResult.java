package br.net.mirante.singular.view.component;


import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.net.mirante.singular.bamclient.portlet.PortletConfig;
import br.net.mirante.singular.bamclient.portlet.PortletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public abstract class ViewResult<T extends PortletConfig> extends Panel {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewResult.class);

    private IModel<T> config;

    private IModel<PortletContext> context;

    public ViewResult(String id, IModel<T> config, IModel<PortletContext> context) {
        super(id);
        this.config = config;
        this.context = context;
    }

    public IModel<T> getConfig() {
        return config;
    }

    public IModel<PortletContext> getContext() {
        return context;
    }

    public String getSerializedContext() {
        try {
            return new ObjectMapper().writeValueAsString(context.getObject());
        } catch (JsonProcessingException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

}

