package br.net.mirante.singular.form.mform.basic.ui;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SAttributeEnabled;
import br.net.mirante.singular.form.mform.SIPredicate;
import br.net.mirante.singular.form.mform.SISupplier;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeBehavior;
import br.net.mirante.singular.form.mform.STypePredicate;
import br.net.mirante.singular.form.mform.STypeSupplier;
import br.net.mirante.singular.form.mform.core.SIBoolean;
import br.net.mirante.singular.form.mform.core.SIInteger;
import br.net.mirante.singular.form.mform.core.SIString;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeDate;
import br.net.mirante.singular.form.mform.core.STypeDecimal;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeString;

public class SPackageBasic extends SPackage {

    public static final String NOME = "mform.basic";

    //@formatter:off
    public static final AtrRef<STypeString, SIString, String>                      ATR_LABEL                    = new AtrRef<>(SPackageBasic.class, "label", STypeString.class, SIString.class, String.class);
    public static final AtrRef<STypeString, SIString, String>                      ATR_SUBTITLE                 = new AtrRef<>(SPackageBasic.class, "subtitle", STypeString.class, SIString.class, String.class);
    public static final AtrRef<STypeString, SIString, String>                      ATR_BASIC_MASK               = new AtrRef<>(SPackageBasic.class, "basicMask", STypeString.class, SIString.class, String.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer>                   ATR_TAMANHO_MAXIMO           = new AtrRef<>(SPackageBasic.class, "tamanhoMaximo", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer>                   ATR_TAMANHO_INTEIRO_MAXIMO   = new AtrRef<>(SPackageBasic.class, "tamanhoInteiroMaximo", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer>                   ATR_TAMANHO_DECIMAL_MAXIMO   = new AtrRef<>(SPackageBasic.class, "tamanhoDecimalMaximo", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer>                   ATR_TAMANHO_EDICAO           = new AtrRef<>(SPackageBasic.class, "tamanhoEdicao", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeBoolean, SIBoolean, Boolean>                   ATR_VISIVEL                  = new AtrRef<>(SPackageBasic.class, "visivel", STypeBoolean.class, SIBoolean.class, Boolean.class);
    public static final AtrRef<STypeBoolean, SIBoolean, Boolean>                   ATR_ENABLED                  = new AtrRef<>(SPackageBasic.class, "enabled", STypeBoolean.class, SIBoolean.class, Boolean.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer>                   ATR_ORDEM                    = new AtrRef<>(SPackageBasic.class, "ordemExibicao", STypeInteger.class, SIInteger.class, Integer.class);
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final AtrRef<STypePredicate, SIPredicate, Predicate<SInstance>> ATR_VISIBLE_FUNCTION         = new AtrRef(SPackageBasic.class, "visivelFunction", STypePredicate.class, SIPredicate.class, Predicate.class);
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final AtrRef<STypePredicate, SIPredicate, Predicate<SInstance>> ATR_ENABLED_FUNCTION         = new AtrRef(SPackageBasic.class, "enabledFunction", STypePredicate.class, SIPredicate.class, Predicate.class);
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static final AtrRef<STypeSupplier<Collection<SType<?>>>, SISupplier<Collection<SType<?>>>, Supplier<Collection<SType<?>>>>
                                                                                   ATR_DEPENDS_ON_FUNCTION      = new AtrRef(SPackageBasic.class, "dependsOnFunction", STypeSupplier.class, SISupplier.class, Supplier.class);
    //    @SuppressWarnings({ "unchecked", "rawtypes" })
    //    public static final AtrRef<MTipoBehavior, MIBehavior, IBehavior<MInstancia>>   ATR_ONCHANGE_BEHAVIOR = new AtrRef(MPacoteBasic.class, "onchangeBehavior", MTipoBehavior.class, MIBehavior.class, IBehavior.class);

    public static final AtrRef<STypeBoolean, SIBoolean, Boolean>                   ATR_ANNOTATED                  = new AtrRef<>(SPackageBasic.class, "anotated", STypeBoolean.class, SIBoolean.class, Boolean.class);
    public static final AtrRef<STypeString, SIString, String>                      ATR_ANNOTATION_LABEL           = new AtrRef<>(SPackageBasic.class, "annotation_label", STypeString.class, SIString.class, String.class);

    /** Representação string da instância. Em geral, deverá ser uma string cálculada dinamicamente com base nos dados da instância.  */
    public static final AtrRef<STypeString, SIString, String>                      ATR_DISPLAY_STRING           = new AtrRef<>(SPackageBasic.class, "displayString", STypeString.class, SIString.class, String.class);

    //@formatter:on

    public SPackageBasic() {
        super(NOME);
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {

        pb.createType(STypeBehavior.class);
        pb.createType(STypeSupplier.class);

        // Cria os tipos de atributos
        pb.createAttributeType(ATR_TAMANHO_MAXIMO);
        pb.createAttributeType(ATR_TAMANHO_INTEIRO_MAXIMO);
        pb.createAttributeType(ATR_TAMANHO_DECIMAL_MAXIMO);
        pb.createAttributeType(ATR_TAMANHO_EDICAO);

        // Aplica os atributos ao tipos
        pb.createAttributeIntoType(SType.class, ATR_LABEL);
        pb.createAttributeIntoType(SType.class, ATR_SUBTITLE);
        pb.createAttributeIntoType(SType.class, ATR_BASIC_MASK);
        pb.createAttributeIntoType(SType.class, ATR_VISIVEL);
        pb.createAttributeIntoType(SType.class, ATR_ENABLED);
        pb.createAttributeIntoType(SType.class, ATR_VISIBLE_FUNCTION);
        pb.createAttributeIntoType(SType.class, ATR_ENABLED_FUNCTION);
        pb.createAttributeIntoType(SType.class, ATR_DEPENDS_ON_FUNCTION);
        //        pb.createTipoAtributo(MTipo.class, ATR_ONCHANGE_BEHAVIOR);
        pb.createAttributeIntoType(SType.class, ATR_ORDEM);
        pb.createAttributeIntoType(SType.class, ATR_ANNOTATED);
        pb.createAttributeIntoType(SType.class, ATR_ANNOTATION_LABEL);

        pb.createAttributeIntoType(SType.class, ATR_DISPLAY_STRING);

        pb.addAttribute(STypeString.class, ATR_TAMANHO_MAXIMO, 100);
        pb.addAttribute(STypeString.class, ATR_TAMANHO_EDICAO, 50);

        pb.addAttribute(STypeInteger.class, ATR_TAMANHO_MAXIMO);
        pb.addAttribute(STypeInteger.class, ATR_TAMANHO_EDICAO);

        pb.addAttribute(STypeDate.class, ATR_TAMANHO_EDICAO, 10);

        pb.addAttribute(STypeDecimal.class, ATR_TAMANHO_INTEIRO_MAXIMO, 9);
        pb.addAttribute(STypeDecimal.class, ATR_TAMANHO_DECIMAL_MAXIMO, 2);

        pb.getDictionary().getType(SType.class).asAtrBasic()
                .displayString(ctx -> ctx.instance().toStringDisplayDefault());

        // defina o meta dado do meta dado
        pb.getAttribute(ATR_LABEL).as(AtrBasic.class).label("Label").tamanhoEdicao(30).tamanhoMaximo(50);
        pb.getAttribute(ATR_SUBTITLE).as(AtrBasic.class).label("Subtítulo").tamanhoEdicao(30).tamanhoMaximo(50);
        pb.getAttribute(ATR_BASIC_MASK).as(AtrBasic.class).label("Máscara básica").tamanhoEdicao(10).tamanhoMaximo(20);
        pb.getAttribute(ATR_TAMANHO_MAXIMO).as(AtrBasic.class).label("Tamanho maximo").tamanhoEdicao(3).tamanhoMaximo(4);
        pb.getAttribute(ATR_TAMANHO_INTEIRO_MAXIMO).as(AtrBasic.class).label("Tamanho inteiro maximo").tamanhoEdicao(3).tamanhoMaximo(4);
        pb.getAttribute(ATR_TAMANHO_DECIMAL_MAXIMO).as(AtrBasic.class).label("Tamanho decimal maximo").tamanhoEdicao(3).tamanhoMaximo(4);
        pb.getAttribute(ATR_TAMANHO_EDICAO).as(AtrBasic.class).label("Tamanho edição").tamanhoEdicao(3).tamanhoMaximo(3);
        pb.getAttribute(ATR_VISIVEL).as(AtrBasic.class).label("Visível");
        pb.getAttribute(ATR_ENABLED).as(AtrBasic.class).label("Habilitado");
        pb.getAttribute(ATR_VISIBLE_FUNCTION).as(AtrBasic.class).label("Visível (função)");
        pb.getAttribute(ATR_ENABLED_FUNCTION).as(AtrBasic.class).label("Habilitado (função)");
        pb.getAttribute(ATR_DEPENDS_ON_FUNCTION).as(AtrBasic.class).label("Depende de (função)");
        //        pb.getAtributo(ATR_ONCHANGE_BEHAVIOR).as(AtrBasic.class).label("On change (comportamento)");
        pb.getAttribute(ATR_ORDEM).as(AtrBasic.class).label("Ordem");
    }

    public static Function<SAttributeEnabled, AtrBasic> aspect() {
        return AtrBasic::new;
    }
}
