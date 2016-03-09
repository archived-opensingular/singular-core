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
        STypeComposite<? extends SIComposite> node = pkg.createTipoComposto("node");

        node.addCampoString("nome");
        node.addCampoString("type");
        node.addCampoListaOf("child",node);


        //FIXME: It seems the isse reside on the setRoot
        SIComposite siComposite = (SIComposite) node.novaInstancia();
        siComposite.getCampo("nome").setValue("Me");
        siComposite.getCampo("type").setValue("the type");
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
