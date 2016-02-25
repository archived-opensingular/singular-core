package br.net.mirante.singular.pet.module.util;

import java.util.HashMap;
import java.util.Map;

public class ServerProperties {

    private static final Map<String, String> mapa = new HashMap<>();

    public static final String SINGULAR_WS_ENDERECO = "singular.ws.endereco";
    public static final String SINGULAR_SERVIDOR_ENDERECO = "singular.servidor.endereco";
    public static final String SINGULAR_MODULE_FORM_ENDERECO = "singular.module.form.endereco";

    static {
        mapa.put(SINGULAR_WS_ENDERECO, "http://localhost:8080/canabidiol/SingularWS?wsdl");
        mapa.put(SINGULAR_SERVIDOR_ENDERECO, "http://localhost:8080/singular/peticionamento");
        mapa.put(SINGULAR_MODULE_FORM_ENDERECO, "/peticionamento/form");
    }

    public static String getProperty(String property) {
        return mapa.get(property);
    }
}
