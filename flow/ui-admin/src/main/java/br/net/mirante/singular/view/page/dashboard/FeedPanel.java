package br.net.mirante.singular.view.page.dashboard;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;

import br.net.mirante.singular.dao.FeedDTO;
import br.net.mirante.singular.service.FeedService;

import static br.net.mirante.singular.view.Behaviors.$b;
import static br.net.mirante.singular.view.Models.$m;

public class FeedPanel extends Panel {

    @Inject
    private FeedService feedService;

    private ListModel<FeedDTO> feeds = new ListModel<>();

    public FeedPanel(String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        initFeeds();
    }

    private void initFeeds() {
        feeds.setObject(feedService.retrieveFeed());
        add(new RefreshingView<FeedDTO>("atividades", feeds) {
            @Override
            protected Iterator<IModel<FeedDTO>> getItemModels() {
                List<IModel<FeedDTO>> models = new ArrayList<>();
                for (FeedDTO feedDTO : feeds.getObject()) {
                    models.add($m.ofValue(feedDTO));
                }
                return models.iterator();
            }

            @Override
            protected void populateItem(Item<FeedDTO> item) {
                final FeedDTO feedDto = item.getModelObject();
                item.queue(new Label("descricao", getDesc(feedDto)));
                item.queue(new Label("tempoDeAtraso", getTimeDesc(feedDto)));

                WebMarkupContainer iconColor = new WebMarkupContainer("feedIconColor");
                iconColor.add($b.classAppender(getIconColor(feedDto)));
                item.queue(iconColor);

                WebMarkupContainer icon = new WebMarkupContainer("icon");
                icon.add($b.classAppender(getIcon(feedDto)));
                iconColor.add(icon);
            }
        });
    }

    private String getDesc(FeedDTO feed) {
        return "[" + feed.getNomeProcesso() + "] " + feed.getDescricaoInstancia();
    }

    private String getIcon(FeedDTO feed) {
        String icon = "fa fa-clock-o ";
        if (feed.getMedia().multiply(BigDecimal.valueOf(1.3)).compareTo(feed.getTempoDecorrido()) < 0) {
            icon = "fa fa-exclamation-triangle ";
        }
        if (feed.getMedia().multiply(BigDecimal.valueOf(2)).compareTo(feed.getTempoDecorrido()) < 0) {
            icon = "fa fa-ambulance ";
        }
        return icon;
    }

    private String getIconColor(FeedDTO feed) {
        String icon = "label-primary ";
        if (feed.getMedia().multiply(BigDecimal.valueOf(1.3)).compareTo(feed.getTempoDecorrido()) < 0) {
            icon = " bg-yellow-lemon ";
        }
        if (feed.getMedia().multiply(BigDecimal.valueOf(2)).compareTo(feed.getTempoDecorrido()) < 0) {
            icon = " label-danger ";
        }
        return icon;
    }

    private String getTimeDesc(FeedDTO feed) {
        return String.format(" %s dias", feed.getTempoDecorrido().subtract(feed.getMedia()));
    }
}
