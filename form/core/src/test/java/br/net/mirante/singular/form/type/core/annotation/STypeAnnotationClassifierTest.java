package br.net.mirante.singular.form.type.core.annotation;

import br.net.mirante.singular.form.PackageBuilder;
import br.net.mirante.singular.form.SDictionary;
import br.net.mirante.singular.form.SIComposite;
import br.net.mirante.singular.form.SIList;
import br.net.mirante.singular.form.SType;
import br.net.mirante.singular.form.STypeComposite;
import br.net.mirante.singular.form.SingularFormException;
import br.net.mirante.singular.form.document.RefType;
import br.net.mirante.singular.form.document.SDocumentFactory;
import br.net.mirante.singular.form.io.FormSerializationUtil;
import br.net.mirante.singular.form.io.FormSerialized;
import br.net.mirante.singular.form.type.core.STypeString;
import org.fest.assertions.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.groups.Properties.extractProperty;

public class STypeAnnotationClassifierTest {
    protected static SDictionary                           dicionario;
    protected        PackageBuilder                        localPackage;
    private          STypeComposite<? extends SIComposite> baseCompositeField, annotated1, annotated2,
            notAnnotated, annotated4;
    private STypeString field11;


    public static enum TipoAnotacao implements AnnotationClassifier {
        ANALISE_TECNICA,
        ANALISE_GERENCIAL,
    }

    @Before
    public void createDicionario() {
        dicionario = SDictionary.create();

        localPackage = dicionario.createNewPackage("test");
        baseCompositeField = localPackage.createCompositeType("group");
        baseCompositeField.addFieldString("notAnnotated");

        annotated1 = baseCompositeField.addFieldComposite("annotatedGroup1");
        field11 = annotated1.addFieldString("field11");
        annotated1.asAtrAnnotation().setAnnotated(TipoAnotacao.ANALISE_TECNICA);

        annotated2 = baseCompositeField.addFieldComposite("annotatedGroup2");
        annotated2.addFieldString("field121");
        annotated2.addFieldString("field122");
        annotated2.asAtrAnnotation().setAnnotated(TipoAnotacao.ANALISE_GERENCIAL);

        notAnnotated = baseCompositeField.addFieldComposite("notAnnotatedGroup3");
        notAnnotated.addFieldString("field13");
        annotated4 = notAnnotated.addFieldComposite("annotatedSubGroup4");
        annotated4.addFieldString("field341");
        annotated4.asAtrAnnotation().setAnnotated(TipoAnotacao.ANALISE_TECNICA);

    }

    @Test
    public void testClassifiedAnnotations(){
        SIComposite instance = baseCompositeField.newInstance();
        asAnnotation(instance, annotated1).annotation(TipoAnotacao.ANALISE_TECNICA).setText("anything");
        asAnnotation(instance, annotated1).annotation(TipoAnotacao.ANALISE_TECNICA).setApproved(true);

        SIAnnotation annotation = asAnnotation(instance, annotated1).annotation(TipoAnotacao.ANALISE_TECNICA);
        Assert.assertEquals("anything", annotation.getText());
        Assert.assertEquals(true, annotation.getApproved());
        Assert.assertEquals(TipoAnotacao.ANALISE_TECNICA.name(), annotation.getClassifier());

    }



    @Test(expected = SingularFormException.class)
    public void testInvalidClassifier(){
        SIComposite instance = baseCompositeField.newInstance();
        asAnnotation(instance, annotated1).annotation(TipoAnotacao.ANALISE_GERENCIAL).setText("anything");
    }

    private AtrAnnotation asAnnotation(SIComposite instance, STypeComposite<? extends SIComposite> field) {
        return instance.getDescendant(field).asAtrAnnotation();
    }

}
