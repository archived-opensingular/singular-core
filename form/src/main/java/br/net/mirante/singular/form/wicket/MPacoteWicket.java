package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.MICode;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipoCode;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class MPacoteWicket extends MPacote {

    public static final AtrRef<MTipoCode, MICode, IWicketComponentMapper> ATR_MAPPER = new AtrRef<>(MPacoteWicket.class, "componentMapper",
            MTipoCode.class, MICode.class, IWicketComponentMapper.class);

    public MPacoteWicket() {
        super("mform.plaf.wicket");
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        pb.createTipoAtributo(ATR_MAPPER);

        // TODO Ver o que já tem de lógica em FormularioVariaveisUtils do
        // Alocpro

        pb.addAtributo(MTipoString.class, ATR_MAPPER, UIBuilderWicket.StringMapper.class);
        pb.addAtributo(MTipoInteger.class, ATR_MAPPER, UIBuilderWicket.IntegerMapper.class);

    }

}
