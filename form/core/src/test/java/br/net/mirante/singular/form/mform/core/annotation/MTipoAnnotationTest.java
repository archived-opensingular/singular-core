package br.net.mirante.singular.form.mform.core.annotation;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.MAnnotationView;
import br.net.mirante.singular.form.mform.core.MTipoString;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.groups.Properties.extractProperty;

public class MTipoAnnotationTest {
    protected static MDicionario dicionario;
    protected PacoteBuilder localPackage;
    private MTipoComposto<? extends MIComposto> baseCompositeField, annotated1, annotated2,
            notAnnotated, annotated4;
    private MTipoString field11;

    @Before
    public void createDicionario() {
        dicionario = MDicionario.create();

        localPackage = dicionario.criarNovoPacote("test");
        baseCompositeField = localPackage.createTipoComposto("group");
        baseCompositeField.addCampoString("notAnnotated");

        annotated1 = baseCompositeField.addCampoComposto("annotatedGroup1");
        field11 = annotated1.addCampoString("field11");
        annotated1.setView(MAnnotationView::new);

        annotated2 = baseCompositeField.addCampoComposto("annotatedGroup2");
        annotated2.addCampoString("field121");
        annotated2.addCampoString("field122");
        annotated2.setView(MAnnotationView::new);

        notAnnotated = baseCompositeField.addCampoComposto("notAnnotatedGroup3");
        notAnnotated.addCampoString("field13");
        annotated4 = notAnnotated.addCampoComposto("annotatedSubGroup4");
        annotated4.addCampoString("field341");
        annotated4.setView(MAnnotationView::new);

    }

    @Test public void aNewInstanceHasNoAnnotations(){
        MIComposto instance = baseCompositeField.novaInstancia();
        assertThat(instance.as(AtrAnnotation::new).allAnnotations()).isEmpty();
    }

    @Test public void returnAllAnnotationsFromInstance(){
        MIComposto instance = baseCompositeField.novaInstancia();

        asAnnotation(instance, annotated1).annotation().setText("Abacate");
        asAnnotation(instance, annotated2).annotation().setText("Avocado");
        asAnnotation(instance, annotated4).annotation().setText("ukwatapheya");

        List<MIAnnotation> all = instance.as(AtrAnnotation::new).allAnnotations();
        assertThat(extractProperty("text").from(all)).containsOnly("Abacate","Avocado","ukwatapheya");
    }

    @Test public void returnAllAnnotationsInAPersitentObject(){
        MIComposto instance = baseCompositeField.novaInstancia();

        asAnnotation(instance, annotated1).annotation().setText("Abacate");
        asAnnotation(instance, annotated2).annotation().setText("Avocado");
        asAnnotation(instance, annotated4).annotation().setText("ukwatapheya");

        MILista persistent = instance.as(AtrAnnotation::new).persistentAnnotations();
        assertThat(persistent.getMTipo()).isInstanceOf(MTipoAnnotationList.class);
        assertThat(persistent.getTipoElementos()).isInstanceOf(MTipoAnnotation.class);
        assertThat(extractProperty("text").from(persistent.getValores()))
                .containsOnly("Abacate","Avocado","ukwatapheya");
    }

    @Test public void youCanRePersistTheAnnotationAsMuchAsYouWant(){
        MIComposto instance = baseCompositeField.novaInstancia();

        asAnnotation(instance, annotated1).annotation().setText("Abacate");

        MILista persistent = instance.as(AtrAnnotation::new).persistentAnnotations();
//        instance.as(AtrAnnotation::new).loadAnnotations(persistent);
        MILista anotherPersistent = instance.as(AtrAnnotation::new).persistentAnnotations();
        assertThat(extractProperty("text").from(anotherPersistent.getValores())).containsOnly("Abacate");
    }

    private AtrAnnotation asAnnotation(MIComposto instance, MTipoComposto<? extends MIComposto> field) {
        return instance.getDescendant(field).as(AtrAnnotation::new);
    }
}
