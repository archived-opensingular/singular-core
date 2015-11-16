package br.net.mirante.singular.form.mform.basic.ui;

import java.util.function.Function;
import java.util.function.Predicate;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.MAtributoEnabled;
import br.net.mirante.singular.form.mform.MIPredicate;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoLista;
import br.net.mirante.singular.form.mform.MTipoPredicate;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.core.MIBoolean;
import br.net.mirante.singular.form.mform.core.MIInteger;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.MTipoData;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class MPacoteBasic extends MPacote {

    public static final String NOME = "mform.basic";

    public static final AtrRef<MTipoString, MIString, String>                      ATR_LABEL            = new AtrRef<>(MPacoteBasic.class, "label", MTipoString.class,
        MIString.class, String.class);
    public static final AtrRef<MTipoString, MIString, String>                      ATR_SUBTITLE         = new AtrRef<>(MPacoteBasic.class, "subtitle", MTipoString.class,
        MIString.class, String.class);
    public static final AtrRef<MTipoString, MIString, String>                      ATR_BASIC_MASK       = new AtrRef<>(MPacoteBasic.class, "basicMask", MTipoString.class,
        MIString.class, String.class);
    public static final AtrRef<MTipoInteger, MIInteger, Integer>                   ATR_TAMANHO_MAXIMO   = new AtrRef<>(MPacoteBasic.class, "tamanhoMaximo",
        MTipoInteger.class, MIInteger.class, Integer.class);
    public static final AtrRef<MTipoInteger, MIInteger, Integer>                   ATR_TAMANHO_EDICAO   = new AtrRef<>(MPacoteBasic.class, "tamanhoEdicao",
        MTipoInteger.class, MIInteger.class, Integer.class);
    public static final AtrRef<MTipoInteger, MIInteger, Integer>                   ATR_TAMANHO_INICIAL  = new AtrRef<>(MPacoteBasic.class, "tamanhoInicial",
        MTipoInteger.class, MIInteger.class, Integer.class);
    public static final AtrRef<MTipoBoolean, MIBoolean, Boolean>                   ATR_VISIVEL          = new AtrRef<>(MPacoteBasic.class, "visivel",
        MTipoBoolean.class, MIBoolean.class, Boolean.class);
    public static final AtrRef<MTipoBoolean, MIBoolean, Boolean>                   ATR_ENABLED          = new AtrRef<>(MPacoteBasic.class, "enabled",
        MTipoBoolean.class, MIBoolean.class, Boolean.class);
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final AtrRef<MTipoPredicate, MIPredicate, Predicate<MInstancia>> ATR_VISIBLE_FUNCTION = new AtrRef(MPacoteBasic.class, "visivelFunction",
        MTipoPredicate.class, MIPredicate.class, Predicate.class);
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final AtrRef<MTipoPredicate, MIPredicate, Predicate<MInstancia>> ATR_ENABLED_FUNCTION = new AtrRef(MPacoteBasic.class, "enabledFunction",
        MTipoPredicate.class, MIPredicate.class, Predicate.class);

    public static final AtrRef<MTipoInteger, MIInteger, Integer> ATR_ORDEM       = new AtrRef<>(MPacoteBasic.class, "ordemExibicao",
        MTipoInteger.class, MIInteger.class, Integer.class);
    public static final AtrRef<MTipoBoolean, MIBoolean, Boolean> ATR_MULTI_LINHA = new AtrRef<>(MPacoteBasic.class, "multiLinha",
        MTipoBoolean.class, MIBoolean.class, Boolean.class);

    public MPacoteBasic() {
        super(NOME);
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {

        // Cria os tipos de atributos
        pb.createTipoAtributo(ATR_TAMANHO_MAXIMO);
        pb.createTipoAtributo(ATR_TAMANHO_EDICAO);
        pb.createTipoAtributo(ATR_TAMANHO_INICIAL);
        pb.createTipoAtributo(ATR_MULTI_LINHA).withDefaultValueIfNull(false);

        // Aplica os atributos ao tipos
        pb.createTipoAtributo(MTipo.class, ATR_LABEL);
        pb.createTipoAtributo(MTipo.class, ATR_SUBTITLE);
        pb.createTipoAtributo(MTipo.class, ATR_BASIC_MASK);
        pb.createTipoAtributo(MTipo.class, ATR_VISIVEL).withDefaultValueIfNull(true);
        pb.createTipoAtributo(MTipo.class, ATR_ENABLED).withDefaultValueIfNull(true);
        pb.createTipoAtributo(MTipo.class, ATR_VISIBLE_FUNCTION);
        pb.createTipoAtributo(MTipo.class, ATR_ENABLED_FUNCTION);
        pb.createTipoAtributo(MTipo.class, ATR_ORDEM);

        pb.addAtributo(MTipoString.class, ATR_TAMANHO_MAXIMO, 100);
        pb.addAtributo(MTipoString.class, ATR_TAMANHO_EDICAO, 50);
        pb.addAtributo(MTipoString.class, ATR_MULTI_LINHA);

        pb.addAtributo(MTipoInteger.class, ATR_TAMANHO_MAXIMO);
        pb.addAtributo(MTipoInteger.class, ATR_TAMANHO_EDICAO);
        pb.addAtributo(MTipoLista.class, ATR_TAMANHO_INICIAL);

        pb.addAtributo(MTipoData.class, ATR_TAMANHO_EDICAO, 10);

        // defina o meta dado do meta dado
        pb.getAtributo(ATR_LABEL).as(AtrBasic.class).label("Label").tamanhoEdicao(30).tamanhoMaximo(50);
        pb.getAtributo(ATR_SUBTITLE).as(AtrBasic.class).label("Subtítulo").tamanhoEdicao(30).tamanhoMaximo(50);
        pb.getAtributo(ATR_BASIC_MASK).as(AtrBasic.class).label("Máscara básica").tamanhoEdicao(10).tamanhoMaximo(20);
        pb.getAtributo(ATR_TAMANHO_MAXIMO).as(AtrBasic.class).label("Tamanho maximo").tamanhoEdicao(3).tamanhoMaximo(4);
        pb.getAtributo(ATR_TAMANHO_EDICAO).as(AtrBasic.class).label("Tamanho edição").tamanhoEdicao(3).tamanhoMaximo(3);
        pb.getAtributo(ATR_MULTI_LINHA).as(AtrBasic.class).label("Multi linha");
        pb.getAtributo(ATR_VISIVEL).as(AtrBasic.class).label("Visível");
        pb.getAtributo(ATR_ENABLED).as(AtrBasic.class).label("Habilitado");
        pb.getAtributo(ATR_VISIBLE_FUNCTION).as(AtrBasic.class).label("Visível (função)");
        pb.getAtributo(ATR_ENABLED_FUNCTION).as(AtrBasic.class).label("Habilitado (função)");
        pb.getAtributo(ATR_ORDEM).as(AtrBasic.class).label("Ordem");
    }

    public static Function<MAtributoEnabled, AtrBasic> aspect() {
        return AtrBasic::new;
    }
}
