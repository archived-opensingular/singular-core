package br.net.mirante.singular.exemplos.notificacaosimplificada.domain.converter;

import br.net.mirante.singular.exemplos.notificacaosimplificada.domain.dto.VocabularioControladoDTO;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.converter.SInstanceConverter;
import br.net.mirante.singular.form.mform.util.transformer.Value;

public class VocabularioControladoDTOSInstanceConverter implements SInstanceConverter<VocabularioControladoDTO, SIComposite> {

    private final String id;
    private final String descricao;

    public VocabularioControladoDTOSInstanceConverter(SType id, SType descricao) {
        this.descricao = descricao.getNameSimple();
        this.id = id.getNameSimple();
    }

    @Override
    public void fillInstance(SIComposite ins, VocabularioControladoDTO obj) {
        ins.setValue(id, obj.getId());
        ins.setValue(descricao, obj.getDescricao());
    }

    @Override
    public VocabularioControladoDTO toObject(SIComposite ins) {
        final Integer realId =  Value.of(ins, id);
        return new VocabularioControladoDTO(realId.longValue(), Value.of(ins, descricao));
    }

}