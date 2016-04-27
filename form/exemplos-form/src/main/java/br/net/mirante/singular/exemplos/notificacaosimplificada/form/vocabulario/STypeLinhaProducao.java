package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.LinhaCbpf;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.converter.SInstanceConverter;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.provider.FilteredProvider;
import br.net.mirante.singular.form.mform.util.transformer.Value;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado.dominioService;

@SInfoType(spackage = SPackageVocabularioControlado.class)
public class STypeLinhaProducao extends STypeComposite<SIComposite> {

    public STypeString  descricao;
    public STypeInteger id;

    protected FilteredProvider getProvider() {
        return (ins, filter) -> dominioService(ins).linhasProducao(filter);
    }

    protected SInstanceConverter<LinhaCbpf> getConverter() {
        return new SInstanceConverter<LinhaCbpf>() {
            @Override
            public void fillInstance(SInstance ins, LinhaCbpf obj) {
                ((SIComposite) ins).setValue(id, obj.getId());
                ((SIComposite) ins).setValue(descricao, obj.getDescricao());
            }

            @Override
            public LinhaCbpf toObject(SInstance ins) {
                return dominioService(ins)
                        .linhasProducao(null)
                        .stream().filter(l -> (Value.of(ins, id).equals(l.getId().intValue())))
                        .findFirst().orElse(null);
            }
        };
    }

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        id = this.addFieldInteger("id");
        descricao = this.addFieldString("descricao");
        {
            this
                    .asAtrBasic()
                    .required()
                    .label("Linha de produção")
                    .asAtrBootstrap()
                    .colPreference(4);
            this.setView(SViewAutoComplete::new);

            this.selection()
                    .id((IFunction<LinhaCbpf, String>) l -> l.getId().toString())
                    .display("${descricao}")
                    .converter(getConverter())
                    .provider(getProvider());
        }
    }

}
