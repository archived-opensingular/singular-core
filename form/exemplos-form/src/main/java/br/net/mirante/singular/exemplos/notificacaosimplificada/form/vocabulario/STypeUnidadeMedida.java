package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.UnidadeMedida;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.converter.SInstanceConverter;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.provider.FilteredProvider;
import br.net.mirante.singular.form.mform.util.transformer.Value;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado.dominioService;

@SInfoType(spackage = SPackageVocabularioControlado.class)
public class STypeUnidadeMedida extends STypeComposite<SIComposite> {

    public STypeString sigla, descricao;
    public STypeInteger id;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        id = this.addFieldInteger("id");
        sigla = this.addFieldString("sigla");
        descricao = this.addFieldString("descricao");
        {

            this
                    .asAtrBasic()
                    .required()
                    .label("Unidade de medida")
                    .asAtrBootstrap()
                    .colPreference(4);
            this.setView(SViewAutoComplete::new);

            this.autocompleteOf(UnidadeMedida.class)
                    .id("${id}")
                    .display("${sigla} - ${descricao}")
                    .converter(new SInstanceConverter<UnidadeMedida>() {
                        @Override
                        public void fillInstance(SInstance ins, UnidadeMedida obj) {
                            ((SIComposite) ins).setValue(id, obj.getId());
                            ((SIComposite) ins).setValue(sigla, obj.getSigla());
                            ((SIComposite) ins).setValue(descricao, obj.getDescricao());
                        }

                        @Override
                        public UnidadeMedida toObject(SInstance ins) {
                            return dominioService(ins).unidadesMedida(null)
                                    .stream().filter(u -> Integer.valueOf(u.getId().intValue()).equals(Value.of(ins, id)))
                                    .findFirst()
                                    .orElse(null);
                        }
                    }).provider((FilteredProvider) (ins, query) -> dominioService(ins).unidadesMedida(query));

        }
    }


}
