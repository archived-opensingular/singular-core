package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoCode;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.PacoteBuilder;

public class MPacoteCore extends MPacote {

    public static final String NOME = "mform.core";

    public static final AtrRef<?, ?, Object> ATR_VALOR_INICIAL = AtrRef.ofSelfReference(MPacoteCore.class, "valorInicial");
    public static final AtrRef<?, ?, Object> ATR_DEFAULT_IF_NULL = AtrRef.ofSelfReference(MPacoteCore.class, "defaultIfNull");
    public static final AtrRef<MTipoBoolean, MIBoolean, Boolean> ATR_TRIM = new AtrRef<>(MPacoteCore.class, "trim", MTipoBoolean.class,
            MIBoolean.class, Boolean.class);
    public static final AtrRef<MTipoBoolean, MIBoolean, Boolean> ATR_EMPTY_TO_NULL = new AtrRef<>(MPacoteCore.class, "emptyToNull",
            MTipoBoolean.class, MIBoolean.class, Boolean.class);
    public static final AtrRef<MTipoBoolean, MIBoolean, Boolean> ATR_OBRIGATORIO = new AtrRef<>(MPacoteCore.class, "obrigatorio",
            MTipoBoolean.class, MIBoolean.class, Boolean.class);

    public static final AtrRef<MTipoFormula, MIComposto, Object> ATR_FORMULA = new AtrRef<>(MPacoteCore.class, "formula",
            MTipoFormula.class, MIComposto.class, Object.class);

    public MPacoteCore() {
        super(NOME);
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        pb.createTipo(MTipo.class);
        pb.createTipo(MTipoSimples.class);
        pb.createTipo(MTipoComposto.class);
        pb.createTipo(MTipoLista.class);
        pb.createTipo(MTipoCode.class);

        pb.createTipo(MTipoString.class);
        pb.createTipo(MTipoInteger.class);
        pb.createTipo(MTipoBoolean.class);
        pb.createTipo(MTipoData.class);

        pb.createTipoAtributo(MTipo.class, ATR_OBRIGATORIO);

        pb.createTipoAtributo(MTipoSimples.class, ATR_VALOR_INICIAL);
        pb.createTipoAtributo(MTipoSimples.class, ATR_DEFAULT_IF_NULL);

        pb.getAtributo(ATR_OBRIGATORIO).withDefaultValueIfNull(false);

        pb.createTipoAtributo(MTipoString.class, ATR_TRIM).withDefaultValueIfNull(true);
        pb.createTipoAtributo(MTipoString.class, ATR_EMPTY_TO_NULL).withDefaultValueIfNull(true);

        pb.createTipo(MTipoFormula.class);
        pb.createTipoAtributo(MTipoSimples.class, ATR_FORMULA);
    }
}
