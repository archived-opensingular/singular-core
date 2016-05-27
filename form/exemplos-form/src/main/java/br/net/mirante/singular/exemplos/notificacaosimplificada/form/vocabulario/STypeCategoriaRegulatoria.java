package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.CategoriaRegulatoriaMedicamento;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.converter.VocabularioControladoDTOSInstanceConverter;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.dto.VocabularioControladoDTO;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.provider.VocabularioControladoTextQueryProvider;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SInfoType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.TypeBuilder;
import br.net.mirante.singular.form.type.core.STypeInteger;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.view.SViewAutoComplete;

@SInfoType(spackage = SPackageVocabularioControlado.class)
public class STypeCategoriaRegulatoria extends STypeComposite<SIComposite> {

    public STypeString descricao;
    public STypeInteger id;

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);
        id = this.addFieldInteger("id");
        descricao = this.addFieldString("descricao");
        {
            this
                    .asAtrBootstrap()
                    .colPreference(6)
                    .asAtr()
                    .label("Classe")
                    .required();
            this.setView(SViewAutoComplete::new);

            this.autocompleteOf(VocabularioControladoDTO.class)
                    .id(VocabularioControladoDTO::getId)
                    .display(VocabularioControladoDTO::getDescricao)
                    .converter(new VocabularioControladoDTOSInstanceConverter(id, descricao))
                    .filteredProvider(new VocabularioControladoTextQueryProvider<>(CategoriaRegulatoriaMedicamento.class));

        }
    }


}
