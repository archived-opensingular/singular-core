package br.net.mirante.singular.form.mform.core.annotation;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.groups.Properties.extractProperty;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SIList;
import br.net.mirante.singular.form.mform.SType;
import br.net.mirante.singular.form.mform.STypeComposite;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.mform.io.FormSerializationUtil;
import br.net.mirante.singular.form.mform.io.FormSerialized;

public class STypeAnnotationTest {
    protected static SDictionary dicionario;
    protected PackageBuilder localPackage;
    private STypeComposite<? extends SIComposite> baseCompositeField, annotated1, annotated2,
            notAnnotated, annotated4;
    private STypeString field11;

    @Before
    public void createDicionario() {
        dicionario = SDictionary.create();

        localPackage = dicionario.createNewPackage("test");
        baseCompositeField = localPackage.createCompositeType("group");
        baseCompositeField.addFieldString("notAnnotated");

        annotated1 = baseCompositeField.addFieldComposite("annotatedGroup1");
        field11 = annotated1.addFieldString("field11");
        annotated1.asAtrAnnotation().setAnnotated();

        annotated2 = baseCompositeField.addFieldComposite("annotatedGroup2");
        annotated2.addFieldString("field121");
        annotated2.addFieldString("field122");
        annotated2.asAtrAnnotation().setAnnotated();

        notAnnotated = baseCompositeField.addFieldComposite("notAnnotatedGroup3");
        notAnnotated.addFieldString("field13");
        annotated4 = notAnnotated.addFieldComposite("annotatedSubGroup4");
        annotated4.addFieldString("field341");
        annotated4.asAtrAnnotation().setAnnotated();

    }

    @Test public void aNewInstanceHasNoAnnotations(){
        SIComposite instance = baseCompositeField.newInstance();
        assertThat(instance.asAtrAnnotation().allAnnotations()).isEmpty();
    }

    @Test public void returnAllAnnotationsFromInstance(){
        SIComposite instance = baseCompositeField.newInstance();

        asAnnotation(instance, annotated1).annotation().setText("Abacate");
        asAnnotation(instance, annotated2).annotation().setText("Avocado");
        asAnnotation(instance, annotated4).annotation().setText("ukwatapheya");

        List<SIAnnotation> all = instance.asAtrAnnotation().allAnnotations();
        assertThat(extractProperty("text").from(all)).containsOnly("Abacate","Avocado","ukwatapheya");
    }

    @Test public void returnAllAnnotationsFromInstanceWithoutEmptyOnes(){
        SIComposite instance = baseCompositeField.newInstance();

        asAnnotation(instance, annotated1).annotation().setText("Abacate");
        asAnnotation(instance, annotated2).annotation();
        asAnnotation(instance, annotated4).annotation().setText("ukwatapheya");

        List<SIAnnotation> all = instance.asAtrAnnotation().allAnnotations();
        assertThat(extractProperty("text").from(all))
                .containsOnly("Abacate",null, "ukwatapheya");
    }

    @Test public void returnAllAnnotationsInAPersitentObject(){
        SIComposite instance = baseCompositeField.newInstance();

        asAnnotation(instance, annotated1).annotation().setText("Abacate");
        asAnnotation(instance, annotated2).annotation().setApproved(true);
        asAnnotation(instance, annotated4).annotation().setText("ukwatapheya");

        SIList persistent = instance.asAtrAnnotation().persistentAnnotations();
        assertThat(persistent.getType()).isInstanceOf(STypeAnnotationList.class);
        assertThat(persistent.getElementsType()).isInstanceOf(STypeAnnotation.class);
        assertThat(extractProperty("text").from(persistent.getValues()))
                .containsOnly("Abacate", null, "ukwatapheya");
        assertThat(extractProperty("approved").from(persistent.getValues()))
                .containsOnly(null, true);
    }

    @Test public void loadsAnnotationsToAnewObject(){
        RefType ref = new RefType() {
            @Override
            protected SType<?> retrieve() {
                return baseCompositeField;
            }
        };
        SIComposite instance = (SIComposite) SDocumentFactory.empty().createInstance(ref);

        asAnnotation(instance, annotated1).annotation().setText("Abacate");

        SIList persistent = instance.asAtrAnnotation().persistentAnnotations();
        FormSerialized serialized = FormSerializationUtil.toSerializedObject(persistent);
        SIList deserialized = (SIList) FormSerializationUtil.toInstance(serialized);

        SIComposite justCreated = baseCompositeField.newInstance();

        justCreated.asAtrAnnotation().loadAnnotations(deserialized);
        SIList anotherPersistent = justCreated.asAtrAnnotation().persistentAnnotations();
        assertThat(extractProperty("text").from(anotherPersistent.getValues()))
                .containsOnly("Abacate");
    }


