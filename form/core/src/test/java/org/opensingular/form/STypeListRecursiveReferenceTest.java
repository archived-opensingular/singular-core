/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.form;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.opensingular.form.STypeListRecursiveReferenceTest.TestPackageWithCircularReference.TypeTestPark;
import org.opensingular.form.STypeListRecursiveReferenceTest.TestPackageWithCircularReference.TypeTestTree;
import org.opensingular.form.STypeListRecursiveReferenceTest.TestPackageWithCircularReference.TypeTestTreeB;
import org.opensingular.form.helpers.AssertionsSInstance;
import org.opensingular.form.helpers.AssertionsSType;
import org.opensingular.form.type.core.STypeString;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(Parameterized.class)
public class STypeListRecursiveReferenceTest extends TestCaseForm {

    public STypeListRecursiveReferenceTest(TestFormConfig testFormConfig) {
        super(testFormConfig);
    }

    @Test
    public void realTestCircularReferenceWithListAndComposite() {
        TypeTestTree tTree = createTestDictionary().getType(TypeTestTree.class);
        AssertionsSType aTree = assertType(tTree);
        aTree.isAttrLabel("Tree").isComposite(2);
        aTree.isString("name").isNotRecursiveReference();
        aTree.isList("children").isNotRecursiveReference();
        aTree.isNotRecursiveReference();
        aTree.listElementType("children").isExtensionCorrect(tTree).isRecursiveReference();
        aTree.listElementType("children").isAttrLabel("SubTree").isComposite(2);
        assertType(tTree.name).isNotNull().isSameAs(tTree.getField("name"));
        assertType(tTree.children).isNotNull().isSameAs(tTree.getField("children"));
        assertType(aTree.listElementType("children").getTarget(TypeTestTree.class).name).isSameAs(tTree.name);
        assertType(aTree.listElementType("children").getTarget(TypeTestTree.class).children).isSameAs(tTree.children);

        testTreeRecursiveInstance(tTree, tTree.name, tTree.children, "Tree", "SubTree");

        TypeTestPark tPark = tTree.getDictionary().getType(TypeTestPark.class);

        testCompositeWithTreeFields(tTree, tTree.name, tTree.children, tPark);

        AssertionsSType aPark = assertType(tPark);
        aPark.field("tree").isSameAs(tPark.tree);
        aPark.field("trees").listElementType().isNotSameAs(tPark.tree).isSameAs(tPark.trees.getElementsType());
        aPark.field("trees").listElementType().field("name").isSameAs(tPark.trees.getElementsType().name);
        aPark.field("trees").listElementType().field("children").isSameAs(tPark.trees.getElementsType().children);
        aPark.field("trees").listElementType().field("children").listElementType().isSameAs(
                tPark.trees.getElementsType().children.getElementsType());
        aPark.field("trees").listElementType().field("children").listElementType().isDirectExtensionOf(
                tTree.children.getElementsType());
    }

    @SInfoPackage(name = "circular")
    public static final class TestPackageWithCircularReference extends SPackage {

        @Override
        protected void onLoadPackage(@Nonnull PackageBuilder pb) {
            pb.createType(TypeTestTree.class);
            pb.createType(TypeTestPark.class);
        }

        @SInfoType(name = "tree", spackage = TestPackageWithCircularReference.class)
        public static class TypeTestTree extends STypeComposite<SIComposite> {

            public STypeString name;
            public STypeList<TypeTestTree, SIComposite> children;

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                name = addFieldString("name");
                children = addFieldListOf("children", TypeTestTree.class);
                asAtr().label("Tree");
                children.getElementsType().asAtr().label("SubTree");
            }
        }

