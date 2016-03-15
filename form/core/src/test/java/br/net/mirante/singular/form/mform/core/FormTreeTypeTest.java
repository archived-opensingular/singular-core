package br.net.mirante.singular.form.mform.core;

import br.net.mirante.singular.form.mform.*;
import org.junit.Ignore;
import org.junit.Test;

public class FormTreeTypeTest extends SPackage {

    private STypeComposite node;

    public FormTreeTypeTest() {
        super("a.test.pkg");
    }

//    @Override
//    protected void carregarDefinicoes(PackageBuilder pb) {
//        super.carregarDefinicoes(pb);
//
//        node = pb.createTipo(NodeType.class);
//    }

    @Test public void shouldNotLoop(){
        SDictionary dict = SDictionary.create();
//        FormTreeTypeTest pkg = (FormTreeTypeTest) dict.loadPackage((Class)FormTreeTypeTest.class);
//        STypeComposite<? extends SIComposite> node = pkg.createTipoComposto("node");

        PackageBuilder pkg = dict.createNewPackage("pkg");
        STypeComposite<? extends SIComposite> node = pkg.createCompositeType("node");

        node.addFieldString("nome");
        node.addFieldString("type");
        node.addFieldListOf("child",node);


        //FIXME: It seems the isse reside on the setRoot
        SIComposite siComposite = (SIComposite) node.newInstance();
        siComposite.getField("nome").setValue("Me");
        siComposite.getField("type").setValue("the type");
    }

//    @MInfoTipo(nome = "NodeType", pacote = FormTreeTypeTest.class)
//    public static class NodeType extends STypeComposite<SIComposite> {
//
//        @Override
//        protected void onLoadType(TypeBuilder tb) {
//            super.onLoadType(tb);
//            addCampoString("nome");
//            addCampoString("type");
//            addCampoListaOf("child",NodeType.class);
//        }
//    }

}
