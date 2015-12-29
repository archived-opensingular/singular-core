package br.net.mirante.singular.form.wicket;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.basic.ui.MPacoteBasic;
import br.net.mirante.singular.form.mform.core.MIInteger;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import java.util.Optional;

public class MPacoteBootstrap extends MPacote {

    public static final AtrRef<MTipoInteger, MIInteger, Integer> ATR_COL_PREFERENCE
            = new AtrRef<>(MPacoteBootstrap.class, "larguraColuna", MTipoInteger.class, MIInteger.class, Integer.class);
    public static final AtrRef<MTipoInteger, MIInteger, Integer> ATR_COL_XS_PREFERENCE
            = new AtrRef<>(MPacoteBootstrap.class, "larguraColunaXS", MTipoInteger.class, MIInteger.class, Integer.class);
    public static final AtrRef<MTipoInteger, MIInteger, Integer> ATR_COL_SM_PREFERENCE
            = new AtrRef<>(MPacoteBootstrap.class, "larguraColunaSM", MTipoInteger.class, MIInteger.class, Integer.class);
    public static final AtrRef<MTipoInteger, MIInteger, Integer> ATR_COL_MD_PREFERENCE
            = new AtrRef<>(MPacoteBootstrap.class, "larguraColunaMD", MTipoInteger.class, MIInteger.class, Integer.class);
    public static final AtrRef<MTipoInteger, MIInteger, Integer> ATR_COL_LG_PREFERENCE
            = new AtrRef<>(MPacoteBootstrap.class, "larguraColunaLG", MTipoInteger.class, MIInteger.class, Integer.class);

    public MPacoteBootstrap() {
        super("mform.plaf.bootstrap");
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        adicionarDefinicaoColuna(pb, ATR_COL_PREFERENCE, null);
        adicionarDefinicaoColuna(pb, ATR_COL_XS_PREFERENCE, "XS");
        adicionarDefinicaoColuna(pb, ATR_COL_SM_PREFERENCE, "SM");
        adicionarDefinicaoColuna(pb, ATR_COL_MD_PREFERENCE, "MD");
        adicionarDefinicaoColuna(pb, ATR_COL_LG_PREFERENCE, "LG");
    }

    private void adicionarDefinicaoColuna(PacoteBuilder pb, AtrRef<?,?,?> atrRef, String label){
        Optional<String> labelOp = Optional.ofNullable(label);
        pb.createTipoAtributo(atrRef);
        pb.addAtributo(MTipo.class, atrRef);
        pb.getAtributo(atrRef).as(MPacoteBasic.aspect()).label(("Largura preferencial " + labelOp.orElse("")).trim());
    }

}
