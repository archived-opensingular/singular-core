package org.opensingular.form.dependson;


import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SType;
import org.opensingular.form.sample.AntaqPackage;
import org.opensingular.form.sample.Resolucao912Form;

public class TestDelayedDependsOn {

    @Test
    public void testDependsOn() throws Exception {
        SDictionary dictionary = SDictionary.create();
        dictionary.loadPackage(AntaqPackage.class);

        Resolucao912Form stype = dictionary.getType(Resolucao912Form.class);
        SIComposite composite = stype.newInstance();

        for (SType s : stype.embarcacoes.embarcacoes.getDependentTypes()){
            System.out.println(s.getName());
        }

    }
}
