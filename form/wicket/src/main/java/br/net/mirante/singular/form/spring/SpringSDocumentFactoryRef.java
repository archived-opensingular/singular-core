package br.net.mirante.singular.form.spring;

import org.springframework.context.ApplicationContext;

import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.mform.document.SDocumentFactoryRef;

/**
 * Referência serializável a uma fábrica de documentos que utiliza referência
 * estática ao ApplicationContext do Spring e o nome do bean no Spring da
 * fábrica para recuperá-la mais adiante.
 *
 * @author Daniel C. Bordin
 */
public class SpringSDocumentFactoryRef extends SDocumentFactoryRef {

    private final String springBeanName;

    public SpringSDocumentFactoryRef(String springBeanName) {
        this.springBeanName = springBeanName;
    }

    @Override
    protected SDocumentFactory reloadFactory() {
        SDocumentFactory f = null;
        if (springBeanName != null) {
            f = findApplicationContext().getBean(springBeanName, SDocumentFactory.class);
        }
        return f;
    }

    protected ApplicationContext findApplicationContext() {
        return SpringFormUtil.getApplicationContext();
    }

}
