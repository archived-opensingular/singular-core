package br.net.mirante.singular.view.page.dashboard;

import static br.net.mirante.singular.view.Behaviors.$b;
import static br.net.mirante.singular.view.Models.$m;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;

import br.net.mirante.singular.dao.FeedDTO;
import br.net.mirante.singular.service.FeedService;
import br.net.mirante.singular.view.template.Content;

@SuppressWarnings("serial")
public class DashboardContent extends Content {
	
	@Inject
	private FeedService feedService;
	
	private ListModel<FeedDTO> feeds = new ListModel<>();

    
	public DashboardContent(String id) {
        super(id);
        
        feedService.retrieveFeed(); // TODO apagar esse metodo quando pronto, está só pra ver como fica
        
        feeds.setObject(feedService.retrieveFeedTemporario()); //TODO colocar funcao real
        add(new RefreshingView<FeedDTO>("atividades", feeds) {
	
			@Override
			protected Iterator<IModel<FeedDTO>> getItemModels() {
				List<IModel<FeedDTO>> models = new ArrayList<>();
				Iterator<FeedDTO> f = feeds.getObject().iterator();
				while (f.hasNext()){
					models.add($m.ofValue(f.next()));
				}
				return models.iterator();
			}

			@Override
			protected void populateItem(Item<FeedDTO> item) {
				final FeedDTO feedDto = item.getModelObject();
				item.queue(new Label("descricao", feedDto.getDescricao() ));
				item.queue(new Label("tempoDeAtraso", feedDto.getTempoAtraso() ));
				
				WebMarkupContainer iconColor = new WebMarkupContainer("feedIconColor");
				iconColor.add($b.classAppender(feedDto.getFeedIconColor().getDescricao()));
				item.queue(iconColor);
				
				WebMarkupContainer icon = new WebMarkupContainer("icon");
				icon.add($b.classAppender(feedDto.getIconSymbol() ));
				item.queue(icon);
			}
        	
		});
    }

    @Override
    protected String getContentTitlelKey() {
        return "label.content.title";
    }

    @Override
    protected String getContentSubtitlelKey() {
        return "label.content.subtitle";
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssReferenceHeaderItem.forUrl("/resources/admin/page/css/page.css"));
        response.render(JavaScriptReferenceHeaderItem.forUrl("/resources/admin/page/scripts/demo.js"));
        response.render(JavaScriptReferenceHeaderItem.forUrl("/resources/admin/page/scripts/page.js"));
        StringBuilder script = new StringBuilder();
        script.append("jQuery(document).ready(function () {\n")
                .append("    Demo.init(); // init demo features\n")
                .append("    Index.init();\n")
                .append("});");
        response.render(OnDomReadyHeaderItem.forScript(script));
    }
}
