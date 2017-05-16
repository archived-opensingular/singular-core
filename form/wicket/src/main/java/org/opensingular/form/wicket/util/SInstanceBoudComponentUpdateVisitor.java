package org.opensingular.form.wicket.util;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.opensingular.form.SInstance;
import org.opensingular.form.wicket.model.ISInstanceAwareModel;

import java.util.Set;


public class SInstanceBoudComponentUpdateVisitor implements IVisitor<Component, Void> {

    private final AjaxRequestTarget ajaxRequestTarget;
    private final Set<SInstance> instances;

    public SInstanceBoudComponentUpdateVisitor(AjaxRequestTarget ajaxRequestTarget, Set<SInstance> instances) {
        this.ajaxRequestTarget = ajaxRequestTarget;
        this.instances = instances;
    }

    @Override
    public void component(Component component, IVisit<Void> v) {
        IModel<?> model = component.getDefaultModel();
        if (model instanceof ISInstanceAwareModel) {
            ISInstanceAwareModel instanceAwareModel = (ISInstanceAwareModel) model;
            if (instances.contains(instanceAwareModel.getSInstance())) {
                WicketFormProcessing.refreshComponentOrCellContainer(ajaxRequestTarget, component);
            }
        }
    }

}
