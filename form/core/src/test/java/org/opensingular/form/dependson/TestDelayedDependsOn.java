package org.opensingular.form.dependson;


import org.junit.Test;
import org.opensingular.form.SDictionary;
import org.opensingular.form.SIComposite;
import org.opensingular.form.SType;
import org.opensingular.form.sample.AntaqPackage;
import org.opensingular.form.sample.Resolucao912Form;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDelayedDependsOn {

    private static final Logger logger = LoggerFactory.getLogger(TestDelayedDependsOn.class);

    @Test
    public void testDependsOn() throws Exception {
        SDictionary dictionary = SDictionary.create();
        dictionary.loadPackage(AntaqPackage.class);

        Resolucao912Form stype     = dictionary.getType(Resolucao912Form.class);
        SIComposite      composite = stype.newInstance();

        org.junit.Assert.assertEquals(2, stype.embarcacoes.embarcacoes.getDependentTypes().size());
        for (SType s : stype.embarcacoes.embarcacoes.getDependentTypes()){
            logger.info(s.getName());
        }

    }
}
