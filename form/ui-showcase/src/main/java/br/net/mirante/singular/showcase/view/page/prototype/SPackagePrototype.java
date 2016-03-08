package br.net.mirante.singular.showcase.view.page.prototype;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.MFixedOptionsSimpleProvider;
import br.net.mirante.singular.form.mform.util.comuns.STypeCPF;

/**
 * Created by nuk on 07/03/16.
 */
public class SPackagePrototype  extends SPackage {

    public static final String  PACOTE = "mform.prototype",
                                META_FORM = "MetaForm",
                                META_FORM_COMPLETE = PACOTE + "." + META_FORM,
                                CHILDREN = "children";
    public static final String NAME_FIELD = "name";


    public SPackagePrototype() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        final STypeComposite<?> meta = pb.createTipoComposto(META_FORM);
        meta.addCampoString(NAME_FIELD).asAtrBasic().label("Nome");

        STypeLista<STypeComposite<SIComposite>, SIComposite> childFields =
                meta.addCampoListaOfComposto(CHILDREN, "field");

        childFields.asAtrBasic().label("Campos");

        STypeComposite<SIComposite> fieldType = childFields.getTipoElementos();

        fieldType.addCampoString("name")
                .asAtrBasic().label("Nome")
                .getTipo().asAtrBootstrap().colPreference(2);
        ;
        STypeString type = fieldType.addCampoString("type");
        type.asAtrBasic().label("Tipo")
                .getTipo().asAtrBootstrap().colPreference(2);
        populateOptions(pb, type.withSelection());

    }

    private void populateOptions(PackageBuilder pb, MFixedOptionsSimpleProvider provider) {
        provider.add(typeName(pb, STypeString.class), "Texto");
        provider.add(typeName(pb, STypeData.class),"Data");
        provider.add(typeName(pb, STypeCPF.class),"CPF");
    }

    private String typeName(PackageBuilder pb, Class<? extends SType> typeClass) {
        return pb.getDicionario().getType(typeClass).getName();
    }
}
