package br.net.mirante.singular.form.mform.basic.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

import br.net.mirante.singular.form.mform.MAtributoEnabled;
import br.net.mirante.singular.form.mform.SInstance2;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.MTranslatorParaAtributo;

public class AtrBasic extends MTranslatorParaAtributo {

    public AtrBasic() {}
    public AtrBasic(MAtributoEnabled alvo) {
        super(alvo);
    }

    public AtrBasic label(String valor) {
        getAlvo().setValorAtributo(SPackageBasic.ATR_LABEL, valor);
        return this;
    }

    public AtrBasic subtitle(String valor) {
        getAlvo().setValorAtributo(SPackageBasic.ATR_SUBTITLE, valor);
        return this;
    }

    public AtrBasic basicMask(String mask) {
        getAlvo().setValorAtributo(SPackageBasic.ATR_BASIC_MASK, mask);
        return this;
    }

    public AtrBasic tamanhoEdicao(Integer valor) {
        getAlvo().setValorAtributo(SPackageBasic.ATR_TAMANHO_EDICAO, valor);
        return this;
    }

    public AtrBasic tamanhoMaximo(Integer valor) {
        getAlvo().setValorAtributo(SPackageBasic.ATR_TAMANHO_MAXIMO, valor);
        return this;
    }

    public AtrBasic tamanhoInteiroMaximo(Integer valor) {
        getAlvo().setValorAtributo(SPackageBasic.ATR_TAMANHO_INTEIRO_MAXIMO, valor);
        return this;
    }

    public AtrBasic tamanhoDecimalMaximo(Integer valor) {
        getAlvo().setValorAtributo(SPackageBasic.ATR_TAMANHO_DECIMAL_MAXIMO, valor);
        return this;
    }

    public AtrBasic visivel(Boolean valor) {
        getAlvo().setValorAtributo(SPackageBasic.ATR_VISIVEL, valor);
        return this;
    }
    public AtrBasic visivel(Predicate<SInstance2> valor) {
        getAlvo().setValorAtributo(SPackageBasic.ATR_VISIBLE_FUNCTION, valor);
        return this;
    }

    public AtrBasic enabled(Boolean valor) {
        getAlvo().setValorAtributo(SPackageBasic.ATR_ENABLED, valor);
        return this;
    }
    public AtrBasic enabled(Predicate<SInstance2> valor) {
        getAlvo().setValorAtributo(SPackageBasic.ATR_ENABLED_FUNCTION, valor);
        return this;
    }
    public AtrBasic dependsOn(Supplier<Collection<SType<?>>> valor) {
        getAlvo().setValorAtributo(SPackageBasic.ATR_DEPENDS_ON_FUNCTION, valor);
        return this;
    }
    public AtrBasic dependsOn(SType<?>... tipos) {
        return dependsOn(() -> Arrays.asList(tipos));
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
        return getAlvo().getValorAtributo(SPackageBasic.ATR_LABEL);
    }

    public String getSubtitle() {
        return getAlvo().getValorAtributo(SPackageBasic.ATR_SUBTITLE);
    }

    public Integer getTamanhoEdicao() {
        return getAlvo().getValorAtributo(SPackageBasic.ATR_TAMANHO_EDICAO);
    }

    public Integer getTamanhoMaximo() {
        return getAlvo().getValorAtributo(SPackageBasic.ATR_TAMANHO_MAXIMO);
    }

    public boolean isVisible() {
        return !Boolean.FALSE.equals(getAlvo().getValorAtributo(SPackageBasic.ATR_VISIVEL));
    }
    
    public boolean isEnabled() {
        return !Boolean.FALSE.equals(getAlvo().getValorAtributo(SPackageBasic.ATR_ENABLED));
    }
    
}
