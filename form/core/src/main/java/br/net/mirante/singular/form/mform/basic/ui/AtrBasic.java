package br.net.mirante.singular.form.mform.basic.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

import br.net.mirante.singular.form.mform.SAttributeEnabled;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.STranslatorForAttribute;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.calculation.SimpleValueCalculation;
import br.net.mirante.singular.form.mform.freemarker.FormFreemarkerUtil;

public class AtrBasic extends STranslatorForAttribute {

    public AtrBasic() {}
    public AtrBasic(SAttributeEnabled alvo) {
        super(alvo);
    }

    public AtrBasic label(String valor) {
        getTarget().setAttributeValue(SPackageBasic.ATR_LABEL, valor);
        return this;
    }

    public AtrBasic subtitle(String valor) {
        getTarget().setAttributeValue(SPackageBasic.ATR_SUBTITLE, valor);
        return this;
    }

    public AtrBasic basicMask(String mask) {
        getTarget().setAttributeValue(SPackageBasic.ATR_BASIC_MASK, mask);
        return this;
    }

    public AtrBasic tamanhoEdicao(Integer valor) {
        getTarget().setAttributeValue(SPackageBasic.ATR_TAMANHO_EDICAO, valor);
        return this;
    }

    public AtrBasic tamanhoMaximo(Integer valor) {
        getTarget().setAttributeValue(SPackageBasic.ATR_TAMANHO_MAXIMO, valor);
        return this;
    }

    public AtrBasic tamanhoInteiroMaximo(Integer valor) {
        getTarget().setAttributeValue(SPackageBasic.ATR_TAMANHO_INTEIRO_MAXIMO, valor);
        return this;
    }

    public AtrBasic tamanhoDecimalMaximo(Integer valor) {
        getTarget().setAttributeValue(SPackageBasic.ATR_TAMANHO_DECIMAL_MAXIMO, valor);
        return this;
    }

    public AtrBasic visivel(Boolean valor) {
        getTarget().setAttributeValue(SPackageBasic.ATR_VISIVEL, valor);
        return this;
    }
    public AtrBasic visivel(Predicate<SInstance> valor) {
        getTarget().setAttributeValue(SPackageBasic.ATR_VISIBLE_FUNCTION, valor);
        return this;
    }

    public AtrBasic enabled(Boolean valor) {
        getTarget().setAttributeValue(SPackageBasic.ATR_ENABLED, valor);
        return this;
    }
    public AtrBasic enabled(Predicate<SInstance> valor) {
        getTarget().setAttributeValue(SPackageBasic.ATR_ENABLED_FUNCTION, valor);
        return this;
    }
    public AtrBasic dependsOn(Supplier<Collection<SType<?>>> valor) {
        getTarget().setAttributeValue(SPackageBasic.ATR_DEPENDS_ON_FUNCTION, valor);
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
        return getTarget().getAttributeValue(SPackageBasic.ATR_LABEL);
    }

    public String getSubtitle() {
        return getTarget().getAttributeValue(SPackageBasic.ATR_SUBTITLE);
    }

    public Integer getTamanhoEdicao() {
        return getTarget().getAttributeValue(SPackageBasic.ATR_TAMANHO_EDICAO);
    }

    public Integer getTamanhoMaximo() {
        return getTarget().getAttributeValue(SPackageBasic.ATR_TAMANHO_MAXIMO);
    }

    public boolean isVisible() {
        return !Boolean.FALSE.equals(getTarget().getAttributeValue(SPackageBasic.ATR_VISIVEL));
    }

    public boolean isEnabled() {
        return !Boolean.FALSE.equals(getTarget().getAttributeValue(SPackageBasic.ATR_ENABLED));
    }

    public AtrBasic displayString(String displayStringTemplate) {
        return displayString(FormFreemarkerUtil.createInstanceCalculation(displayStringTemplate));
    }

    public AtrBasic displayString(SimpleValueCalculation<String> valueCalculation) {
        getTarget().setAttributeCalculation(SPackageBasic.ATR_DISPLAY_STRING, valueCalculation);
        return this;
    }

    public String getDisplayString() {
        return getTarget().getAttributeValue(SPackageBasic.ATR_DISPLAY_STRING);
    }
}
