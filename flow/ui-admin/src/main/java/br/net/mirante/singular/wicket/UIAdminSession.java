package br.net.mirante.singular.wicket;

import org.apache.wicket.Session;
import org.apache.wicket.core.request.ClientInfo;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;

public class UIAdminSession extends Session {

    private String nome;
    private String codRh;

    public UIAdminSession(Request request, Response response) {
        super(request);
        this.nome = request.getRequestParameters().getParameterValue("nome").toString("Daniel");
        this.codRh = request.getRequestParameters().getParameterValue("codRh").toString("10");
    }

    public static UIAdminSession get() {
        return (UIAdminSession) Session.get();
    }

    @Override
    public ClientInfo getClientInfo() {
        return null;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodRh() {
        return codRh;
    }

    public void setCodRh(String codRh) {
        this.codRh = codRh;
    }
}
