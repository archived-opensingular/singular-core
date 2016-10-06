package org.opensingular.singular.exemplos.notificacaosimplificada.form.vocabulario;

import org.opensingular.singular.exemplos.notificacaosimplificada.domain.FormaFarmaceuticaBasica;
import org.opensingular.singular.exemplos.notificacaosimplificada.service.DominioService;
import org.opensingular.singular.form.SIComposite;
import org.opensingular.singular.form.SInfoType;
import org.opensingular.singular.form.STypeComposite;
import org.opensingular.singular.form.TypeBuilder;
import org.opensingular.singular.form.provider.STextQueryProvider;
import org.opensingular.singular.form.type.core.STypeInteger;
import org.opensingular.singular.form.type.core.STypeString;
import org.opensingular.singular.form.view.SViewAutoComplete;

@SInfoType(spackage = SPackageVocabularioControlado.class)
public class STypeFormaFarmaceutica extends STypeComposite<SIComposite> {

    public STypeString descricao;
    public STypeInteger id;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        id = this.addFieldInteger("id");
        descricao = this.addFieldString("descricao");
        {

            this
                    .asAtr()
                    .required()
                    .label("Forma farmacêutica")
                    .asAtrBootstrap()
                    .colPreference(4);
            this.setView(SViewAutoComplete::new);

            this.autocomplete()
                    .id(id)
                    .display(descricao)
                    .filteredProvider((STextQueryProvider) (builder, query) -> {
                        builder
                                .getCurrentInstance()
                                .getDocument()
                                .lookupService(DominioService.class)
                                .buscarVocabulario(FormaFarmaceuticaBasica.class, query)
                                .forEach(vc -> builder.add().set(id, vc.getId()).set(descricao, vc.getDescricao()));
                    });

        }
    }


}
