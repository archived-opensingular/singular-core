package br.net.mirante.singular.form.wicket.mapper.selection;

import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class STypeStringKeyValueRadioTest {

//    private static class Base extends SingularFormBaseTest {
//        protected STypeComposite<? extends SIComposite> baseCompositeField;
//        protected STypeString tipoDeMedia;
//
//        @Override
//        protected void buildBaseType(STypeComposite<?> baseCompositeField) {
//            page.enableAnnotation();
//
//            this.baseCompositeField = baseCompositeField;
//            tipoDeMedia = baseCompositeField.addFieldString("tipoDeMedia");
//            tipoDeMedia.withRadioView();
//            tipoDeMedia.withSelectionFromProvider(new SOptionsProvider() {
//                @Override
//                public SIList<? extends SInstance> listOptions(SInstance optionsInstance, String filter) {
//                    STypeString type = (STypeString) optionsInstance.getType();
//                    SIList<?> r = type.newList();
//                    r.addElement(newElement(type, "IMG", "Imagem"));
//                    r.addElement(newElement(type, "TXT", "Texto"));
//                    r.addElement(newElement(type, "BIN", "Binário"));
//                    return r;
//                }
//
//                private SIString newElement(STypeString type, String id, String label) {
//                    SIString e = type.newInstance();
//                    e.setValue(id);
//                    e.setSelectLabel(label);
//                    return e;
//                }
//            });
//            tipoDeMedia.asAtrBasic().label("Tipo do Arquivo");
//        }
//    }
//
//    public static class Default extends Base {
//        @Test
//        public void rendersARadioChoiceWithInformedLabels() {
//            List<RadioChoice> inputs = (List) findTag(form.getForm(), RadioChoice.class);
//            assertThat(inputs).hasSize(1);
//            assertThat(extractProperty("value").from(inputs.get(0).getChoices()))
//                    .containsOnly("1", "2", "3");
//            assertThat(extractProperty("selectLabel").from(inputs.get(0).getChoices()))
//                    .containsOnly("Imagem", "Texto", "Binário");
//        }
//    }
//
//    public static class WithSelectedValue extends Base {
//        @Override
//        protected void populateInstance(SIComposite instance) {
//            instance.getDescendant(tipoDeMedia).setValue("TXT");
//        }
//
//        @Test
//        public void rendersARadioChoiceWithInformedOptionsRegardlessOfSelection() {
//            List<RadioChoice> inputs = (List) findTag(form.getForm(), RadioChoice.class);
//            assertThat(inputs).hasSize(1);
//            assertThat(extractProperty("value").from(inputs.get(0).getChoices()))
//                    .containsOnly("1", "2", "3");
//            assertThat(extractProperty("selectLabel").from(inputs.get(0).getChoices()))
//                    .containsOnly("Imagem", "Texto", "Binário");
//            assertThat(inputs.get(0).getValue()).isEqualTo("2");
//        }
//    }

}

