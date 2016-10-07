/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.lib.wicket.util.datatable.column;

import org.opensingular.lib.commons.lambda.IFunction;
import org.opensingular.lib.wicket.util.bootstrap.layout.BSContainer;
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
