package br.net.mirante.singular.exemplos.notificacaosimplificada.form;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.geral.EnderecoEmpresaInternacional;
import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.converter.SInstanceConverter;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.provider.TextQueryProvider;
import br.net.mirante.singular.form.mform.util.transformer.Value;

import static br.net.mirante.singular.exemplos.notificacaosimplificada.form.vocabulario.SPackageVocabularioControlado.dominioService;

@SInfoType(spackage = SPackageNotificacaoSimplificada.class)
public class STypeEmpresaInternacional extends STypeComposite<SIComposite> {

    @Override
    protected void onLoadType(TypeBuilder tb) {
        super.onLoadType(tb);

        final STypeComposite<SIComposite> id = addFieldComposite("id");

        final STypeInteger idEmpresaInternacional = id.addFieldInteger("idEmpresaInternacional");
        final STypeInteger sequencialEndereco     = id.addFieldInteger("sequencialEndereco");

        final STypeString razaoSocial = addFieldString("razaoSocial");
        final STypeString endereco    = addFieldString("endereco");

        razaoSocial.
                asAtr()
                .required()
                .label("Razão Social");

        autocompleteOf(EnderecoEmpresaInternacional.class)
                .id(e -> String.format("%d-%d", e.getId().getEmpresaInternacional().getId(), e.getId().getSequencialEndereco()))
                .display(e -> e.getEmpresaInternacional().getRazaoSocial())
                .converter(new SInstanceConverter<EnderecoEmpresaInternacional, SIComposite>() {
                    @Override
                    public void fillInstance(SIComposite ins, EnderecoEmpresaInternacional obj) {
                        final SIComposite compositeId;
                        if (ins.getField(id.getNameSimple()) != null) {
                            compositeId = (SIComposite) ins.getField(id.getNameSimple());
                        } else {
                            compositeId = id.newInstance();
                            ins.setValue(id, compositeId);
                        }
                        compositeId.setValue(idEmpresaInternacional, obj.getId().getEmpresaInternacional().getId());
                        compositeId.setValue(sequencialEndereco, obj.getId().getSequencialEndereco());
                        ins.setValue(razaoSocial, obj.getEmpresaInternacional().getRazaoSocial());
                        ins.setValue(endereco, obj.getEnderecoCompleto());
                    }

                    @Override
                    public EnderecoEmpresaInternacional toObject(SIComposite ins) {
                        final SInstance value = (SInstance) ins.getField(id.getNameSimple());
                        if (!value.isEmptyOfData()) {
                            final Integer idEmpresa  = Value.of(value, idEmpresaInternacional);
                            final Integer sequencial = Value.of(value, sequencialEndereco);
                            return dominioService(ins).buscarEmpresaInternacional(idEmpresa.longValue(), sequencial.shortValue());
                        }
                        return null;
                    }
                })
                .filteredProvider((TextQueryProvider<EnderecoEmpresaInternacional, SIComposite>) (ins, query) -> dominioService(ins).empresaInternacional(query));

    }


}
