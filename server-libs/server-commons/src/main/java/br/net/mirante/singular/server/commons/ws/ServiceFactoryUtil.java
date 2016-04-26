package br.net.mirante.singular.server.commons.ws;


import br.net.mirante.singular.server.commons.config.ConfigProperties;
import br.net.mirante.singular.ws.client.SingularWS;
import br.net.mirante.singular.ws.client.SingularWSService;
import org.apache.log4j.Logger;

import javax.xml.ws.BindingProvider;

public class ServiceFactoryUtil {

    /**
     * Constante LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(ServiceFactoryUtil.class);

    /**
     * Campo singular service.
     */
    private SingularWS singularService = null;

    /**
     * Obtém uma referência de singular ws.
     *
     * @return uma referência de singular ws
     */
    public SingularWS getSingularWS() {
        if (singularService == null) {
            singularService = new SingularWSService().getSingularWSPort();
            BindingProvider bp = (BindingProvider) singularService;
            bp.getRequestContext().put(
                    BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
                    getAdressWithoutWsdl(
                            ConfigProperties.get("singular.ws.endereco")
                    ));
        }
        return singularService;
    }

    private static String getAdressWithoutWsdl(String adress) {
        int lastIndex = adress.lastIndexOf("?wsdl");
        if (lastIndex > 0) {
            return adress.substring(0, lastIndex);
        }
        return adress;
    }

}
