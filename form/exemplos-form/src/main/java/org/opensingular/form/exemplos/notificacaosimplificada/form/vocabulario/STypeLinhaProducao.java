package org.opensingular.form.exemplos.notificacaosimplificada.form.vocabulario;

import org.opensingular.form.SIComposite;
import org.opensingular.form.SInfoType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.TypeBuilder;
import org.opensingular.form.provider.STextQueryProvider;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.view.SViewAutoComplete;

@SInfoType(spackage = SPackageVocabularioControlado.class)
public class STypeLinhaProducao extends STypeComposite<SIComposite> {

    public STypeString  descricao;
    public STypeInteger id;

    protected STextQueryProvider getProvider() {
        return (STextQueryProvider) (builder, query) -> SPackageVocabularioControlado.dominioService(builder.getCurrentInstance()).linhasProducao(query).forEach(lp -> {
            builder.add().set(id, lp.getId()).set(descricao, lp.getDescricao());
        });
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        id = this.addFieldInteger("id");
        descricao = this.addFieldString("descricao");
        {
            this
                    .asAtr()
                    .required()
                    .label("Linha de produção")
                    .asAtrBootstrap()
                    .colPreference(6);
            this.setView(SViewAutoComplete::new);

            this.autocomplete()
                    .id(id)
                    .display(descricao)
                    .filteredProvider(getProvider());
        }
    }

}