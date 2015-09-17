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

import br.net.mirante.singular.flow.core.dto.IFeedDTO;
import br.net.mirante.singular.service.FeedService;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;
import static br.net.mirante.singular.util.wicket.util.WicketUtils.$m;

public class FeedPanel extends Panel {

    @Inject
    private FeedService feedService;

    private ListModel<IFeedDTO> feeds = new ListModel<>();

    public FeedPanel(String id) {
        super(id);
    }

    private class FeedItem {
        public String icon;
        public String color;
        public String msg;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        initFeeds();
    }

    private void initFeeds() {
        feeds.setObject(feedService.retrieveFeed());
        add(new RefreshingView<IFeedDTO>("atividades", feeds) {
            @Override
            protected Iterator<IModel<IFeedDTO>> getItemModels() {
                List<IModel<IFeedDTO>> models = new ArrayList<>();
                for (IFeedDTO feedDTO : feeds.getObject()) {
                    models.add($m.ofValue(feedDTO));
                }
                return models.iterator();
            }

            @Override
            protected void populateItem(Item<IFeedDTO> item) {
                final IFeedDTO feedDTO = item.getModelObject();
                FeedItem fi = getFeedItem(feedDTO);

                item.queue(new Label("descricao", getDesc(feedDTO)));
                item.queue(new Label("tempoDeAtraso", getTimeDesc(feedDTO)));

                WebMarkupContainer iconColor = new WebMarkupContainer("feedIconColor");
                iconColor.add($b.classAppender(fi.color));

                iconColor.add($b.classAppender(" tooltips "));
                iconColor.add($b.attr("data-placement", "left"));
                iconColor.add($b.attr("data-container", "body"));
                iconColor.add($b.attr("data-original-title", fi.msg));

                item.queue(iconColor);

                WebMarkupContainer icon = new WebMarkupContainer("icon");
                icon.add($b.classAppender(fi.icon));

                iconColor.queue(icon);
            }
        });
    }

    private FeedItem getFeedItem(IFeedDTO feed) {
        FeedItem fi = new FeedItem();
        fi.color = "label-default ";
        fi.icon = "fa fa-clock-o ";
        fi.msg = "até 50% ";
        if (feed.getMedia().multiply(BigDecimal.valueOf(1.5)).compareTo(feed.getTempoDecorrido()) < 0) {
            fi.icon = "fa fa-exclamation-triangle ";
            fi.color = " bg-yellow-lemon ";
            fi.msg = "até 100% ";
        }
        if (feed.getMedia().multiply(BigDecimal.valueOf(2)).compareTo(feed.getTempoDecorrido()) < 0) {
            fi.icon = "fa fa-ambulance ";
            fi.color = " label-danger ";
            fi.msg = " 100% ou + ";
        }
        return fi;
    }

    private String getDesc(IFeedDTO feed) {
        return "[" + feed.getNomeProcesso() + "] " + (feed.getDescricaoInstancia() != null
                ? feed.getDescricaoInstancia() : "N/A");
    }

    private String getTimeDesc(IFeedDTO feed) {
        return String.format(" + %s dias ", feed.getTempoDecorrido().subtract(feed.getMedia()));
    }
}
