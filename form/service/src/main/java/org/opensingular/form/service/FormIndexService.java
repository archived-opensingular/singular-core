package org.opensingular.form.service;

import org.opensingular.form.SInfoType;
import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.io.SFormXMLUtil;
import org.opensingular.form.persistence.FormKeyLong;
import org.opensingular.form.persistence.dao.FormDAO;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.lib.commons.scan.SingularClassPathScanner;

import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
@Named
public class FormIndexService {

    @Inject
    private FormFieldService formFieldService;

    @Inject
    private FormDAO formDAO;

    /**
     * Recupera todos os formularios e a partir do seu tipo encontra a classe de seu SType no classpath.
     * Com a classe do SType carrega uma SInstance que é passada para o serviço FormFieldService para indexação
     */
    public void indexAllForms() {
        List<FormEntity> forms = formDAO.listAll();

        SingularClassPathScanner scanner = SingularClassPathScanner.get();
        Set<Class<?>>            classes = scanner.findClassesAnnotatedWith(SInfoType.class);
        classes.removeAll(classes.stream()
                .filter(c -> c.getName().contains("org.opensingular"))
                .collect(Collectors.toList()));

        for (FormEntity form : forms) {
            String formType = form.getFormType().getAbbreviation();
            String typeName = formType.substring(formType.lastIndexOf(".")+1, formType.length());
            Optional<Class<?>> clazz = classes.stream()
                            .filter(item -> item.getName().contains(typeName))
                            .findFirst();

            if (clazz.isPresent()) {
                Class formClass = clazz.get();
                RefType refType = RefType.of(formClass);
                SDocumentFactory sDocumentFactory = SDocumentFactory.empty();
                SInstance instance = SFormXMLUtil.fromXML(refType, form.getCurrentFormVersionEntity().getXml(), sDocumentFactory);

                formFieldService.saveFields(instance, form.getFormType(), form.getCurrentFormVersionEntity());
            } else {
                System.out.println("Não foi possível indexar o form " + formType);
            }
        }

        System.out.println("Indexação completa");
    }

}
