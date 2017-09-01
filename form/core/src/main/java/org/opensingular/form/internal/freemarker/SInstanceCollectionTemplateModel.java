package org.opensingular.form.internal.freemarker;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;
import org.opensingular.form.SInstance;

import java.util.Collection;
import java.util.Iterator;


public class SInstanceCollectionTemplateModel implements TemplateCollectionModel {
    private final Collection<SInstance> collection;
    private final FormObjectWrapper formObjectWrapper;
    private boolean escapeContentHtml;

    public SInstanceCollectionTemplateModel(Collection<SInstance> collection, boolean escapeContentHtml,
                                            FormObjectWrapper formObjectWrapper) {
        this.collection = collection;
        this.escapeContentHtml = escapeContentHtml;
        this.formObjectWrapper = formObjectWrapper;
    }

    @Override
    public TemplateModelIterator iterator() throws TemplateModelException {
        Iterator<SInstance> it = collection.iterator();
        return new TemplateModelIterator() {

            @Override
            public TemplateModel next() {
                return formObjectWrapper.newTemplateModel(it.next(), escapeContentHtml);
            }

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }
        };
    }
}