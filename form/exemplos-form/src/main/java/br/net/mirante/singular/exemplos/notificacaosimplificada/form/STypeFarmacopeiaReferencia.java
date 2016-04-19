package br.net.mirante.singular.exemplos.notificacaosimplificada.form;

import br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.STypeFarmacopeia;
import br.net.mirante.singular.exemplos.notificacaosimplificada.service.DominioService;
import br.net.mirante.singular.form.mform.*;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeFarmacopeiaReferencia extends STypeComposite<SIComposite> {


    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {

        this.addField("farmacopeia", STypeFarmacopeia.class);

        this.addFieldString("edicao")
                .asAtrBasic().label("Edição")
                .asAtrBootstrap().colPreference(2);
        this.addFieldString("pagina")
                .asAtrBasic().label("Página")
                .asAtrBootstrap().colPreference(2);
    }
}
