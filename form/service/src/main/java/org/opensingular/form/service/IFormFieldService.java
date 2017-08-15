package org.opensingular.form.service;


import org.opensingular.form.SInstance;
import org.opensingular.form.persistence.entity.FormEntity;
import org.opensingular.form.persistence.entity.FormTypeEntity;
import org.opensingular.form.persistence.entity.FormVersionEntity;

public interface IFormFieldService {

    void saveFields(SInstance instance, FormTypeEntity formType, FormVersionEntity formVersion);
}
