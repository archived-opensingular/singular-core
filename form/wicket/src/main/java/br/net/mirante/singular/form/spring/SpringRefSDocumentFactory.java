package br.net.mirante.singular.form.spring;

import br.net.mirante.singular.form.mform.document.RefSDocumentFactory;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;

/**
 * Referência serializável a uma fábrica de documentos que utiliza referência
 * estática ao ApplicationContext do Spring e o nome do bean no Spring da
 * fábrica para recuperá-la mais adiante.
 *
 * @author Daniel C. Bordin
 */
public class SpringRefSDocumentFactory extends RefSDocumentFactory {

    private final String springBeanName;

    public SpringRefSDocumentFactory(SpringSDocumentFactory springSDocumentFactory) {
        super(springSDocumentFactory);
        this.springBeanName = SpringFormUtil.checkBeanName(springSDocumentFactory);
    }

    @Override
    protected SDocumentFactory retrieve() {
        SDocumentFactory f = null;
        if (springBeanName != null) {
            f = SpringFormUtil.getApplicationContext().getBean(springBeanName, SDocumentFactory.class);
        }
        return f;
    }
}
