package br.net.mirante.singular.form.mform.core;

import java.util.Optional;

import br.net.mirante.singular.form.mform.AtrRef;
import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.basic.ui.SPackageBasic;

public class SPackageBootstrap extends SPackage {

    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_COL_PREFERENCE
            = new AtrRef<>(SPackageBootstrap.class, "larguraColuna", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_COL_XS_PREFERENCE
            = new AtrRef<>(SPackageBootstrap.class, "larguraColunaXS", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_COL_SM_PREFERENCE
            = new AtrRef<>(SPackageBootstrap.class, "larguraColunaSM", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_COL_MD_PREFERENCE
            = new AtrRef<>(SPackageBootstrap.class, "larguraColunaMD", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeInteger, SIInteger, Integer> ATR_COL_LG_PREFERENCE
            = new AtrRef<>(SPackageBootstrap.class, "larguraColunaLG", STypeInteger.class, SIInteger.class, Integer.class);
    public static final AtrRef<STypeBoolean, SIBoolean, Boolean> ATR_COL_ON_NEW_ROW
            = new AtrRef<>(SPackageBootstrap.class, "newRow", STypeBoolean.class, SIBoolean.class, Boolean.class);

    public SPackageBootstrap() {
        super("mform.plaf.bootstrap");
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        adicionarDefinicaoColuna(pb, ATR_COL_PREFERENCE, null);
        adicionarDefinicaoColuna(pb, ATR_COL_XS_PREFERENCE, "XS");
        adicionarDefinicaoColuna(pb, ATR_COL_SM_PREFERENCE, "SM");
        adicionarDefinicaoColuna(pb, ATR_COL_MD_PREFERENCE, "MD");
        adicionarDefinicaoColuna(pb, ATR_COL_LG_PREFERENCE, "LG");

        pb.createTipoAtributo(SType.class, ATR_COL_ON_NEW_ROW);

    }

    private void adicionarDefinicaoColuna(PackageBuilder pb, AtrRef<?, ?, ?> atrRef, String label) {
        Optional<String> labelOp = Optional.ofNullable(label);
        pb.createTipoAtributo(atrRef);
        pb.addAtributo(SType.class, atrRef);
        pb.getAtributo(atrRef).as(SPackageBasic.aspect()).label(("Largura preferencial " + labelOp.orElse("")).trim());
    }

}
