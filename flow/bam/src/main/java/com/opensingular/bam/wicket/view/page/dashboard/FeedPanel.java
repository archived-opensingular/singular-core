/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.opensingular.bam.wicket.view.page.dashboard;

import javax.inject.Inject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.opensingular.bam.service.UIAdminFacade;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RefreshingView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.util.ListModel;

import com.opensingular.bam.dto.FeedDTO;
import org.opensingular.flow.core.dto.IFeedDTO;
import org.opensingular.singular.util.wicket.behavior.SlimScrollBehaviour;
import static org.opensingular.singular.util.wicket.util.WicketUtils.$b;
import static org.opensingular.singular.util.wicket.util.WicketUtils.$m;

public class FeedPanel extends Panel {

    @Inject
    private UIAdminFacade uiAdminFacade;

    private String processCode;

    private ListModel<FeedDTO> feeds = new ListModel<>();

    private Set<String> processCodeWithAccess;

    public FeedPanel(String id, String processCode, Set<String> processCodeWithAccess) {
        super(id);
        this.processCode = processCode;
        this.processCodeWithAccess = processCodeWithAccess;
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
        feeds.setObject(uiAdminFacade.retrieveAllFeed(processCode, processCodeWithAccess));
        queue(new WebMarkupContainer("instancesContent")
                .add(new RefreshingView<IFeedDTO>("atividades", feeds) {
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
                })
                .add(new SlimScrollBehaviour())
                .setVisible(!feeds.getObject().isEmpty()));
        queue(new WebMarkupContainer("instancesEmptyMessage").setVisible(feeds.getObject().isEmpty()));
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
        if (processCode == null) {
            return "[" + feed.getNomeProcesso() + "] " + (feed.getDescricaoInstancia() != null
                    ? feed.getDescricaoInstancia() : "N/A");
        } else {
            return (feed.getDescricaoInstancia() != null
                    ? feed.getDescricaoInstancia() : "N/A");
        }
    }

    private String getTimeDesc(IFeedDTO feed) {
        return String.format(" + %s dias ", feed.getTempoDecorrido().subtract(feed.getMedia()));
    }

    @Override
    public boolean isVisible() {
        return !feeds.getObject().isEmpty();
    }
}
