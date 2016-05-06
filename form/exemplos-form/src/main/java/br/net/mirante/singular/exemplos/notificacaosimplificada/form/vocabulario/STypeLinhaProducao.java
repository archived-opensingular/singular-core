package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.LinhaCbpf;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.converter.SInstanceConverter;
import br.net.mirante.singular.form.provider.TextQueryProvider;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.util.transformer.Value;
import br.net.mirante.singular.form.view.SViewAutoComplete;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado.dominioService;

@SInfoType(spackage = SPackageVocabularioControlado.class)
public class STypeLinhaProducao extends STypeComposite<SIComposite> {

    public STypeString  descricao;
    public STypeInteger id;

    protected TextQueryProvider<LinhaCbpf, SIComposite> getProvider() {
        return (ins, filter) -> dominioService(ins).linhasProducao(filter);
    }

    protected SInstanceConverter<LinhaCbpf, SIComposite> getConverter() {
        return new SInstanceConverter<LinhaCbpf, SIComposite>() {
            @Override
            public void fillInstance(SIComposite ins, LinhaCbpf obj) {
                ins.setValue(id, obj.getId());
                ins.setValue(descricao, obj.getDescricao());
            }

            @Override
            public LinhaCbpf toObject(SIComposite ins) {
                final LinhaCbpf linhaCbpf = new LinhaCbpf();
                linhaCbpf.setId(Value.of(ins, id).longValue());
                linhaCbpf.setDescricao(Value.of(ins, descricao));
                return linhaCbpf;
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
                    .asAtr()
                    .required()
                    .label("Linha de produção")
                    .asAtrBootstrap()
                    .colPreference(4);
            this.setView(SViewAutoComplete::new);

            this.autocompleteOf(LinhaCbpf.class)
                    .id(l -> l.getId().toString())
                    .display("${descricao}")
                    .converter(getConverter())
                    .filteredProvider(getProvider());
        }
    }

}
