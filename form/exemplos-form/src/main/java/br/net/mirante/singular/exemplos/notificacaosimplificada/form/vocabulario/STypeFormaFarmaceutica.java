package br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.FormaFarmaceuticaBasica;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.converter.VocabularioControladoDTOSInstanceConverter;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.dto.VocabularioControladoDTO;
import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.provider.VocabularioControladoFilteredProvider;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInfoType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.TypeBuilder;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;

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
                    .asAtrBasic()
                    .required()
                    .label("Forma farmacêutica")
                    .asAtrBootstrap()
                    .colPreference(4);
            this.setView(SViewAutoComplete::new);

            this.autocompleteOf(VocabularioControladoDTO.class)
                    .id(VocabularioControladoDTO::getId)
                    .display(VocabularioControladoDTO::getDescricao)
                    .converter(new VocabularioControladoDTOSInstanceConverter(id, descricao))
                    .filteredProvider(new VocabularioControladoFilteredProvider<>(FormaFarmaceuticaBasica.class));


        }
    }


}
