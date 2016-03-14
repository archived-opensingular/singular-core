package br.net.mirante.singular.form.mform.core;

import java.util.function.Function;
import java.util.function.Predicate;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SAttributeEnabled;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIPredicate;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeCalculation;
import br.net.mirante.singular.form.mform.STypeCode;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.STypePredicate;
import br.net.mirante.singular.form.mform.STypeSimple;
import br.net.mirante.singular.form.mform.core.annotation.STypeAnnotation;
import br.net.mirante.singular.form.mform.core.annotation.STypeAnnotationList;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SPackageCore extends SPackage {

    public static final String NOME = "mform.core";

    //@formatter:off
    public static final AtrRef<?, ?, Object>                     ATR_VALOR_INICIAL   = AtrRef.ofSelfReference(SPackageCore.class, "valorInicial");
    public static final AtrRef<?, ?, Object>                     ATR_DEFAULT_IF_NULL = AtrRef.ofSelfReference(SPackageCore.class, "defaultIfNull");
    public static final AtrRef<STypeBoolean, SIBoolean, Boolean> ATR_TRIM            = new AtrRef<>(SPackageCore.class, "trim", STypeBoolean.class, SIBoolean.class, Boolean.class);
    public static final AtrRef<STypeBoolean, SIBoolean, Boolean> ATR_EMPTY_TO_NULL   = new AtrRef<>(SPackageCore.class, "emptyToNull", STypeBoolean.class, SIBoolean.class, Boolean.class);

    public static final AtrRef<STypeBoolean, SIBoolean, Boolean> ATR_REQUIRED     = new AtrRef<>(SPackageCore.class, "obrigatorio", STypeBoolean.class, SIBoolean.class, Boolean.class);
    public static final AtrRef<STypePredicate, SIPredicate, Predicate<SInstance>> ATR_OBRIGATORIO_FUNCTION = new AtrRef(SPackageCore.class, "obrigatorioFunction", STypePredicate.class, SIPredicate.class, Predicate.class);

    public static final AtrRef<STypeBoolean, SIBoolean, Boolean> ATR_EXISTS          = new AtrRef<>(SPackageCore.class, "exists", STypeBoolean.class, SIBoolean.class, Boolean.class);
    public static final AtrRef<STypePredicate, SIPredicate, Predicate<SInstance>> ATR_EXISTS_FUNCTION      = new AtrRef(SPackageCore.class, "existsFunction", STypePredicate.class, SIPredicate.class, Predicate.class);

    public static final AtrRef<STypeFormula, SIComposite, Object> ATR_FORMULA         = new AtrRef<>(SPackageCore.class, "formula", STypeFormula.class, SIComposite.class, Object.class);

    public static final AtrRef<STypeCalculation, SInstance, Object> ATR_CALCULATION         = new AtrRef<>(SPackageCore.class, "calculation", STypeCalculation.class, SInstance.class, Object.class);

    //@formatter:on

    public SPackageCore() {
        super(NOME);
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        pb.createType(SType.class);
        pb.createType(STypeSimple.class);

        pb.createType(STypeList.class);
        pb.createType(STypeCode.class);
        pb.createType(STypePredicate.class);

        pb.createType(STypeString.class);
        pb.createType(STypeInteger.class);
        pb.createType(STypeBoolean.class);
        pb.createType(STypeDate.class);
        pb.createType(STypeDecimal.class);
        pb.createType(STypeMonetary.class);
        pb.createType(STypeDateTime.class);
        pb.createType(STypeTime.class);

        pb.createType(STypeComposite.class);

        pb.createType(STypeAnnotation.class);
        pb.createType(STypeAnnotationList.class);

        pb.createAttributeIntoType(SType.class, ATR_REQUIRED);
        pb.createAttributeIntoType(SType.class, ATR_OBRIGATORIO_FUNCTION);
        pb.createAttributeIntoType(SType.class, ATR_EXISTS);
        pb.createAttributeIntoType(SType.class, ATR_EXISTS_FUNCTION);
        pb.createAttributeIntoType(SType.class, ATR_DEFAULT_IF_NULL);

        pb.createAttributeIntoType(STypeSimple.class, ATR_VALOR_INICIAL);
        //pb.createTipoAtributo(MTipoSimples.class, ATR_DEFAULT_IF_NULL);

        pb.getAttribute(ATR_REQUIRED).withDefaultValueIfNull(false);
        pb.getAttribute(ATR_EXISTS).withDefaultValueIfNull(true);

        pb.createAttributeIntoType(STypeString.class, ATR_TRIM).withDefaultValueIfNull(true);
        pb.createAttributeIntoType(STypeString.class, ATR_EMPTY_TO_NULL).withDefaultValueIfNull(true);

        pb.createType(STypeFormula.class);
        pb.createAttributeIntoType(STypeSimple.class, ATR_FORMULA);

        pb.createType(STypeAttachment.class);
        pb.createAttributeIntoType(STypeAttachment.class, STypeAttachment.ATR_ORIGINAL_ID);
        pb.createAttributeIntoType(STypeAttachment.class, STypeAttachment.ATR_IS_TEMPORARY);

        pb.createType(STypeCalculation.class);
        pb.createAttributeIntoType(STypeSimple.class, ATR_CALCULATION);

        pb.createType(STypeLatitudeLongitude.class);
    }

    public static Function<SAttributeEnabled, AtrCore> aspect() {
        return AtrCore::new;
    }
}
