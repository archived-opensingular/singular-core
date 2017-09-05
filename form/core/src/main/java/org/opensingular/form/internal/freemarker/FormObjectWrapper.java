package org.opensingular.form.internal.freemarker;

import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.*;
import org.opensingular.form.*;
import org.opensingular.form.type.core.*;
import org.opensingular.lib.commons.util.Loggable;

import java.util.Map;


public class FormObjectWrapper implements ObjectWrapper, Loggable {

    private boolean escapeContentHtml;
    private ObjectWrapper fallBackWrapper;

    public FormObjectWrapper(boolean escapeContentHtml) {
        this.escapeContentHtml = escapeContentHtml;
        this.fallBackWrapper = new BeansWrapperBuilder(Configuration.VERSION_2_3_22).build();
    }

    @Override
    public TemplateModel wrap(Object obj) throws TemplateModelException {
        return newTemplateModel(obj, escapeContentHtml);
    }

    public TemplateModel newTemplateModel(Object obj, boolean escapeContentHtml) {
        if (obj == null) {
            return null;
        } else if (obj instanceof SISimple) {
            return newTemplateModelSimple((SISimple) obj, escapeContentHtml);
        } else if (obj instanceof SIComposite) {
            return new SICompositeTemplateModel((SIComposite) obj, this, escapeContentHtml);
        } else if (obj instanceof SIList) {
            return new SListTemplateModel((SIList<?>) obj, this, escapeContentHtml);
        } else if (obj instanceof Map) {
            return new SimpleHash((Map) obj, this);
        }
        if (obj instanceof SInstance) {
            String msg = "A classe " + obj.getClass().getName() + " não é suportada para mapeamento no template";
            throw new SingularFormException(msg, (SInstance) obj);
        }
        try {
            return fallBackWrapper.wrap(obj);
        } catch (TemplateModelException e) {
            throw SingularFormException.rethrow("Nao foi possivel fazer wrap da classe" + obj.getClass(), e);
        }
    }

    public TemplateModel newTemplateModelSimple(SISimple obj, boolean escapeContentHtml) {
        if (obj != null && obj.getValue() == null) { // && !(obj instanceof SIString)
            return null;//nullModel
        }
        if (obj instanceof SIString) {
            return new SSimpleTemplateModel(obj, this, escapeContentHtml);
        } else if (obj instanceof SINumber) {
            return new SNumberTemplateModel<>((SINumber<?>) obj, this);
        } else if (obj instanceof SIBoolean) {
            return new SIBooleanTemplateModel((SIBoolean) obj, this);
        } else if (obj instanceof SIDate) {
            return new SIDateTemplateModel((SIDate) obj, this);
        } else if (obj instanceof SIDateTime) {
            return new SIDateTimeTemplateModel((SIDateTime) obj, this);
        } else if (obj instanceof SITime) {
            return new SITimeTemplateModel((SITime) obj, this);
        }
        return new SSimpleTemplateModel(obj, this, escapeContentHtml);
    }
}
