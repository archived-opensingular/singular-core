package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.LinhaCbpf;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
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

    protected FilteredProvider<LinhaCbpf, SIComposite> getProvider() {
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
                    .asAtrBasic()
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
