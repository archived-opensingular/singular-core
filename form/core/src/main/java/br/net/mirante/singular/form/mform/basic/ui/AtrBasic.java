package br.net.mirante.singular.form.mform.basic.ui;

import br.net.mirante.singular.form.mform.MAtributoEnabled;
import br.net.mirante.singular.form.mform.MInstancia;
import br.net.mirante.singular.form.mform.MTipo;
import br.net.mirante.singular.form.mform.MTranslatorParaAtributo;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AtrBasic extends MTranslatorParaAtributo {

    public AtrBasic() {}
    public AtrBasic(MAtributoEnabled alvo) {
        super(alvo);
    }

    public AtrBasic label(String valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_LABEL, valor);
        return this;
    }

    public AtrBasic subtitle(String valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_SUBTITLE, valor);
        return this;
    }

    public AtrBasic basicMask(String mask) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_BASIC_MASK, mask);
        return this;
    }

    public AtrBasic tamanhoEdicao(Integer valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_TAMANHO_EDICAO, valor);
        return this;
    }

    public AtrBasic tamanhoInicial(Integer valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_TAMANHO_INICIAL, valor);
        return this;
    }

    public AtrBasic tamanhoMaximo(Integer valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_TAMANHO_MAXIMO, valor);
        return this;
    }

    public AtrBasic visivel(Boolean valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_VISIVEL, valor);
        return this;
    }
    public AtrBasic visivel(Predicate<MInstancia> valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_VISIBLE_FUNCTION, valor);
        return this;
    }

    public AtrBasic enabled(Boolean valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_ENABLED, valor);
        return this;
    }
    public AtrBasic enabled(Predicate<MInstancia> valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_ENABLED_FUNCTION, valor);
        return this;
    }
    public AtrBasic depends(Supplier<Collection<MTipo<?>>> valor) {
        getAlvo().setValorAtributo(MPacoteBasic.ATR_DEPENDS_FUNCTION, valor);
        return this;
    }
    public AtrBasic depends(MTipo<?>... tipos) {
        return depends(() -> Arrays.asList(tipos));
    }

    //    public AtrBasic onChange(Function<IBehavior<MInstancia>, IBehavior<MInstancia>> behaviorFunction) {
    //        IBehavior<MInstancia> existingBehavior = getOnChange();
    //        IBehavior<MInstancia> newBehavior = behaviorFunction.apply(IBehavior.noopIfNull(existingBehavior));
    //        getAlvo().setValorAtributo(MPacoteBasic.ATR_ONCHANGE_BEHAVIOR, newBehavior);
    //        return this;
    //    }
    //    public AtrBasic onChange(IBehavior<MInstancia> behavior) {
    //        onChange(old -> old.andThen(behavior));
    //        return this;
    //    }
    //    public boolean hasOnChange() {
    //        return getOnChange() != null;
    //    }
    //    @SuppressWarnings("unchecked")
    //    public IBehavior<MInstancia> getOnChange() {
    //        return (IBehavior<MInstancia>) getAlvo().getValorAtributo(MPacoteBasic.ATR_ONCHANGE_BEHAVIOR.getNomeCompleto());
    //    }

    public String getLabel() {
        return getAlvo().getValorAtributo(MPacoteBasic.ATR_LABEL);
    }

    public String getSubtitle() {
        return getAlvo().getValorAtributo(MPacoteBasic.ATR_SUBTITLE);
    }

    public Integer getTamanhoEdicao() {
        return getAlvo().getValorAtributo(MPacoteBasic.ATR_TAMANHO_EDICAO);
    }

    public Integer getTamanhoInicial() {
        return getAlvo().getValorAtributo(MPacoteBasic.ATR_TAMANHO_INICIAL);
    }

    public Integer getTamanhoMaximo() {
        return getAlvo().getValorAtributo(MPacoteBasic.ATR_TAMANHO_MAXIMO);
    }

    public Boolean isVisivel() {
        return getAlvo().getValorAtributo(MPacoteBasic.ATR_VISIVEL);
    }

}
