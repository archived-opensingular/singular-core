package br.net.mirante.singular.view.page.dashboard;

import br.net.mirante.singular.dao.FeedDTO;
import br.net.mirante.singular.service.FeedService;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        feedService.retrieveFeed(); // TODO apagar esse metodo quando pronto, está só pra ver como fica
        feeds.setObject(feedService.retrieveFeedTemporario()); //TODO colocar funcao real
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
                item.queue(new Label("descricao", feedDto.getDescricao()));
                item.queue(new Label("tempoDeAtraso", feedDto.getTempoAtraso()));

                WebMarkupContainer iconColor = new WebMarkupContainer("feedIconColor");
                iconColor.add($b.classAppender(feedDto.getFeedIconColor().getDescricao()));
                item.queue(iconColor);

                WebMarkupContainer icon = new WebMarkupContainer("icon");
                icon.add($b.classAppender(feedDto.getIconSymbol()));
                item.queue(icon);
            }
        });
    }
}
