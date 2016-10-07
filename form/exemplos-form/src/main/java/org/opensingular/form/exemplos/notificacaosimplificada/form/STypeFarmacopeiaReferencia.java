package org.opensingular.form.exemplos.notificacaosimplificada.form;

import org.opensingular.form.exemplos.notificacaosimplificada.form.vocabulario.STypeFarmacopeia;
import org.opensingular.form.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeFarmacopeiaReferencia extends STypeComposite<SIComposite> {


    static DominioService dominioService(SInstance ins) {
        return ins.getDocument().lookupService(DominioService.class);
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {

        this.addField("farmacopeia", STypeFarmacopeia.class);

        this.addFieldString("edicao")
                .asAtr().label("Edição")
                .asAtrBootstrap().colPreference(2);
        this.addFieldString("pagina")
                .asAtr().label("Página")
                .asAtrBootstrap().colPreference(2);
    }
}
