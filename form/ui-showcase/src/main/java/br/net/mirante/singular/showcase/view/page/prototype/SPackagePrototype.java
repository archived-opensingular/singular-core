package br.net.mirante.singular.showcase.view.page.prototype;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeData;
import br.net.mirante.singular.form.mform.core.STypeDataHora;
import br.net.mirante.singular.form.mform.core.STypeDecimal;
import br.net.mirante.singular.form.mform.core.STypeFormula;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeLatitudeLongitude;
import br.net.mirante.singular.form.mform.core.STypeMonetario;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.options.MFixedOptionsSimpleProvider;
import br.net.mirante.singular.form.mform.util.comuns.STypeAnoMes;
import br.net.mirante.singular.form.mform.util.comuns.STypeCEP;
import br.net.mirante.singular.form.mform.util.comuns.STypeCNPJ;
import br.net.mirante.singular.form.mform.util.comuns.STypeCPF;
import br.net.mirante.singular.form.mform.util.comuns.STypeEMail;
import br.net.mirante.singular.form.mform.util.comuns.STypeNomePessoa;
import br.net.mirante.singular.form.mform.util.comuns.STypeTelefoneNacional;

import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by nuk on 07/03/16.
 */
public class SPackagePrototype  extends SPackage {

    public static final String  PACOTE = "mform.prototype",
                                META_FORM = "MetaForm",
                                META_FORM_COMPLETE = PACOTE + "." + META_FORM,
                                CHILDREN = "children",
                                NAME = "name",
                                TYPE = "type",
                                IS_LIST = "isList",
                                FIELDS = "fields";
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

        fieldType.addCampoString(NAME)
                .asAtrBasic().label("Nome")
                .getTipo().asAtrBootstrap().colPreference(2);
        ;
        STypeString type = fieldType.addCampoString(TYPE);
        type.asAtrBasic().label("Tipo")
                .getTipo().asAtrBootstrap().colPreference(2);
        populateOptions(pb, type.withSelection());

        fieldType.addCampoBoolean(IS_LIST).asAtrBasic().label("Múltiplo");

        STypeLista<STypeComposite<SIComposite>, SIComposite> fields =
                fieldType.addCampoListaOf(FIELDS, fieldType);
        fields.asAtrBasic().label("Campos");
        fields.withExists(
                (instance) -> {
                    Optional<String> optType = instance.findNearestValue(type, String.class);
                    if(!optType.isPresent()) return false;
                    return optType.get().equals(typeName(pb,STypeComposite.class));
                } )
                .asAtrBasic().dependsOn(() -> {
                    return newArrayList(type);
                })
        ;

    }

    private void populateOptions(PackageBuilder pb, MFixedOptionsSimpleProvider provider) {
        provider.add(typeName(pb, STypeAnoMes.class), "Ano/Mês");
        provider.add(typeName(pb, STypeBoolean.class), "Booleano");
        provider.add(typeName(pb, STypeComposite.class), "Composto");
        provider.add(typeName(pb, STypeCEP.class), "CEP");
        provider.add(typeName(pb, STypeCPF.class), "CPF");
        provider.add(typeName(pb, STypeCNPJ.class), "CNPJ");
        provider.add(typeName(pb, STypeData.class), "Data");
        provider.add(typeName(pb, STypeDataHora.class), "Data/Hora");
        provider.add(typeName(pb, STypeEMail.class), "Email");
        provider.add(typeName(pb, STypeFormula.class), "Formula");
        provider.add(typeName(pb, STypeLatitudeLongitude.class), "Latitude/Longitude");
        provider.add(typeName(pb, STypeMonetario.class), "Monetário");
        provider.add(typeName(pb, STypeNomePessoa.class), "Nome Pessoa");
        provider.add(typeName(pb, STypeInteger.class), "Número");
        provider.add(typeName(pb, STypeDecimal.class), "Número Decimal");
        provider.add(typeName(pb, STypeString.class), "Texto");
        provider.add(typeName(pb, STypeTelefoneNacional.class), "Telefone Nacional");
    }

    private String typeName(PackageBuilder pb, Class<? extends SType> typeClass) {
        return pb.getDicionario().getType(typeClass).getName();
    }
}
