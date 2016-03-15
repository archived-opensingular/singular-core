/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.util.wicket.datatable.column;

import br.net.mirante.singular.commons.lambda.IFunction;
import br.net.mirante.singular.util.wicket.bootstrap.layout.BSContainer;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import java.io.Serializable;

public class MetronicStatusColumn<T, S> extends BSPropertyColumn<T, S> {

    public static enum BagdeType {
        WARNING,
        DANGER,
        SUCCESS,
        INFO,
        NONE
    }

    private BadgeTypeMapper<T> badgeTypeMapper;

    public MetronicStatusColumn(IModel<String> displayModel, IFunction<T, Object> propertyFunction) {
        super(displayModel, propertyFunction);
    }

    public MetronicStatusColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    public MetronicStatusColumn(IModel<String> displayModel, S sortProperty, IFunction<T, Object> propertyFunction) {
        super(displayModel, sortProperty, propertyFunction);
    }

    public MetronicStatusColumn(IModel<String> displayModel, S sortProperty, String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }


    public MetronicStatusColumn(IModel<String> displayModel, IFunction<T, Object> propertyFunction, BadgeTypeMapper<T> badgeTypeMapper) {
        super(displayModel, propertyFunction);
        this.badgeTypeMapper = badgeTypeMapper;
    }

    public MetronicStatusColumn(IModel<String> displayModel, String propertyExpression, BadgeTypeMapper<T> badgeTypeMapper) {
        super(displayModel, propertyExpression);
        this.badgeTypeMapper = badgeTypeMapper;
    }

    public MetronicStatusColumn(IModel<String> displayModel, S sortProperty, IFunction<T, Object> propertyFunction, BadgeTypeMapper<T> badgeTypeMapper) {
        super(displayModel, sortProperty, propertyFunction);
        this.badgeTypeMapper = badgeTypeMapper;
    }

    public MetronicStatusColumn(IModel<String> displayModel, S sortProperty, String propertyExpression, BadgeTypeMapper<T> badgeTypeMapper) {
        super(displayModel, sortProperty, propertyExpression);
        this.badgeTypeMapper = badgeTypeMapper;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        BSContainer container = new BSContainer<>(componentId);
        container.setOutputMarkupPlaceholderTag(false);
        IModel<Object> cellModel = getDataModel(rowModel);
        container.appendTag("span", true, getBadgeCssClassAttribute(cellModel, rowModel), new Label("label", cellModel));
        cellItem.add(container);
    }

    private String getBadgeCssClassAttribute(IModel<Object> cellModel, IModel<T> rowModel) {
        String css = "class=\"label label-sm ";
        BagdeType type = BagdeType.NONE;
        if (badgeTypeMapper != null) {
            type = badgeTypeMapper.getType(cellModel, rowModel);
        }
        if (type == BagdeType.NONE) {
            return css + " \" style=\"background-color: #889988;\" ";
        } else {

            return css + " label-" + type.name().toLowerCase() + "\"";
        }
    }

    @FunctionalInterface
    public static interface BadgeTypeMapper<T> extends Serializable {

        MetronicStatusColumn.BagdeType getType(IModel<Object> cellModel, IModel<T> rowModel);

    }
}
