package br.net.mirante.singular.form.io.definition;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.List;

import br.net.mirante.singular.form.*;
import br.net.mirante.singular.form.io.FormAssert;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;

import br.net.mirante.singular.form.io.PersistenceBuilderXML;
import br.net.mirante.singular.form.type.core.STypeString;
import br.net.mirante.singular.form.type.country.brazil.STypeCEP;

public class TestSFormDefinitionPersistenceUtil {

    @Test
    public void testVerySimple() {
        SDictionary novo = SDictionary.create();
        PackageBuilder pkg = novo.createNewPackage("myPkg.teste");
        STypeString type = pkg.createType("descr", STypeString.class);

        SIPersistenceArchive archive = testArchiveAndUnarchive(type);
        assertListSize(archive, "packages", 1);
        assertListSize(archive, "packages[0].types", 1);
        assertEqualsArchive(archive, "packages[0].types[0].name", "descr");
        assertEqualsArchive(archive, "packages[0].types[0].type", "String");

        STypeString type2 = pkg.createType("descr2", type);
        SIPersistenceArchive archive2 = testArchiveAndUnarchive(type2);
        assertListSize(archive2, "packages", 1);
        assertListSize(archive2, "packages[0].types", 2);
        assertEqualsArchive(archive2, "packages[0].types[0].name", "descr2");
        assertEqualsArchive(archive2, "packages[0].types[0].type", "myPkg.teste.descr");
        assertEqualsArchive(archive2, "packages[0].types[1].name", "descr");
        assertEqualsArchive(archive2, "packages[0].types[1].type", "String");
    }

    @Test
    public void testTypeCompositeOneLevel() {
        SDictionary novo = SDictionary.create();
        PackageBuilder pkg = novo.createNewPackage("myPkg.teste");
        STypeComposite<SIComposite> typeEndereco = pkg.createCompositeType("endereco");
        typeEndereco.addFieldString("rua");
        typeEndereco.addFieldInteger("numero");
        typeEndereco.addField("cep", STypeCEP.class);

        SIPersistenceArchive archive = testArchiveAndUnarchive(typeEndereco);
        assertListSize(archive, "packages", 1);
        assertListSize(archive, "packages[0].types", 1);
        assertEqualsArchive(archive, "packages[0].types[0].members[0].name", "rua");
        assertEqualsArchive(archive, "packages[0].types[0].members[2].name", "cep");
        assertEqualsArchive(archive, "packages[0].types[0].members[2].type", SFormUtil.getTypeName(STypeCEP.class));
    }

    @Test
    public void testTypeCompositeTwoLevels() {
        SDictionary novo = SDictionary.create();
        PackageBuilder pkg = novo.createNewPackage("myPkg.teste");
        STypeComposite<SIComposite> typeEndereco = pkg.createCompositeType("endereco");
        typeEndereco.addFieldString("rua");
        typeEndereco.addFieldInteger("numero");
        typeEndereco.addField("cep", STypeCEP.class);
        //Second level
        STypeComposite<SIComposite> typeDetalhes = typeEndereco.addFieldComposite("detalhes");
        typeDetalhes.addFieldString("cor");
        typeDetalhes.addFieldDate("andares");

        SIPersistenceArchive archive = testArchiveAndUnarchive(typeEndereco);
        assertEqualsArchive(archive, "packages[0].types[0].members[3].name", "detalhes");
        assertEqualsArchive(archive, "packages[0].types[0].members[3].members[0].name", "cor");
        assertEqualsArchive(archive, "packages[0].types[0].members[3].members[0].type", "String");
    }

    @Test
    public void testTypeOfCompositeSubMemberInSamePackage() {
        SDictionary novo = SDictionary.create();
        PackageBuilder pkg = novo.createNewPackage("myPkg.teste");
        STypeComposite<SIComposite> typeEndereco = pkg.createCompositeType("endereco");
        STypeString typeRua = typeEndereco.addFieldString("rua");
        typeEndereco.addFieldInteger("numero");
        typeEndereco.addField("cep", STypeCEP.class);
        //Second level
        STypeComposite<SIComposite> typeDetalhes = typeEndereco.addFieldComposite("detalhes");
        typeDetalhes.addFieldString("cor");
        typeDetalhes.addFieldDate("andares");

        STypeString typeOfSubType = pkg.createType("minhaRua", typeRua);

        SIPersistenceArchive archive = testArchiveAndUnarchive(typeOfSubType);
        assertListSize(archive, "packages", 1);
        assertListSize(archive, "packages[0].types", 2);
        assertEqualsArchive(archive, "packages[0].types[0].name", "minhaRua");
        assertEqualsArchive(archive, "packages[0].types[0].type", "myPkg.teste.endereco.rua");
        assertEqualsArchive(archive, "packages[0].types[1].members[0].name", "rua");
        assertEqualsArchive(archive, "packages[0].types[1].members[2].name", "cep");
    }

