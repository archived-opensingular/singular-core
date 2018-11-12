package org.opensingular.form;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.STypePolymorphicTest.PackageBase.OrderItem;
import org.opensingular.form.STypePolymorphicTest.PackageBase.OrderItemSpecial;
import org.opensingular.form.STypePolymorphicTest.PackageBase.SInstanceA;
import org.opensingular.form.STypePolymorphicTest.PackageBase.SInstanceB;
import org.opensingular.form.STypePolymorphicTest.PackageBase.SInstanceC;
import org.opensingular.form.STypePolymorphicTest.PackageBase.STypeA;
import org.opensingular.form.STypePolymorphicTest.PackageBase.STypeB;
import org.opensingular.form.STypePolymorphicTest.PackageBase.STypeC;
import org.opensingular.form.STypePolymorphicTest.PackagePolymorphic1.OrderType;
import org.opensingular.form.STypePolymorphicTest.PackagePolymorphic2.BlockType;
import org.opensingular.form.type.core.STypeInteger;
import org.opensingular.form.type.core.STypeString;
import org.opensingular.form.type.core.attachment.STypeAttachment;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Daniel C. Bordin
 * @since 2018-08-10
 */
@RunWith(Parameterized.class)
public class STypePolymorphicTest extends TestCaseForm {

    public STypePolymorphicTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    //Testar serialização
    //Testar restringir lista

    @Test
    public void createPolymorphicListWithTypeDefinedByClass() {
        SDictionary dictionary = createTestDictionary();

        SIComposite order = dictionary.newInstance(OrderType.class);
        OrderType orderType = (OrderType) order.getType();
        SIList<SIComposite> items = order.getField(orderType.items);
        OrderItem itemType = orderType.items.getElementsType();
        OrderItemSpecial specialItemType = dictionary.getType(OrderItemSpecial.class);

        SIComposite item = items.addNew();
        item.getField(itemType.cod).setValue(103);
        item.getField(itemType.description).setValue("Pen");
        item.getField(itemType.qtd).setValue(30);

        SIComposite itemSpecial = items.addNew(OrderItemSpecial.class);
        itemSpecial.getField(specialItemType.cod).setValue(104);
        itemSpecial.getField(specialItemType.description).setValue("Pencil");
        itemSpecial.getField(specialItemType.qtd).setValue(10);
        itemSpecial.getField(specialItemType.observations).setValue("B2 model");

        SIComposite itemSpecial2 = items.addNew(OrderItemSpecial.class);
        itemSpecial2.getField(specialItemType.cod).setValue(105);
        itemSpecial2.getField(specialItemType.description).setValue("Eraser");
        itemSpecial2.getField(specialItemType.qtd).setValue(5);
        itemSpecial2.getField(specialItemType.observations).setValue("blue");

        assertType(itemSpecial.getType()).isSameAs(itemSpecial2.getType()).isDirectExtensionOf(OrderItemSpecial.class)
                .isDirectComplementaryExtensionOf(item.getType());

        assertInstance(order).assertCorrectStructure();

        Assertions.assertThatThrownBy(() -> items.addNew(STypeAttachment.class)).isExactlyInstanceOf(
                SingularFormException.class).hasMessageContaining("java class isn't a derived class of");
    }


