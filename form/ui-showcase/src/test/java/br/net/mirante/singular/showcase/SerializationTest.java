package br.net.mirante.singular.showcase;

import br.net.mirante.singular.showcase.dao.form.TemplateRepository;
import br.net.mirante.singular.form.mform.SIComposite;
import br.net.mirante.singular.form.mform.io.FormSerializationUtil;
import br.net.mirante.singular.showcase.view.page.form.examples.ExamplePackage;

import org.junit.Test;

//@RunWith(value = Parameterized.class)
public class SerializationTest {
//
//    TemplateRepository.TemplateEntry entry;
//
//    public SerializationTest(TemplateRepository.TemplateEntry entry){
//        this.entry = entry;
//    }
//
//    @Before public void setResolver(){
//        TemplateRepository.setDefault(TemplateRepository.get());
//    }

//    @Parameterized.Parameters(name = "{index}: serializeAndDeserialize({0})")
//    public static Iterable<Object[]> data1() {
//        Collection<TemplateRepository.TemplateEntry> entries = TemplateRepository.get().getEntries();
//        return entries.stream().map( (x) -> new Object[]{x}).collect(Collectors.toList());
//    }
//
//    @Test public void serializeAndDeserialize(){
//        MInstancia instance = entry.getType().novaInstancia();
//        FormSerializationUtil.toInstance(FormSerializationUtil.toSerializedObject(instance));
//    }

    @Test public void serializeAndDeserialize(){
        TemplateRepository repo = TemplateRepository.create();
//        TemplateRepository.setDefault(TemplateRepository.get());
        ExamplePackage pacote = null;
        for(TemplateRepository.TemplateEntry entry: repo.getEntries()){
            if(entry.getType().getName().equals(ExamplePackage.Types.ORDER.name)){
                pacote = (ExamplePackage) entry.getType().getPacote();
            }
        }
        SIComposite order = pacote.order.novaInstancia();
        order.setValor(pacote.orderNumber.getSimpleName(),1);
        FormSerializationUtil.toInstance(FormSerializationUtil.toSerializedObject(order));
    }

}
