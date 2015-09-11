package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.MICode;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoCode;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.AtrBasic;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MIInteger;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class MPacoteWicket extends MPacote {

    public static final AtrRef<MTipoInteger, MIInteger, Integer> ATR_LARGURA_PREF    = new AtrRef<>(MPacoteBasic.class, "larguraColuna",
        MTipoInteger.class, MIInteger.class, Integer.class);

    public static final AtrRef<MTipoCode, MICode, IWicketComponentMapper> ATR_MAPPER = new AtrRef<>(MPacoteWicket.class, "componentMapper",
            MTipoCode.class, MICode.class, IWicketComponentMapper.class);

    public MPacoteWicket() {
        super("mform.plaf.wicket");
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        pb.createTipoAtributo(ATR_MAPPER);
        pb.createTipoAtributo(MTipo.class, ATR_LARGURA_PREF);

        // TODO Ver o que já tem de lógica em FormularioVariaveisUtils do
        // Alocpro

        pb.addAtributo(MTipoString.class, ATR_MAPPER, UIBuilderWicket.StringMapper.class);
        pb.addAtributo(MTipoInteger.class, ATR_MAPPER, UIBuilderWicket.IntegerMapper.class);

        pb.getAtributo(ATR_LARGURA_PREF).as(AtrBasic.class).label("Largura preferencial");
    }

}
