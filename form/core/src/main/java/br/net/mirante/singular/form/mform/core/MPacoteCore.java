package br.net.mirante.singular.form.mform.core;

import java.util.function.Function;
import java.util.function.Predicate;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.MAtributoEnabled;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MIPredicate;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoCode;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.MTipoPredicate;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.core.attachment.MTipoAttachment;
import br.net.mirante.singular.form.mform.options.MTipoSelectItem;

public class MPacoteCore extends MPacote {

    public static final String NOME = "mform.core";

    public static final AtrRef<?, ?, Object>                     ATR_VALOR_INICIAL   = AtrRef.ofSelfReference(MPacoteCore.class, "valorInicial");
    public static final AtrRef<?, ?, Object>                     ATR_DEFAULT_IF_NULL = AtrRef.ofSelfReference(MPacoteCore.class, "defaultIfNull");
    public static final AtrRef<MTipoBoolean, MIBoolean, Boolean> ATR_TRIM            = new AtrRef<>(MPacoteCore.class, "trim", MTipoBoolean.class,
        MIBoolean.class, Boolean.class);
    public static final AtrRef<MTipoBoolean, MIBoolean, Boolean> ATR_EMPTY_TO_NULL   = new AtrRef<>(MPacoteCore.class, "emptyToNull",
        MTipoBoolean.class, MIBoolean.class, Boolean.class);
    public static final AtrRef<MTipoBoolean, MIBoolean, Boolean> ATR_OBRIGATORIO     = new AtrRef<>(MPacoteCore.class, "obrigatorio",
        MTipoBoolean.class, MIBoolean.class, Boolean.class);

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final AtrRef<MTipoPredicate, MIPredicate, Predicate<MInstancia>> ATR_OBRIGATORIO_FUNCTION = new AtrRef(MPacoteCore.class, "obrigatorioFunction",
        MTipoPredicate.class, MIPredicate.class, Predicate.class);

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
        pb.createTipo(MTipoPredicate.class);

        pb.createTipo(MTipoString.class);
        pb.createTipo(MTipoInteger.class);
        pb.createTipo(MTipoBoolean.class);
        pb.createTipo(MTipoData.class);
        
        pb.createTipo(MTipoSelectItem.class);

        pb.createTipoAtributo(MTipo.class, ATR_OBRIGATORIO);
        pb.createTipoAtributo(MTipo.class, ATR_OBRIGATORIO_FUNCTION);
        pb.createTipoAtributo(MTipo.class, ATR_DEFAULT_IF_NULL);

        pb.createTipoAtributo(MTipoSimples.class, ATR_VALOR_INICIAL);
        //pb.createTipoAtributo(MTipoSimples.class, ATR_DEFAULT_IF_NULL);

        pb.getAtributo(ATR_OBRIGATORIO).withDefaultValueIfNull(false);
        pb.getAtributo(ATR_OBRIGATORIO_FUNCTION);

        pb.createTipoAtributo(MTipoString.class, ATR_TRIM).withDefaultValueIfNull(true);
        pb.createTipoAtributo(MTipoString.class, ATR_EMPTY_TO_NULL).withDefaultValueIfNull(true);

        pb.createTipo(MTipoFormula.class);
        pb.createTipoAtributo(MTipoSimples.class, ATR_FORMULA);

        pb.createTipo(MTipoAttachment.class);
    }

    public static Function<MAtributoEnabled, AtrCore> aspect() {
        return AtrCore::new;
    }
}
