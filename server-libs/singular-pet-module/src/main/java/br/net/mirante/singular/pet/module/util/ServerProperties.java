package br.net.mirante.singular.pet.module.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Essa classe recupera propriedades para customizar o
 * ambiente em que a aplicação está instalada.
 */
public class ServerProperties {

    private static final Map<String, String> mapa = new HashMap<>();

    public static final String SINGULAR_WS_ENDERECO = "singular.ws.endereco";
    public static final String SINGULAR_SERVIDOR_ENDERECO = "singular.servidor.endereco";
    public static final String SINGULAR_MODULE_FORM_ENDERECO = "singular.module.form.endereco";

    static {
        //TODO propriedades provisórias, quando definirmos uma forma de externalizar
        // essas configurações, esse código será removido
        mapa.put(SINGULAR_WS_ENDERECO, "http://localhost:8080/canabidiol/SingularWS?wsdl");
        mapa.put(SINGULAR_SERVIDOR_ENDERECO, "http://localhost:8080/singular/peticionamento");
        mapa.put(SINGULAR_MODULE_FORM_ENDERECO, "/peticionamento");
    }

    public static String getProperty(String property) {
        return mapa.get(property);
    }
}