    @Test
    public void createPolymorphicListWithTypeDefinedByClassAnsSpecificClassForInstances() {
        SDictionary dictionary = createTestDictionary();

        SIComposite block = dictionary.newInstance(BlockType.class);
        BlockType blockType = (BlockType) block.getType();
        SIList<SInstanceA> items = block.getField(blockType.items);
        STypeA<SInstanceA> itemType = blockType.items.getElementsType();
        STypeB typeB0 = dictionary.getType(STypeB.class);
        STypeC typeC0 = dictionary.getType(STypeC.class);

        SInstanceA item = items.addNew();
        item.getField(itemType.fieldA).setValue(103);

        SInstanceB itemB = items.addNew(STypeB.class);
        itemB.getField(typeB0.fieldA).setValue(104);
        itemB.getField(typeB0.fieldB).setValue("XX");

        SInstanceC itemC = items.addNew(STypeC.class);
        itemC.getField(typeC0.fieldA).setValue(105);
        itemC.getField(typeC0.fieldC).setValue("YY");

        SInstanceC itemC2 = items.addNew(STypeC.class);
        itemC.getField(typeC0.fieldA).setValue(106);
        itemC.getField(typeC0.fieldC).setValue("ZZ");

        assertType(itemC.getType()).isSameAs(itemC2.getType()).isDirectExtensionOf(STypeC.class)
                .isDirectComplementaryExtensionOf(item.getType());

        assertThat(items.getValues()).hasSize(4);
        assertInstance(items.get(0)).isTypeOf(itemType).isExactlyInstanceOf(SInstanceA.class);
        assertInstance(items.get(1)).isTypeOf(STypeB.class).isExactlyInstanceOf(SInstanceB.class);
        assertInstance(items.get(2)).isTypeOf(STypeC.class).isExactlyInstanceOf(SInstanceC.class);
        assertInstance(items.get(3)).isTypeOf(STypeC.class).isExactlyInstanceOf(SInstanceC.class);
        assertInstance(block).assertCorrectStructure();
    }

    @SInfoPackage
    static class PackagePolymorphic1 extends SPackage {

        @SInfoType(spackage = PackagePolymorphic1.class)
        public static class OrderType extends STypeComposite<SIComposite> {

            public STypeList<OrderItem, SIComposite> items;

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                items = addFieldListOf("items", OrderItem.class);
            }
        }
    }

    @SInfoPackage
    static class PackageBase extends SPackage {

        @SInfoType(spackage = PackageBase.class)
        public static class OrderItem extends STypeComposite<SIComposite> {
            public STypeInteger cod;
            public STypeString description;
            public STypeInteger qtd;

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                cod = addFieldInteger("cod");
                description = addFieldString("description");
                qtd = addFieldInteger("qtd");
            }
        }

        @SInfoType(spackage = PackageBase.class)
        public static class OrderItemSpecial extends OrderItem {
            public STypeString observations;

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                observations = addFieldString("observations");
            }
        }

        @SInfoType(spackage = PackageBase.class)
        public static class STypeA<INS extends SInstanceA> extends STypeComposite<INS> {
            public STypeInteger fieldA;

            @SuppressWarnings("unchecked")
            public STypeA() {
                super((Class<INS>) SInstanceA.class);
            }

            public STypeA(Class<INS> instanceClass) {
                super(instanceClass);
            }

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                fieldA = addFieldInteger("fieldA");
            }
        }

        static class SInstanceA extends SIComposite {
        }

        @SInfoType(spackage = PackageBase.class)
        public static class STypeB extends STypeA<SInstanceB> {
            public STypeString fieldB;

            public STypeB() {
                super(SInstanceB.class);
            }

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                fieldB = addFieldString("fieldB");
            }
        }

        static class SInstanceB extends SInstanceA {
        }

        @SInfoType(spackage = PackageBase.class)
        public static class STypeC extends STypeA<SInstanceC> {
            public STypeString fieldC;

            public STypeC() {
                super(SInstanceC.class);
            }

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                fieldC = addFieldString("fieldC");
            }
        }

        static class SInstanceC extends SInstanceB {
        }
    }


    @SInfoPackage
    static class PackagePolymorphic2 extends SPackage {

        @SInfoType(spackage = PackagePolymorphic2.class)
        public static class BlockType extends STypeComposite<SIComposite> {

            public STypeList<STypeA<SInstanceA>, SInstanceA> items;

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                //noinspection unchecked
                items = addFieldListOf("items", (Class<STypeA<SInstanceA>>) (Object) STypeA.class);
                //items.addSupportedElementSubTypes(STypeB.class, STypeC.class);
            }
        }
    }
}
