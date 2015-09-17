package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MIInteger;
import br.net.mirante.singular.form.mform.core.MTipoInteger;

public class MPacoteWicket extends MPacote {

    public static final AtrRef<MTipoInteger, MIInteger, Integer> ATR_LARGURA_PREF = new AtrRef<>(MPacoteWicket.class, "larguraColuna",
                                                                                      MTipoInteger.class, MIInteger.class, Integer.class);

    //    public static final AtrRef<MTipoCode, MICode, IWicketComponentMapper> ATR_MAPPER       = new AtrRef<>(MPacoteWicket.class, "componentMapper",
    //                                                                                               MTipoCode.class, MICode.class, IWicketComponentMapper.class);

    public MPacoteWicket() {
        super("mform.plaf.wicket");
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        pb.createTipoAtributo(ATR_LARGURA_PREF);
        //        pb.createTipoAtributo(ATR_MAPPER);

        pb.addAtributo(MTipo.class, ATR_LARGURA_PREF);
        //        pb.addAtributo(MTipoString.class, ATR_MAPPER, UIBuilderWicket.StringMapper.class);

        pb.getAtributo(ATR_LARGURA_PREF).as(MPacoteBasic.aspect()).label("Largura preferencial");
    }

}
