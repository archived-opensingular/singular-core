package br.net.mirante.singular.showcase.view.page.prototype;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.MListMasterDetailView;
import br.net.mirante.singular.form.mform.core.AtrCore;
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
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by nuk on 07/03/16.
 */
public class SPackagePrototype extends SPackage {

    public static final String PACOTE = "mform.prototype",
            META_FORM = "MetaForm",
            META_FORM_COMPLETE = PACOTE + "." + META_FORM,
            CHILDREN = "children",
            NAME = "name",
            TYPE = "type",
            IS_LIST = "isList",
            TAMANHO_CAMPO = "tamanhoCampo",
            OBRIGATORIO = "obrigatorio",
            TAMANHO_MAXIMO = "tamanhoMaximo",
            TAMANHO_INTEIRO_MAXIMO = "tamanhoInteiroMaximo",
            TAMANHO_DECIMAL_MAXIMO = "tamanhoDecimalMaximo",
            FIELDS = "fields";
    public static final String NAME_FIELD = "name";


    public SPackagePrototype() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        final STypeComposite<?> meta = pb.createTipoComposto(META_FORM);
        meta.addCampoString(NAME_FIELD).asAtrBasic().label("Nome")
                .as(AtrCore::new).obrigatorio();

        STypeLista<STypeComposite<SIComposite>, SIComposite> childFields =
                meta.addCampoListaOfComposto(CHILDREN, "field");

        childFields.withView(MListMasterDetailView::new).asAtrBasic().label("Campos");

        STypeComposite<SIComposite> fieldType = childFields.getTipoElementos();

        fieldType.addCampoString(NAME)
                .asAtrBasic().label("Nome")
                .as(AtrCore::new).obrigatorio()
                .getTipo().asAtrBootstrap().colPreference(2);
        ;
        STypeString type = fieldType.addCampoString(TYPE);
        type.asAtrBasic().label("Tipo")
                .getTipo().asAtrCore().obrigatorio()
                .getTipo().asAtrBootstrap().colPreference(2);
        populateOptions(pb, type.withSelection());

        fieldType.addCampoBoolean(IS_LIST)
                .asAtrBasic().label("Múltiplo")
                .getTipo().asAtrBootstrap().colPreference(2);

        addAttributeFields(pb, fieldType, type);

        addFields(pb, fieldType, type);

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

    private void addAttributeFields(PackageBuilder pb, STypeComposite<SIComposite> fieldType, STypeString type) {
        fieldType.addCampoInteger(TAMANHO_CAMPO)
                .asAtrBasic().label("Tamanho do Campo").tamanhoMaximo(12)
                .getTipo().asAtrBootstrap().colPreference(3);

        fieldType.addCampoBoolean(OBRIGATORIO)
                .withRadioView()
                .asAtrBasic().label("Obrigatório")
                .getTipo().asAtrBootstrap().colPreference(2);

        fieldType.addCampoInteger(TAMANHO_MAXIMO)
                .asAtrBootstrap().colPreference(2)
                .getTipo().asAtrBasic().label("Tamanho Máximo")
                .visivel(
                        (instance) -> {
                            Optional<String> optType = instance.findNearestValue(type, String.class);
                            if (!optType.isPresent()) return false;
                            return optType.get().equals(typeName(pb, STypeInteger.class));
                        }
                );

        Predicate<SInstance> ifDecimalPredicate = (instance) -> {
            Optional<String> optType = instance.findNearestValue(type, String.class);
            if (!optType.isPresent()) return false;
            return optType.get().equals(typeName(pb, STypeDecimal.class));
        };

        fieldType.addCampoInteger(TAMANHO_INTEIRO_MAXIMO)
                .asAtrBootstrap().colPreference(2)
                .getTipo().asAtrBasic().label("Tamanho Inteiro Máximo")
                .visivel(ifDecimalPredicate);

        fieldType.addCampoInteger(TAMANHO_DECIMAL_MAXIMO)
                .asAtrBootstrap().colPreference(2)
                .getTipo().asAtrBasic().label("Tamanho Decimal Máximo")
                .visivel(ifDecimalPredicate);
    }

    private void addFields(PackageBuilder pb, STypeComposite<SIComposite> fieldType, STypeString type) {
        STypeLista<STypeComposite<SIComposite>, SIComposite> fields =
                fieldType.addCampoListaOf(FIELDS, fieldType);
        fields.asAtrBasic().label("Campos")
                .getTipo().withView(MListMasterDetailView::new)
                .withExists(
                        (instance) -> {
                            Optional<String> optType = instance.findNearestValue(type, String.class);
                            if (!optType.isPresent()) return false;
                            return optType.get().equals(typeName(pb, STypeComposite.class));
                        })
                .asAtrBasic().dependsOn(() -> {
            return newArrayList(type);
        })
        ;
    }
}
