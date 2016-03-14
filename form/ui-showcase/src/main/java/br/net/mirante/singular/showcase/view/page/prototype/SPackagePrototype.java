package br.net.mirante.singular.showcase.view.page.prototype;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Optional;
import java.util.function.Predicate;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SInstance;
import br.net.mirante.singular.form.mform.SPackage;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.STypeList;
import br.net.mirante.singular.form.mform.basic.view.SViewListByMasterDetail;
import br.net.mirante.singular.form.mform.core.AtrCore;
import br.net.mirante.singular.form.mform.core.STypeBoolean;
import br.net.mirante.singular.form.mform.core.STypeDate;
import br.net.mirante.singular.form.mform.core.STypeDateTime;
import br.net.mirante.singular.form.mform.core.STypeDecimal;
import br.net.mirante.singular.form.mform.core.STypeFormula;
import br.net.mirante.singular.form.mform.core.STypeInteger;
import br.net.mirante.singular.form.mform.core.STypeLatitudeLongitude;
import br.net.mirante.singular.form.mform.core.STypeMonetary;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.core.attachment.STypeAttachment;
import br.net.mirante.singular.form.mform.options.SFixedOptionsSimpleProvider;
import br.net.mirante.singular.form.mform.util.comuns.STypeYearMonth;
import br.net.mirante.singular.form.mform.util.brasil.STypeCEP;
import br.net.mirante.singular.form.mform.util.brasil.STypeCNPJ;
import br.net.mirante.singular.form.mform.util.brasil.STypeCPF;
import br.net.mirante.singular.form.mform.util.brasil.STypeTelefoneNacional;
import br.net.mirante.singular.form.mform.util.comuns.STypeEMail;
import br.net.mirante.singular.form.mform.util.comuns.STypePersonName;

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

    private STypeInteger tamanhoCampo;
    private STypeBoolean obrigatorio;

    public SPackagePrototype() {
        super(PACOTE);
    }

    @Override
    protected void carregarDefinicoes(PackageBuilder pb) {
        final STypeComposite<?> meta = pb.createCompositeType(META_FORM);
        meta.addFieldString(NAME_FIELD).asAtrBasic().label("Nome")
                .as(AtrCore::new).obrigatorio();

        STypeList<STypeComposite<SIComposite>, SIComposite> childFields =
                meta.addFieldListOfComposite(CHILDREN, "field");

        childFields.asAtrBasic().label("Campos");

        STypeComposite<SIComposite> fieldType = childFields.getElementsType();

        STypeString nome = fieldType.addFieldString(NAME);
        nome.asAtrBasic().label("Nome")
                .as(AtrCore::new).obrigatorio()
.getTipo().asAtrBootstrap().colPreference(3);

        STypeString type = fieldType.addFieldString(TYPE);
        type.asAtrBasic().label("Tipo")
                .getTipo().asAtrCore().obrigatorio()
                .getTipo().asAtrBootstrap().colPreference(2);
        populateOptions(pb, type.withSelection());

        fieldType.addFieldBoolean(IS_LIST)
                .withRadioView()
                .withDefaultValueIfNull(false)
                .asAtrBasic().label("Múltiplo").getTipo().asAtrBootstrap().colPreference(2);

        addAttributeFields(pb, fieldType, type);

        childFields.withView(new SViewListByMasterDetail()
                .col(nome)
                .col(type)
                .col(tamanhoCampo)
                .col(obrigatorio)
        );

        addFields(pb, fieldType, type);

    }

    private void populateOptions(PackageBuilder pb, SFixedOptionsSimpleProvider provider) {
        provider.add(typeName(pb, STypeAttachment.class), "Anexo");
        provider.add(typeName(pb, STypeYearMonth.class), "Ano/Mês");
        provider.add(typeName(pb, STypeBoolean.class), "Booleano");
        provider.add(typeName(pb, STypeComposite.class), "Composto");
        provider.add(typeName(pb, STypeCEP.class), "CEP");
        provider.add(typeName(pb, STypeCPF.class), "CPF");
        provider.add(typeName(pb, STypeCNPJ.class), "CNPJ");
        provider.add(typeName(pb, STypeDate.class), "Data");
        provider.add(typeName(pb, STypeDateTime.class), "Data/Hora");
        provider.add(typeName(pb, STypeEMail.class), "Email");
        provider.add(typeName(pb, STypeLatitudeLongitude.class), "Latitude/Longitude");
        provider.add(typeName(pb, STypeMonetary.class), "Monetário");
        provider.add(typeName(pb, STypePersonName.class), "Nome Pessoa");
        provider.add(typeName(pb, STypeInteger.class), "Número");
        provider.add(typeName(pb, STypeDecimal.class), "Número Decimal");
        provider.add(typeName(pb, STypeString.class), "Texto");
        provider.add(typeName(pb, STypeTelefoneNacional.class), "Telefone Nacional");
    }

    private String typeName(PackageBuilder pb, Class<? extends SType> typeClass) {
        return pb.getDictionary().getType(typeClass).getName();
    }

    private void addAttributeFields(PackageBuilder pb, STypeComposite<SIComposite> fieldType, STypeString type) {
        tamanhoCampo = fieldType.addFieldInteger(TAMANHO_CAMPO);
        tamanhoCampo.asAtrBasic().label("Colunas").tamanhoMaximo(12)
                .getTipo().asAtrBootstrap().colPreference(2);

        obrigatorio = fieldType.addFieldBoolean(OBRIGATORIO);
        obrigatorio.withRadioView().asAtrBasic().label("Obrigatório").getTipo().asAtrBootstrap().colPreference(2);

        fieldType.addFieldInteger(TAMANHO_MAXIMO)
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

        fieldType.addFieldInteger(TAMANHO_INTEIRO_MAXIMO)
                .asAtrBootstrap().colPreference(2)
                .getTipo().asAtrBasic()
                .label("Tamanho Inteiro")
                .visivel(ifDecimalPredicate);

        fieldType.addFieldInteger(TAMANHO_DECIMAL_MAXIMO)
                .asAtrBootstrap().colPreference(2)
                .getTipo().asAtrBasic()
                .label("Tamanho Decimal")
                .visivel(ifDecimalPredicate);
    }

    private void addFields(PackageBuilder pb, STypeComposite<SIComposite> fieldType, STypeString type) {
        STypeList<STypeComposite<SIComposite>, SIComposite> fields =
                fieldType.addFieldListOf(FIELDS, fieldType);
        fields.asAtrBasic().label("Campos")
                .getTipo().withView(SViewListByMasterDetail::new)
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
