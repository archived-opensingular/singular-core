package br.net.mirante.singular.pet.module.flow;

import br.net.mirante.singular.flow.core.view.Lnk;
import br.net.mirante.singular.flow.core.view.ModalViewDef;
import br.net.mirante.singular.flow.core.view.WebRef;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.wicket.markup.html.WebPage;

public class SingularWebRef implements WebRef {

    private Class<? extends WebPage> page;

    public SingularWebRef(Class<? extends WebPage> page) {
        this.page = page;
    }

    public Class<? extends WebPage> getPageClass() {
        return page;
    }

    @Deprecated
    @Override
    public String getNome() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public String getNomeCurto() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public Lnk getPath() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public String getPathIcone() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public String getPathIconePequeno() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public String getConfirmacao() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public boolean isPossuiDireitoAcesso() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public boolean isJs() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public String getJs() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public boolean isAbrirEmNovaJanela() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public boolean isSeAplicaAoContexto() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public ModalViewDef getModalViewDef() {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public WebRef addParam(String nome, Object valor) {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }

    @Deprecated
    @Override
    public String gerarHtml(String urlApp) {
        throw new NotImplementedException("Método não implementado, não é necessário para o singular.");
    }
}
