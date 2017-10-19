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

package org.opensingular.form.type.core.annotation;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.opensingular.form.PackageBuilder;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.SingularFormException;
import org.opensingular.form.type.core.STypeString;

public class STypeAnnotationClassifierTest {
    protected static SDictionary dictionary;
    protected        PackageBuilder                        localPackage;
    private          STypeComposite<? extends SIComposite> baseCompositeField, annotated1, annotated2,
            notAnnotated, annotated4;
    private STypeString field11;


    public static enum AnnotationType implements AnnotationClassifier {
        ANALISE_TECNICA,
        ANALISE_GERENCIAL,
    }

    @Before
    public void createDictionary() {
        dictionary = SDictionary.create();

        localPackage = dictionary.createNewPackage("test");
        baseCompositeField = localPackage.createCompositeType("group");
        baseCompositeField.addFieldString("notAnnotated");

        annotated1 = baseCompositeField.addFieldComposite("annotatedGroup1");
        field11 = annotated1.addFieldString("field11");
        annotated1.asAtrAnnotation().setAnnotated(AnnotationType.ANALISE_TECNICA);

        annotated2 = baseCompositeField.addFieldComposite("annotatedGroup2");
        annotated2.addFieldString("field121");
        annotated2.addFieldString("field122");
        annotated2.asAtrAnnotation().setAnnotated(AnnotationType.ANALISE_GERENCIAL);

        notAnnotated = baseCompositeField.addFieldComposite("notAnnotatedGroup3");
        notAnnotated.addFieldString("field13");
        annotated4 = notAnnotated.addFieldComposite("annotatedSubGroup4");
        annotated4.addFieldString("field341");
        annotated4.asAtrAnnotation().setAnnotated(AnnotationType.ANALISE_TECNICA);

    }

    @Test
    public void testClassifiedAnnotations(){
        SIComposite instance = baseCompositeField.newInstance();
        asAnnotation(instance, annotated1).annotation(AnnotationType.ANALISE_TECNICA).setText("anything");
        asAnnotation(instance, annotated1).annotation(AnnotationType.ANALISE_TECNICA).setApproved(true);

        SIAnnotation annotation = asAnnotation(instance, annotated1).annotation(AnnotationType.ANALISE_TECNICA);
        Assert.assertEquals("anything", annotation.getText());
        Assert.assertEquals(true, annotation.getApproved());
        Assert.assertEquals(AnnotationType.ANALISE_TECNICA.name(), annotation.getClassifier());

    }



    @Test(expected = SingularFormException.class)
    public void testInvalidClassifier(){
        SIComposite instance = baseCompositeField.newInstance();
        asAnnotation(instance, annotated1).annotation(AnnotationType.ANALISE_GERENCIAL).setText("anything");
    }

    private AtrAnnotation asAnnotation(SIComposite instance, STypeComposite<? extends SIComposite> field) {
        return instance.getDescendant(field).asAtrAnnotation();
    }

}
