package br.net.mirante.singular.form.mform.core.annotation;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.groups.Properties.extractProperty;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.net.mirante.singular.form.mform.PackageBuilder;
import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.SList;
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
        baseCompositeField = localPackage.createTipoComposto("group");
        baseCompositeField.addCampoString("notAnnotated");

        annotated1 = baseCompositeField.addCampoComposto("annotatedGroup1");
        field11 = annotated1.addCampoString("field11");
        annotated1.as(AtrAnnotation::new).setAnnotated();

        annotated2 = baseCompositeField.addCampoComposto("annotatedGroup2");
        annotated2.addCampoString("field121");
        annotated2.addCampoString("field122");
        annotated2.as(AtrAnnotation::new).setAnnotated();

        notAnnotated = baseCompositeField.addCampoComposto("notAnnotatedGroup3");
        notAnnotated.addCampoString("field13");
        annotated4 = notAnnotated.addCampoComposto("annotatedSubGroup4");
        annotated4.addCampoString("field341");
        annotated4.as(AtrAnnotation::new).setAnnotated();

    }

    @Test public void aNewInstanceHasNoAnnotations(){
        SIComposite instance = baseCompositeField.novaInstancia();
        assertThat(instance.as(AtrAnnotation::new).allAnnotations()).isEmpty();
    }

    @Test public void returnAllAnnotationsFromInstance(){
        SIComposite instance = baseCompositeField.novaInstancia();

        asAnnotation(instance, annotated1).annotation().setText("Abacate");
        asAnnotation(instance, annotated2).annotation().setText("Avocado");
        asAnnotation(instance, annotated4).annotation().setText("ukwatapheya");

        List<SIAnnotation> all = instance.as(AtrAnnotation::new).allAnnotations();
        assertThat(extractProperty("text").from(all)).containsOnly("Abacate","Avocado","ukwatapheya");
    }

    @Test public void returnAllAnnotationsFromInstanceWithoutEmptyOnes(){
        SIComposite instance = baseCompositeField.novaInstancia();

        asAnnotation(instance, annotated1).annotation().setText("Abacate");
        asAnnotation(instance, annotated2).annotation();
        asAnnotation(instance, annotated4).annotation().setText("ukwatapheya");

        List<SIAnnotation> all = instance.as(AtrAnnotation::new).allAnnotations();
        assertThat(extractProperty("text").from(all))
                .containsOnly("Abacate",null, "ukwatapheya");
    }

    @Test public void returnAllAnnotationsInAPersitentObject(){
        SIComposite instance = baseCompositeField.novaInstancia();

        asAnnotation(instance, annotated1).annotation().setText("Abacate");
        asAnnotation(instance, annotated2).annotation().setApproved(true);
        asAnnotation(instance, annotated4).annotation().setText("ukwatapheya");

        SList persistent = instance.as(AtrAnnotation::new).persistentAnnotations();
        assertThat(persistent.getType()).isInstanceOf(STypeAnnotationList.class);
        assertThat(persistent.getTipoElementos()).isInstanceOf(STypeAnnotation.class);
        assertThat(extractProperty("text").from(persistent.getValores()))
                .containsOnly("Abacate", null, "ukwatapheya");
        assertThat(extractProperty("approved").from(persistent.getValores()))
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

        SList persistent = instance.as(AtrAnnotation::new).persistentAnnotations();
        FormSerialized serialized = FormSerializationUtil.toSerializedObject(persistent);
        SList deserialized = (SList) FormSerializationUtil.toInstance(serialized);

        SIComposite justCreated = baseCompositeField.novaInstancia();

        justCreated.as(AtrAnnotation::new).loadAnnotations(deserialized);
        SList anotherPersistent = justCreated.as(AtrAnnotation::new).persistentAnnotations();
        assertThat(extractProperty("text").from(anotherPersistent.getValores()))
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

        SList persistent = instance.as(AtrAnnotation::new).persistentAnnotations();
        FormSerialized serialized = FormSerializationUtil.toSerializedObject(persistent);
        SList deserialized = (SList) FormSerializationUtil.toInstance(serialized);
        asAnnotation(instance, annotated1).clear();
        instance.as(AtrAnnotation::new).loadAnnotations(deserialized);
        SList anotherPersistent = instance.as(AtrAnnotation::new).persistentAnnotations();
        assertThat(extractProperty("text").from(anotherPersistent.getValores())).containsOnly("Abacate");
    }

    @Test public void youCanEreaseAnnotationsFromASingleInstance(){
        SIComposite instance = baseCompositeField.novaInstancia();

        asAnnotation(instance, annotated1).annotation().setText("Abacate");

        asAnnotation(instance, annotated1).annotation().clear();

        assertThat(asAnnotation(instance, annotated1).annotation()).isNotNull();
        assertThat(asAnnotation(instance, annotated1).annotation().getText()).isNullOrEmpty();
        assertThat(asAnnotation(instance, annotated1).annotation().getApproved()).isNull();
    }

    @Test public void youCanEreaseAnnotationsFromTheMixin(){
        SIComposite instance = baseCompositeField.novaInstancia();

        asAnnotation(instance, annotated1).annotation().setText("Abacate");

        asAnnotation(instance, annotated1).clear();

        List<SIAnnotation> all = instance.as(AtrAnnotation::new).allAnnotations();
        assertThat(asAnnotation(instance, annotated1).annotation().getText()).isNullOrEmpty();
        assertThat(asAnnotation(instance, annotated1).annotation().getApproved()).isNull();
    }

    @Test public void aNewInstanceHasNoAnnotationsOrChilds(){
        SIComposite instance = baseCompositeField.novaInstancia();
        assertThat(instance.as(AtrAnnotation::new).hasAnnotationOnTree()).isFalse();
    }

    @Test public void itsOwnAnnotationCountsAsChild(){
        SIComposite instance = baseCompositeField.novaInstancia();
        instance.as(AtrAnnotation::new).annotation().setText("anything");
        assertThat(instance.as(AtrAnnotation::new).hasAnnotationOnTree()).isTrue();
    }

    @Test public void returnsIfAnyOfItsChildHasAnnotations(){
        SIComposite instance = baseCompositeField.novaInstancia();
        asAnnotation(instance, annotated1).annotation().setText("anything");
        assertThat(instance.as(AtrAnnotation::new).hasAnnotationOnTree()).isTrue();
    }

    @Test public void anEmptyInstanceHasNoRefusals(){
        SIComposite instance = baseCompositeField.novaInstancia();
        assertThat(instance.as(AtrAnnotation::new).hasAnyRefusal()).isFalse();
    }

    @Test public void anApprovedInstanceIsnNotRefused(){
        SIComposite instance = baseCompositeField.novaInstancia();
        instance.as(AtrAnnotation::new).annotation().setApproved(true);
        assertThat(instance.as(AtrAnnotation::new).hasAnyRefusal()).isFalse();
    }

    @Test public void anRejectedInstanceIsnRefused(){
        SIComposite instance = baseCompositeField.novaInstancia();
        instance.as(AtrAnnotation::new).annotation().setApproved(false);
        assertThat(instance.as(AtrAnnotation::new).hasAnyRefusal()).isTrue();
    }

    @Test public void returnsIfAnyOfItsChildIsRefused(){
        SIComposite instance = baseCompositeField.novaInstancia();
        asAnnotation(instance, annotated1).annotation().setApproved(false);
        assertThat(instance.as(AtrAnnotation::new).hasAnyRefusal()).isTrue();
    }

    @Test public void returnsIfHasAnAnnotatedChild(){
        SIComposite instance = baseCompositeField.novaInstancia();
        assertThat(instance.as(AtrAnnotation::new).isOrHasAnnotatedChild()).isTrue();
    }

    private AtrAnnotation asAnnotation(SIComposite instance, STypeComposite<? extends SIComposite> field) {
        return instance.getDescendant(field).as(AtrAnnotation::new);
    }

}
