package br.net.mirante.singular.form.mform.core;

import java.util.function.Function;
import java.util.function.Predicate;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.MAtributoEnabled;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIPredicate;
import br.net.mirante.singular.form.mform.SInstance2;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeCode;
import br.net.mirante.singular.form.mform.STypeComposto;
import br.net.mirante.singular.form.mform.STypeLista;
import br.net.mirante.singular.form.mform.STypePredicate;
import br.net.mirante.singular.form.mform.STypeSimples;
import br.net.mirante.singular.form.mform.PacoteBuilder;
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

    public static final AtrRef<STypeBoolean, SIBoolean, Boolean> ATR_OBRIGATORIO     = new AtrRef<>(SPackageCore.class, "obrigatorio", STypeBoolean.class, SIBoolean.class, Boolean.class);
    public static final AtrRef<STypePredicate, SIPredicate, Predicate<SInstance2>> ATR_OBRIGATORIO_FUNCTION = new AtrRef(SPackageCore.class, "obrigatorioFunction", STypePredicate.class, SIPredicate.class, Predicate.class);

    public static final AtrRef<STypeBoolean, SIBoolean, Boolean> ATR_EXISTS          = new AtrRef<>(SPackageCore.class, "exists", STypeBoolean.class, SIBoolean.class, Boolean.class);
    public static final AtrRef<STypePredicate, SIPredicate, Predicate<SInstance2>> ATR_EXISTS_FUNCTION      = new AtrRef(SPackageCore.class, "existsFunction", STypePredicate.class, SIPredicate.class, Predicate.class);
    
    public static final AtrRef<STypeFormula, SIComposite, Object> ATR_FORMULA         = new AtrRef<>(SPackageCore.class, "formula", STypeFormula.class, SIComposite.class, Object.class);
    
    
    
    //@formatter:on

    public SPackageCore() {
        super(NOME);
    }

    @Override
    protected void carregarDefinicoes(PacoteBuilder pb) {
        pb.createTipo(SType.class);
        pb.createTipo(STypeSimples.class);

        pb.createTipo(STypeLista.class);
        pb.createTipo(STypeCode.class);
        pb.createTipo(STypePredicate.class);

        pb.createTipo(STypeString.class);
        pb.createTipo(STypeInteger.class);
        pb.createTipo(STypeBoolean.class);
        pb.createTipo(STypeData.class);
        pb.createTipo(STypeDecimal.class);
        pb.createTipo(STypeMonetario.class);
        pb.createTipo(STypeDataHora.class);

        pb.createTipo(STypeComposto.class);

        pb.createTipo(STypeAnnotation.class);
        pb.createTipo(STypeAnnotationList.class);

        pb.createTipoAtributo(SType.class, ATR_OBRIGATORIO);
        pb.createTipoAtributo(SType.class, ATR_OBRIGATORIO_FUNCTION);
        pb.createTipoAtributo(SType.class, ATR_EXISTS);
        pb.createTipoAtributo(SType.class, ATR_EXISTS_FUNCTION);
        pb.createTipoAtributo(SType.class, ATR_DEFAULT_IF_NULL);

        pb.createTipoAtributo(STypeSimples.class, ATR_VALOR_INICIAL);
        //pb.createTipoAtributo(MTipoSimples.class, ATR_DEFAULT_IF_NULL);

        pb.getAtributo(ATR_OBRIGATORIO).withDefaultValueIfNull(false);
        pb.getAtributo(ATR_EXISTS).withDefaultValueIfNull(true);

        pb.createTipoAtributo(STypeString.class, ATR_TRIM).withDefaultValueIfNull(true);
        pb.createTipoAtributo(STypeString.class, ATR_EMPTY_TO_NULL).withDefaultValueIfNull(true);

        pb.createTipo(STypeFormula.class);
        pb.createTipoAtributo(STypeSimples.class, ATR_FORMULA);

        pb.createTipo(STypeAttachment.class);
        pb.createTipoAtributo(STypeAttachment.class, STypeAttachment.ATR_ORIGINAL_ID);
        pb.createTipoAtributo(STypeAttachment.class, STypeAttachment.ATR_IS_TEMPORARY);
        
        pb.createTipo(STypeLatitudeLongitude.class);
    }

    public static Function<MAtributoEnabled, AtrCore> aspect() {
        return AtrCore::new;
    }
}
