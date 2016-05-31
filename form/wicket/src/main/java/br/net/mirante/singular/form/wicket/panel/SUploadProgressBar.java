package br.net.mirante.singular.form.wicket.panel;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.IInitializer;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.servlet.MultipartServletWebRequestImpl;
import org.apache.wicket.protocol.http.servlet.UploadInfo;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.*;
import org.apache.wicket.resource.CoreLibrariesContributor;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

import javax.servlet.http.HttpServletRequest;
import java.util.Formatter;

/**
 * Created by nuk on 19/05/16.
 * Ripoff od {@link UploadProgressBar} since it's not designed for extension.
 * Example found at http://examples7x.wicket.apache.org/upload/single
 *
 */
public abstract class SUploadProgressBar extends Panel {
    public static final String RESOURCE_STARTING = "UploadProgressBar.starting";

    /**
     * Initializer for this component; binds static resources.
     */
    public final static class ComponentInitializer implements IInitializer
    {
        @Override
        public void init(final Application application)
        {
            // register the upload status resource
            application.getSharedResources().add(RESOURCE_NAME, new UploadStatusResource());
        }

        @Override
        public String toString()
        {
            return "UploadProgressBar initializer";
        }

        @Override
        public void destroy(final Application application)
        {
        }
    }

    private static final ResourceReference JS = new JavaScriptResourceReference(
            UploadProgressBar.class, "progressbar.js");

    private static final ResourceReference CSS = new CssResourceReference(
            UploadProgressBar.class, "UploadProgressBar.css");

    private static final String RESOURCE_NAME = UploadProgressBar.class.getName();

    private static final long serialVersionUID = 1L;

    private MarkupContainer statusDiv;

    private MarkupContainer barDiv;

    private final FileUploadField uploadField;

    public SUploadProgressBar(final String id, final FileUploadField uploadField)
    {
        super(id);

        this.uploadField = uploadField;
        if (uploadField != null)
        {
            uploadField.setOutputMarkupId(true);
        }

        setRenderBodyOnly(true);
    }

    @Override
    protected void onInitialize()
    {
        super.onInitialize();
        getCallbackForm().setOutputMarkupId(true);

        barDiv = newBarComponent("bar");
        add(barDiv);

        statusDiv = newStatusComponent("status");
        add(statusDiv);
    }

    /**
     * Creates a component for the status text
     *
     * @param id
     *          The component id
     * @return the status component
     */
    protected MarkupContainer newStatusComponent(String id)
    {
        WebMarkupContainer status = new WebMarkupContainer(id);
        status.setOutputMarkupId(true);
        return status;
    }

    /**
     * Creates a component for the bar
     *
     * @param id
     *          The component id
     * @return the bar component
     */
    protected MarkupContainer newBarComponent(String id)
    {
        WebMarkupContainer bar = new WebMarkupContainer(id);
        bar.setOutputMarkupId(true);
        return bar;
    }

    /**
     * Override this to provide your own CSS, or return null to avoid including the default.
     *
     * @return ResourceReference for your CSS.
     */
    protected ResourceReference getCss()
    {
        return CSS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderHead(final IHeaderResponse response)
    {
        super.renderHead(response);

        CoreLibrariesContributor.contributeAjax(getApplication(), response);
        response.render(JavaScriptHeaderItem.forReference(JS));
        ResourceReference css = getCss();
        if (css != null)
        {
            response.render(CssHeaderItem.forReference(css));
        }

        ResourceReference ref = new SharedResourceReference(RESOURCE_NAME);

        final String uploadFieldId = (uploadField == null) ? "" : uploadField.getMarkupId();

        final String status = new StringResourceModel(RESOURCE_STARTING, this, (IModel<?>)null).getString();

        CharSequence url = urlFor(ref, UploadStatusResource.newParameter(getPage().getId()));

        StringBuilder builder = new StringBuilder(128);
        Formatter formatter = new Formatter(builder);

        formatter.format(
                "new Wicket.WUPB('%s', '%s', '%s', '%s', '%s', '%s');",
                getCallbackForm().getMarkupId(), statusDiv.getMarkupId(), barDiv.getMarkupId(), url, uploadFieldId,
                status);

        formatter.close();
        response.render(OnDomReadyHeaderItem.forScript(builder.toString()));
    }

    /**
     * Form on where will be installed the JavaScript callback to present the progress bar.
     * {@link ModalWindow} is designed to hold nested forms and the progress bar callback JavaScript
     * needs to be add at the form inside the {@link ModalWindow} if one is used.
     *
     * @return form
     */
    private Form<?> getCallbackForm()
    {
        Boolean insideModal = getForm().visitParents(ModalWindow.class,
                new IVisitor<ModalWindow, Boolean>()
                {
                    @Override
                    public void component(final ModalWindow object, final IVisit<Boolean> visit)
                    {
                        visit.stop(true);
                    }
                });
        if ((insideModal != null) && insideModal)
        {
            return getForm();
        }
        else
        {
            return getForm().getRootForm();
        }
    }

    protected abstract Form<?> getForm();
}

class UploadStatusResource extends AbstractResource
{

    private static final long serialVersionUID = 1L;

    private static final String UPLOAD_PARAMETER = "upload";

    /**
     * Resource key used to retrieve status message for.
     *
     * Example: UploadStatusResource.status=${percentageComplete}% finished, ${bytesUploadedString}
     * of ${totalBytesString} at ${transferRateString}; ${remainingTimeString}
     */
    public static final String RESOURCE_STATUS = "UploadStatusResource.status";

    @Override
    protected ResourceResponse newResourceResponse(final Attributes attributes)
    {
        // Determine encoding
        final String encoding = Application.get()
                .getRequestCycleSettings()
                .getResponseRequestEncoding();

        ResourceResponse response = new ResourceResponse();
        response.setContentType("text/html; charset=" + encoding);
        response.setCacheDuration(Duration.NONE);

        final String status = getStatus(attributes);
        response.setWriteCallback(new WriteCallback()
        {
            @Override
            public void writeData(final Attributes attributes)
            {
                attributes.getResponse().write("<html><body>|");
                attributes.getResponse().write(status);
                attributes.getResponse().write("|</body></html>");
            }
        });

        return response;
    }

    /**
     * @param attributes
     * @return status string with progress data that will feed the progressbar.js variables on
     *         browser to update the progress bar
     */
    private String getStatus(final Attributes attributes)
    {
        final String upload = attributes.getParameters().get(UPLOAD_PARAMETER).toString();

        final HttpServletRequest req = (HttpServletRequest)attributes.getRequest()
                .getContainerRequest();

        UploadInfo info = MultipartServletWebRequestImpl.getUploadInfo(req, upload);

        String status;
        if ((info == null) || (info.getTotalBytes() < 1))
        {
            status = "100|";
        }
        else
        {
            status = info.getPercentageComplete() +
                    "|" +
                    new StringResourceModel(RESOURCE_STATUS, (Component)null, Model.of(info)).getString();
        }
        return status;
    }

    /**
     * Create a new parameter for the given identifier of a {@link UploadInfo}.
     *
     * @param upload
     *            identifier
     * @return page parameter suitable for URLs to this resource
     */
    public static PageParameters newParameter(String upload)
    {
        return new PageParameters().add(UPLOAD_PARAMETER, upload);
    }
}