        @SInfoType(name = "park", spackage = TestPackageWithCircularReference.class)
        public static class TypeTestPark extends STypeComposite<SIComposite> {
            public STypeString name;
            public TypeTestTree tree;
            public STypeList<TypeTestTree, SIComposite> trees;

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                name = addFieldString("name");
                tree = addField("tree", TypeTestTree.class);
                trees = addFieldListOf("trees", TypeTestTree.class);
            }
        }

        @SInfoType(name = "treeB", spackage = TestPackageWithCircularReference.class)
        public static final class TypeTestTreeB extends TypeTestTree {
            public STypeString location;

            @Override
            protected void onLoadType(@Nonnull TypeBuilder tb) {
                location = addFieldString("location");
            }
        }
    }

    @Test
    public void testCircularReferenceWithOutTypeDefinedByClass() {
        PackageBuilder pb = createTestPackage();
        STypeComposite<SIComposite> tTree = pb.createCompositeType("tree");
        STypeString tTreeName = tTree.addFieldString("name");
        STypeList<STypeComposite<SIComposite>, SIComposite> tTreeChildrens = tTree.addFieldListOf("children", tTree);
        STypeComposite<SIComposite> tSubTree = tTreeChildrens.getElementsType();
        tTree.asAtr().label("Tree");
        tSubTree.asAtr().label("SubTree");

        AssertionsSType aTree = assertType(tTree);
        aTree.isAttrLabel("Tree").isComposite(2);
        aTree.isString("name").isNotRecursiveReference();
        aTree.isList("children").isNotRecursiveReference();
        aTree.isNotRecursiveReference();
        aTree.listElementType("children").isExtensionCorrect(tTree).isRecursiveReference();
        aTree.listElementType("children").isAttrLabel("SubTree").isComposite(2);
        aTree.listElementType("children").field("name").isSameAs(tTreeName);
        aTree.listElementType("children").listElementType("children").isSameAs(tSubTree);

        testTreeRecursiveInstance(tTree, tTreeName, tTreeChildrens, "Tree", "SubTree");

        STypeComposite<SIComposite> tPark = pb.createCompositeType("park");
        STypeString tParkName = tPark.addFieldString("name");
        STypeComposite<SIComposite> tParkTree = tPark.addField("tree", tTree);
        STypeList<STypeComposite<SIComposite>, SIComposite> tParkTrees = tPark.addFieldListOf("trees", tTree);

        testCompositeWithTreeFields(tTree, tTreeName, tTreeChildrens, tPark);
    }

    private void testCompositeWithTreeFields(STypeComposite<SIComposite> tTree, SType<?> tTreeName,
            SType<?> tTreeChildren2, @Nonnull STypeComposite<SIComposite> tPark) {
        //noinspection unchecked
        STypeList<STypeComposite<SIComposite>, SIComposite> tTreeChildren =
                (STypeList<STypeComposite<SIComposite>, SIComposite>) tTreeChildren2;

        tPark.getField("tree").asAtr().label("parkTree");
        tPark.getLocalType("tree.children").asAtr().label("parkSubTree");
        tPark.getLocalType("trees.tree").asAtr().label("parkTree2");
        tPark.getLocalType("trees.tree.children").asAtr().label("parkSubTree2");

        AssertionsSType aPark = assertType(tPark);
        aPark.isComposite(3);
        aPark.getTarget().getDictionary();
        //aPark.field("tree").isExtensionCorrect(tTree);
        aPark.field("trees").listElementType().isDirectExtensionOf(tTree).isNotSameAs(tPark.getField("tree"));
        aPark.field("trees").listElementType().field("name").isDirectExtensionOf(tTreeName);
        aPark.field("trees").listElementType().field("children").isDirectExtensionOf(tTreeChildren);
        aPark.field("trees").listElementType().field("children").listElementType().isDirectExtensionOf(
                tTreeChildren.getElementsType());

        tPark.newInstance();
        //SIComposite iPark = tPark.newInstance();
        //        testTreeRecursiveInstance((STypeComposite<SIComposite>) tPark.getField("tree"), tPark.getLocalType
        // ("tree.name"),
        //                tPark.getLocalType("tree.children"), "", "");

    }

    @SuppressWarnings("SameParameterValue")
    private void testTreeRecursiveInstance(@Nonnull STypeComposite<SIComposite> tTree, @Nonnull SType<?> tName,
            @Nonnull SType<?> tChildren2, String expectedFirstLevelLabel, String expectedSubLevelLabel) {
        //noinspection unchecked
        STypeList<STypeComposite<SIComposite>, SIComposite> tChildren =
                (STypeList<STypeComposite<SIComposite>, SIComposite>) tChildren2;

        SIComposite iTree = tTree.newInstance();
        iTree.setValue(tName, "a");
        SIComposite f0 = iTree.getField(tChildren).addNew();
        f0.setValue(tName, "b");
        SIComposite f1 = iTree.getField(tChildren).addNew();
        f1.setValue(tName, "c");
        SIComposite f10 = f1.getField(tChildren).addNew();
        f10.setValue(tName, "d");

        AssertionsSInstance aITree = assertInstance(iTree);
        aITree.isAttrLabel(expectedFirstLevelLabel);
        aITree.field("children[0]").isComposite().isAttrLabel(expectedSubLevelLabel);
        aITree.field("children[1].children[0]").isComposite().isAttrLabel(expectedSubLevelLabel);
        aITree.isValueEquals("name", "a");
        aITree.isValueEquals("children[0].name", "b");
        aITree.isValueEquals("children[1].name", "c");
        aITree.isValueEquals("children[1].children[0].name", "d");
        aITree.isList("children", 2);
        aITree.isList("children[0].children", 0);
        aITree.isList("children[1].children", 1);
        aITree.isList("children[1].children[0].children", 0);
    }

    @Test
    public void testCircularReferenceWithTwoLayerDerivedClasses() {
        SDictionary dictionary = createTestDictionary();

        // B0 -> A0
        TypeTestTree typeA0 = dictionary.getType(TypeTestTree.class);
        TypeTestTreeB typeB0 = dictionary.getType(TypeTestTreeB.class);

        assertType(typeA0.children.getElementsType()).isDirectExtensionOf(typeA0).isRecursiveReference();
        assertType(typeB0.children.getElementsType()).isDirectExtensionOf(typeA0.children.getElementsType())
                .isRecursiveReference();
        assertType(typeB0).isExtensionOf(typeA0).isExtensionCorrect(typeA0);

        SIComposite b0 = typeB0.newInstance();
        b0.setValue(typeB0.name, "xx");
        b0.setValue(typeB0.location, "yy");

        SIComposite ch1 = b0.getField(typeA0.children).addNew();
        ch1.setValue(typeB0.name, "1");

        SIComposite ch11 = ch1.getField(typeA0.children).addNew();
        ch11.setValue(typeB0.name, "11");

        SIComposite ch2 = b0.getField(typeA0.children).addNew();
        ch2.setValue(typeB0.name, "2");

        assertInstance(b0).field("children").isExactTypeOf(typeB0.children);
        assertInstance(b0).field("children[0]").isExactTypeOf(typeB0.children.getElementsType());
        assertInstance(b0).field("children[0].children").isExactTypeOf(typeA0.children);
        assertInstance(b0).field("children[0].children[0]").isExactTypeOf(typeA0.children.getElementsType());

        assertInstance(b0).assertCorrectStructure();
    }

    @Test
    public void cantAddNewFieldToACircularReference() {
        SDictionary dictionary = createTestDictionary();
        TypeTestTree typeA0 = dictionary.getType(TypeTestTree.class);
        TypeTestTreeB typeB0 = dictionary.getType(TypeTestTreeB.class);

        TypeTestTree refA0 = typeA0.children.getElementsType();
        TypeTestTree refB0 = typeB0.children.getElementsType();
        assertType(refA0).isRecursiveReference();
        assertType(refB0).isRecursiveReference();

        assertThatThrownBy(() -> refA0.addFieldString("f1")).isExactlyInstanceOf(SingularFormException.class)
                .hasMessageContaining("because it's a recursive reference");
        assertThatThrownBy(() -> refB0.addFieldString("f2")).isExactlyInstanceOf(SingularFormException.class)
                .hasMessageContaining("because it's a recursive reference");
    }
}
