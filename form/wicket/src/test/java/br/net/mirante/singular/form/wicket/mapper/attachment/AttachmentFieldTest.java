package br.net.mirante.singular.form.wicket.mapper.attachment;

import br.net.mirante.singular.form.mform.SDictionary;
import br.net.mirante.singular.form.mform.SISimple;
import br.net.mirante.singular.form.wicket.hepers.TestPackage;
import br.net.mirante.singular.form.wicket.test.base.TestApp;
import br.net.mirante.singular.form.wicket.test.base.TestPage;
import org.apache.wicket.Component;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static br.net.mirante.singular.form.wicket.hepers.TestFinders.findId;
import static org.fest.assertions.api.Assertions.assertThat;

@SuppressWarnings("rawtypes")
public class AttachmentFieldTest {

    private static SDictionary dicionario;
    private static TestPackage pacote;
    private WicketTester driver;
    private TestPage page;
    private FormTester form;

    @BeforeClass
    public static void createDicionario() {
        dicionario = SDictionary.create();
        pacote = dicionario.carregarPacote(TestPackage.class);
    }

    @Before
    public void setupPage() {
        driver = new WicketTester(new TestApp());
        page = new TestPage();
        page.setDicionario(dicionario);
        page.setNewInstanceOfType(TestPackage.TIPO_ATTACHMENT);
        page.build();
        driver.startPage(page);
    }

    @Before
    public void setupFormAssessor() {
        form = driver.newFormTester("test-form", false);
    }

    @Test
    public void generatesFieldsResposibleForCompositeParts() {
        driver.assertEnabled(formField(form, "file_name_fileField"));
        driver.assertEnabled(formField(form, "file_hash_fileField"));
        driver.assertEnabled(formField(form, "file_size_fileField"));
        driver.assertEnabled(formField(form, "file_id_fileField"));
    }

    private String formField(FormTester form, String leafName) {
        return "test-form:" + findId(form.getForm(), leafName).get();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void onSubmissionItPopulatesTheFieldsOfTheAttachmentComposite() {
        form.setValue(findId(form.getForm(), "file_name_fileField").get(), "abacate.png");
        form.setValue(findId(form.getForm(), "file_hash_fileField").get(), "1234567890asdfghj");
        form.setValue(findId(form.getForm(), "file_size_fileField").get(), "1234");
        form.setValue(findId(form.getForm(), "file_id_fileField").get(), "1020304050");
        form.submit("save-btn");

        String attachmentName = pacote.attachmentFileField.getNomeSimples();
        List<SISimple> values = (List) page.getCurrentInstance().getValor(attachmentName);
        assertThat(findValueInList(values, "name")).isEqualTo("abacate.png");
        assertThat(findValueInList(values, "hashSHA1")).isEqualTo("1234567890asdfghj");
        assertThat(findValueInList(values, "size")).isEqualTo(1234);
        assertThat(findValueInList(values, "fileId")).isEqualTo("1020304050");
    }

    @Test
    public void componentMustHaveAUploadBehaviourWhichReflectsTheUploadUrl() {
        Component attachmentComponent = page.get(formField(form, "_attachment_fileField"));
        List<UploadBehavior> behaviours = attachmentComponent.getBehaviors(UploadBehavior.class);
        assertThat(behaviours).hasSize(1);
    }

    private Object findValueInList(List<SISimple> list, String propName) {
        for (SISimple m : list) {
            if (m.getNome().equals(propName))
                return m.getValor();
        }
        return null;
    }

}
