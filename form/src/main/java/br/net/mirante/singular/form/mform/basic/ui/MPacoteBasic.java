package br.net.mirante.singular.form.mform.basic.ui;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.MAtributoEnabled;
import br.net.mirante.singular.form.mform.MIComposto;
import br.net.mirante.singular.form.mform.MPacote;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTipoComposto;
import br.net.mirante.singular.form.mform.MTipoSimples;
import br.net.mirante.singular.form.mform.PacoteBuilder;
import br.net.mirante.singular.form.mform.core.MIBoolean;
import br.net.mirante.singular.form.mform.core.MIInteger;
import br.net.mirante.singular.form.mform.core.MIString;
import br.net.mirante.singular.form.mform.core.MTipoBoolean;
import br.net.mirante.singular.form.mform.core.MTipoInteger;
import br.net.mirante.singular.form.mform.core.MTipoString;

public class MPacoteBasic extends MPacote {

    public static final String NOME = "mform.basic";

    public static final AtrRef<MTipoString, MIString, String> ATR_LABEL = new AtrRef<>(MPacoteBasic.class, "label", MTipoString.class,
            MIString.class, String.class);
    public static final AtrRef<MTipoInteger, MIInteger, Integer> ATR_TAMANHO_MAXIMO = new AtrRef<>(MPacoteBasic.class, "tamanhoMaximo",
            MTipoInteger.class, MIInteger.class, Integer.class);
    public static final AtrRef<MTipoInteger, MIInteger, Integer> ATR_TAMANHO_EDICAO = new AtrRef<>(MPacoteBasic.class, "tamanhoEdicao",
            MTipoInteger.class, MIInteger.class, Integer.class);
    public static final AtrRef<MTipoBoolean, MIBoolean, Boolean> ATR_VISIVEL = new AtrRef<>(MPacoteBasic.class, "visivel",
            MTipoBoolean.class, MIBoolean.class, Boolean.class);
    public static final AtrRef<MTipoInteger, MIInteger, Integer> ATR_ORDEM = new AtrRef<>(MPacoteBasic.class, "ordemExibicao",
            MTipoInteger.class, MIInteger.class, Integer.class);
    public static final AtrRef<MTipoComposto, MIComposto, Object> ATR_POSICAO_TELA = new AtrRef<>(MPacoteBasic.class, "posicaoTela",
            MTipoComposto.class, MIComposto.class, null);

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
        pb.createTipoAtributo(ATR_MULTI_LINHA).withDefaultValueIfNull(false);

        // Aplica os atributos ao tipos
        pb.createTipoAtributo(MTipo.class, ATR_LABEL);
        pb.createTipoAtributo(MTipo.class, ATR_VISIVEL).withDefaultValueIfNull(true);
        pb.createTipoAtributo(MTipo.class, ATR_ORDEM);

        pb.addAtributo(MTipoString.class, ATR_TAMANHO_MAXIMO, 100);
        pb.addAtributo(MTipoString.class, ATR_TAMANHO_EDICAO, 50);
        pb.addAtributo(MTipoString.class, ATR_MULTI_LINHA);

        pb.addAtributo(MTipoInteger.class, ATR_TAMANHO_MAXIMO);
        pb.addAtributo(MTipoInteger.class, ATR_TAMANHO_EDICAO);

        // defina o meta dado do meta dado
        pb.getAtributo(ATR_LABEL).as(AtrBasic.class).label("Label").tamanhoEdicao(30).tamanhoMaximo(50);
        pb.getAtributo(ATR_TAMANHO_MAXIMO).as(AtrBasic.class).label("Tamanho maximo").tamanhoEdicao(3).tamanhoMaximo(4);
        pb.getAtributo(ATR_TAMANHO_EDICAO).as(AtrBasic.class).label("Tamanho edição").tamanhoEdicao(3).tamanhoMaximo(3);
        pb.getAtributo(ATR_MULTI_LINHA).as(AtrBasic.class).label("Multi linha");
        pb.getAtributo(ATR_VISIVEL).as(AtrBasic.class).label("Visível");
        pb.getAtributo(ATR_ORDEM).as(AtrBasic.class).label("Ordem");

        MTipoComposto<?> tipoPosicao = pb.createTipoComposto("PosicaoTela");
        tipoPosicao.addCampo("lin", MTipoInteger.class);
        tipoPosicao.addCampo("col", MTipoInteger.class);
        tipoPosicao.addCampo("colSpan", MTipoInteger.class).withDefaultValueIfNull(1);

        tipoPosicao.getCampo("lin").as(AtrBasic.class).label("linha").tamanhoEdicao(3);

        pb.createTipoAtributo(MTipoSimples.class, ATR_POSICAO_TELA, tipoPosicao);
    }
    
    public static AtrBasic atr(MAtributoEnabled o) {
        return new AtrBasic(o);
    }
    public static MBasicAtr atr() {
        return MBasicAtr.begin();
    }
}
