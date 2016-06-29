package br.net.mirante.singular.form.wicket.mapper.attachment;

import br.net.mirante.singular.form.type.core.attachment.SIAttachment;
import br.net.mirante.singular.util.wicket.model.IReadOnlyModel;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;

import static br.net.mirante.singular.util.wicket.util.Shortcuts.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

/**
 * Classe de link para utilização em conjunto dom {@link DownloadSupportedBehavior}
 * para disponibilizar links de download de um único uso.
 */
public class DownloadLink extends Link<Void> {

    private IModel<SIAttachment> model;
    private DownloadSupportedBehavior downloadSupportedBehaviour;

    public DownloadLink(String id, IModel<SIAttachment> model, DownloadSupportedBehavior downloadSupportedBehaviour) {
        super(id);
        this.model = model;
        this.downloadSupportedBehaviour = downloadSupportedBehaviour;

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        this.add($b.attr("onclick", (IReadOnlyModel<String>) () -> "DownloadSupportedBehavior.ajaxDownload(" +
                "'" + downloadSupportedBehaviour.getUrl() + "'," +
                "'" + model.getObject().getFileId() + "'," +
                "'" + model.getObject().getFileName() + "'" +
                ");" +
                "return false;"));
        this.setBody($m.property(model, "fileName"));
        add(WicketUtils.$b.attr("title", $m.ofValue(model.getObject().getFileName())));
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();
    }

    @Override
    public void onClick() {
    }


}
