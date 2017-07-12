package org.opensingular.form.service;

import org.opensingular.form.SInstance;
import org.opensingular.form.SType;
import org.opensingular.form.STypeComposite;
import org.opensingular.form.document.RefType;
import org.opensingular.form.document.SDocumentFactory;
import org.opensingular.form.io.SFormXMLUtil;
import org.opensingular.form.persistence.FormKeyLong;
import org.opensingular.form.persistence.dao.FormDAO;
import org.opensingular.form.persistence.entity.FormEntity;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class FormIndexService {

    @Inject
    FormService formService;

    @Inject
    private FormFieldService formFieldService;

    @Inject
    private FormDAO formDAO;

    public void indexAllForms() throws ClassNotFoundException {
        List<FormEntity> forms = formDAO.listAll();
        for (FormEntity form : forms) {
            FormKeyLong key = new FormKeyLong(form.getCod());
            RefType refType = RefType.of(STypeComposite.class);
            SDocumentFactory sDocumentFactory = SDocumentFactory.empty();

            SInstance instance = SFormXMLUtil.fromXML(refType, form.getCurrentFormVersionEntity().getXml(), sDocumentFactory);
//            SInstance instance = formService.loadSInstance(key, refType, sDocumentFactory);

            System.out.print("form type: " + form.getFormType().getAbbreviation());
            System.out.println(" - versão: " + form.getCurrentFormVersionEntity().getCod());
        }
    }

}
