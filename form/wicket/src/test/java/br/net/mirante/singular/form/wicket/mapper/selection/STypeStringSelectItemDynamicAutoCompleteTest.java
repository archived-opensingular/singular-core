package br.net.mirante.singular.form.wicket.mapper.selection;

import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class STypeStringSelectItemDynamicAutoCompleteTest {

//    private static class Base extends SingularFormBaseTest {
//
//        final String[] DOMAINS = {"@gmail.com", "@hotmail.com", "@yahoo.com"};
//        protected STypeString base;
//
//        @Override
//        protected void buildBaseType(STypeComposite<?> baseType) {
//            base = baseType.addFieldString("myHero");
//            base.withSelectionFromProvider(new SOptionsProvider() {
//                @Override
//                public SIList<? extends SInstance> listOptions(SInstance instance, String filter) {
//                    SIList<?> r = base.newList();
//                    for(String d : DOMAINS){
//                        r.addNew().setValue(d);
//                    }
//                    return r;
//                }
//            });
//            base.withView(new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC));
//        }
//
//        protected SIString fieldInstance() {
//            return page.getCurrentInstance().getDescendant(base);
//        }
//
//        protected TextField fieldComponent() {
//            return (TextField) ((List) findTag(form.getForm(), TextField.class)).get(0);
//        }
//
//    }
//
//    public static class Default extends Base {
//
//        @Test public void renderField(){
//            assertThat(findTag(form.getForm(), TypeaheadComponent.class)).hasSize(1);
//            assertThat(findTag(form.getForm(), TextField.class)).hasSize(2);
//        }
//
//        @Test public void haveABloodhoundBehabiour(){
//            List<Component> tag = findTag(form.getForm(), TypeaheadComponent.class);
//            assertThat(tag.get(0).getBehaviors()).haveAtLeast(1, new Condition<Behavior>() {
//                @Override
//                public boolean matches(Behavior value) {
//                    return BloodhoundDataBehavior.class.isInstance(value);
//                }
//            });
//        }
//
//    }
}
