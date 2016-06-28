package br.net.mirante.singular.form.wicket.mapper.attachment;

import org.apache.wicket.Component;
import org.apache.wicket.IResourceListener;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

public abstract class BaseJQueryFileUploadBehavior<T> extends Behavior implements IResourceListener {

    private Component component;
    private IModel<T> model;

    public BaseJQueryFileUploadBehavior(IModel<T> model) {
        this.model = model;
    }

    protected T currentInstance() {
        return model.getObject();
    }

    protected StringValue getParamFileId(String fileId) {
        return params().getParameterValue(fileId);
    }

    protected IRequestParameters params() {
        WebRequest request = (WebRequest) RequestCycle.get().getRequest();
        return request.getRequestParameters();
    }

    @Override
    public void bind(Component component) {
        this.component = component;
    }

    public String getUrl() {
        return component.urlFor(this, IResourceListener.INTERFACE, new PageParameters()).toString();
    }
}