    @Test public void youCanRePersistTheAnnotationAsMuchAsYouWant(){
        RefType ref = new RefType() {
            @Override
            protected SType<?> retrieve() {
                return baseCompositeField;
            }
        };
        SIComposite instance = (SIComposite) SDocumentFactory.empty().createInstance(ref);

        asAnnotation(instance, annotated1).annotation().setText("Abacate");

        SIList persistent = instance.asAtrAnnotation().persistentAnnotations();
        FormSerialized serialized = FormSerializationUtil.toSerializedObject(persistent);
        SIList deserialized = (SIList) FormSerializationUtil.toInstance(serialized);
        asAnnotation(instance, annotated1).clear();
        instance.asAtrAnnotation().loadAnnotations(deserialized);
        SIList anotherPersistent = instance.asAtrAnnotation().persistentAnnotations();
        assertThat(extractProperty("text").from(anotherPersistent.getValues())).containsOnly("Abacate");
    }

    @Test public void youCanEreaseAnnotationsFromASingleInstance(){
        SIComposite instance = baseCompositeField.newInstance();

        asAnnotation(instance, annotated1).annotation().setText("Abacate");

        asAnnotation(instance, annotated1).annotation().clear();

        assertThat(asAnnotation(instance, annotated1).annotation()).isNotNull();
        assertThat(asAnnotation(instance, annotated1).annotation().getText()).isNullOrEmpty();
        assertThat(asAnnotation(instance, annotated1).annotation().getApproved()).isNull();
    }

    @Test public void youCanEreaseAnnotationsFromTheMixin(){
        SIComposite instance = baseCompositeField.newInstance();

        asAnnotation(instance, annotated1).annotation().setText("Abacate");

        asAnnotation(instance, annotated1).clear();

        List<SIAnnotation> all = instance.asAtrAnnotation().allAnnotations();
        assertThat(asAnnotation(instance, annotated1).annotation().getText()).isNullOrEmpty();
        assertThat(asAnnotation(instance, annotated1).annotation().getApproved()).isNull();
    }

    @Test public void aNewInstanceHasNoAnnotationsOrChilds(){
        SIComposite instance = baseCompositeField.newInstance();
        assertThat(instance.asAtrAnnotation().hasAnnotationOnTree()).isFalse();
    }

    @Test public void itsOwnAnnotationCountsAsChild(){
        SIComposite instance = baseCompositeField.newInstance();
        instance.asAtrAnnotation().annotation().setText("anything");
        assertThat(instance.asAtrAnnotation().hasAnnotationOnTree()).isTrue();
    }

    @Test public void returnsIfAnyOfItsChildHasAnnotations(){
        SIComposite instance = baseCompositeField.newInstance();
        asAnnotation(instance, annotated1).annotation().setText("anything");
        assertThat(instance.asAtrAnnotation().hasAnnotationOnTree()).isTrue();
    }

    @Test public void anEmptyInstanceHasNoRefusals(){
        SIComposite instance = baseCompositeField.newInstance();
        assertThat(instance.asAtrAnnotation().hasAnyRefusal()).isFalse();
    }

    @Test public void anApprovedInstanceIsnNotRefused(){
        SIComposite instance = baseCompositeField.newInstance();
        instance.asAtrAnnotation().annotation().setApproved(true);
        assertThat(instance.asAtrAnnotation().hasAnyRefusal()).isFalse();
    }

    @Test public void anRejectedInstanceIsnRefused(){
        SIComposite instance = baseCompositeField.newInstance();
        instance.asAtrAnnotation().annotation().setApproved(false);
        assertThat(instance.asAtrAnnotation().hasAnyRefusal()).isTrue();
    }

    @Test public void returnsIfAnyOfItsChildIsRefused(){
        SIComposite instance = baseCompositeField.newInstance();
        asAnnotation(instance, annotated1).annotation().setApproved(false);
        assertThat(instance.asAtrAnnotation().hasAnyRefusal()).isTrue();
    }

    @Test public void returnsIfHasAnAnnotatedChild(){
        SIComposite instance = baseCompositeField.newInstance();
        assertThat(instance.asAtrAnnotation().isOrHasAnnotatedChild()).isTrue();
    }

    private AtrAnnotation asAnnotation(SIComposite instance, STypeComposite<? extends SIComposite> field) {
        return instance.getDescendant(field).asAtrAnnotation();
    }

}