    @Test
    public void testTypeOfCompositeSubMemberInDifferentPackage() {
        PackageBuilder pkg = SDictionary.create().createNewPackage("myPkg.teste");
        STypeComposite<SIComposite> typeEndereco = pkg.createCompositeType("endereco");
        STypeString typeRua = typeEndereco.addFieldString("rua");
        typeEndereco.addFieldInteger("numero");
        typeEndereco.addField("cep", STypeCEP.class);

        PackageBuilder pkg2 = pkg.getDictionary().createNewPackage("myPkg.teste2");
        STypeString typeOfSubType = pkg2.createType("minhaRua", typeRua);

        SIPersistenceArchive archive = testArchiveAndUnarchive(typeOfSubType);
        assertListSize(archive, "packages", 2);
        assertListSize(archive, "packages[0].types", 1);
        assertEqualsArchive(archive, "packages[0].types[0].name", "minhaRua");
        assertEqualsArchive(archive, "packages[0].types[0].type", "myPkg.teste.endereco.rua");
        assertListSize(archive, "packages[1].types", 1);
        assertEqualsArchive(archive, "packages[1].types[0].members[0].name", "rua");
        assertEqualsArchive(archive, "packages[1].types[0].members[2].name", "cep");
    }

    @Test
    @Ignore
    public void testTypeCompositeWithAttribute() {
        PackageBuilder pkg = SDictionary.create().createNewPackage("myPkg.teste");
        STypeComposite<SIComposite> typeEndereco = pkg.createCompositeType("endereco");
        typeEndereco.asAtr().label("Endereço");
        typeEndereco.addFieldString("rua").asAtr().label("Rua");
        typeEndereco.addFieldInteger("numero").asAtr().label("Número");
        typeEndereco.addField("cep", STypeCEP.class).asAtr().label("CEP").required();

        SIPersistenceArchive archive = testArchiveAndUnarchive(typeEndereco);
        assertListSize(archive, "packages", 1);
        assertListSize(archive, "packages[0].types", 1);
        assertEqualsArchive(archive, "packages[0].types[0].members[0].name", "rua");
        assertEqualsArchive(archive, "packages[0].types[0].members[2].name", "cep");
    }

    private static void assertEqualsArchive(SIPersistenceArchive archive, String path, String expectedValue) {
        assertThat(archive.getValue(path)).isEqualTo(expectedValue);
    }

    private static void assertListSize(SIPersistenceArchive archive, String path, int expectedSize) {
        assertThat(archive.getFieldList(path).size()).isEqualTo(expectedSize);

    }

    private static SIPersistenceArchive testArchiveAndUnarchive(SType<?> type) {
        SIPersistenceArchive archive = SFormDefinitionPersistenceUtil.toArchive(type);
        System.out.println(toPersistenceString(archive));

        SType<?> type2 = SFormDefinitionPersistenceUtil.fromArchive(archive);

        assertEquivalenceRootType(type, type2);

        SIPersistenceArchive archive2 = SFormDefinitionPersistenceUtil.toArchive(type2);
        SType<?> type3 = SFormDefinitionPersistenceUtil.fromArchive(archive2);

        assertEquivalenceRootType(type, type3);
        System.out.println(toPersistenceString(archive2));
        assertThat(toPersistenceString(archive)).isEqualTo(toPersistenceString(archive2));

        return archive;
    }

    private static String toPersistenceString(SIPersistenceArchive archive) {
        return new PersistenceBuilderXML().withPersistId(false).toXML(archive).toString();
    }

    private static void assertEquivalenceRootType(SType<?> original, SType<?> recovered) {
        assertThat(original.getDictionary()).isNotSameAs(recovered.getDictionary());
        assertEquivalence(original, recovered);
    }

    private static void assertEquivalence(SType<?> original, SType<?> recovered) {
        try {
            assertThat(original.getName()).isEqualTo(recovered.getName());
            assertThat(original.getPackage().getName()).isEqualTo(recovered.getPackage().getName());
            assertEquals(original.getClass(), recovered.getClass());
            if (original.getSuperType() != null) {
                assertEquivalence(original.getSuperType(), recovered.getSuperType());
            } else {
                assertThat(recovered.getSuperType()).isNull();
            }
            FormAssert.assertEqualsAttributes(original, recovered);
            List<SType<?>> localTypesOriginal = Lists.newArrayList(original.getLocalTypes());
            List<SType<?>> localTypesRecovered = Lists.newArrayList(recovered.getLocalTypes());
            assertEquals(localTypesOriginal.size(), localTypesRecovered.size());
            if (localTypesOriginal.size() != 0) {
                for (int i = 0; i < localTypesOriginal.size(); i++) {
                    assertEquivalence(localTypesOriginal.get(i), localTypesRecovered.get(i));
                }
            }
            if (original instanceof STypeList) {
                assertEquivalence(((STypeList) original).getElementsType(), ((STypeList) recovered).getElementsType());
            }

        } catch (AssertionError e) {
            throw new AssertionError("Erro no valor recuperado para " + original.getName(),e);
        }
    }
}
