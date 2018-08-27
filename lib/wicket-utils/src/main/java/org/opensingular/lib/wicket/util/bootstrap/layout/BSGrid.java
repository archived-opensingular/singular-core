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

package org.opensingular.lib.wicket.util.bootstrap.layout;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;

import org.opensingular.lib.commons.lambda.IBiFunction;

/**
 * <p>Componente para gerar grids do bootstrap de forma programática (sem ter que escrever HTML). Este componente é bem leve,
 * sem comportamentos extras. Ele foi criado para quando você quiser utilizar uma grid para fazer algum layout
 * um pouco mais refinado, pois você consegue ter as referências para todas as tags geradas (podendo adicionar CSS
 * específico em cada uma, por exemplo).</p>
 * <p>
 * <p>Os métodos desta API que geram containers (grids, rows, cols) podem ser utilizados de duas formas:</p>
 * <ul>
 * <li>os métodos prefixados com new- (newGrid(), newRow(), newCol()) <u>retornam</u> a instância do container criado,
 * permitindo um uso mais 'procedural' da API.</li>
 * <li>os métodos não prefixados (grid(), row(), col()) <u>recebem</u> callbacks que consomem o container criado,
 * mas retornam 'this' (o container no qual o método foi chamado), para encadeamento, permitindo um uso mais
 * 'declarativo' da API.</li>
 * </ul>
 * <p>
 * <p>A forma procedural resulta em um código mais natural se você precisa guardar referências para os containers,
 * por exemplo, para ter controle fino da renderização de respostas Ajax.</p>
 * <p>
 * <pre>
 * BSGrid grid = new BSGrid("grid");
 * grid.setAddContainerFluidClass(true);
 *
 * BSRow row = grid.newRow();
 * row.newCol(2)
 * .tag("span", new Label("span1", "[col-md-2]"));
 * row.newCol(4)
 * .tag("span", new Label("span2", "[col-md-4]"));
 *
 * BSCol col3 = row.newCol();
 * col3.md(2).sm(4);
 * col3.tag("span", new Label("span3", "[col-md-2 col-sm-4]"));
 *
 * BSCol col4 = row.newCol();
 * col4.md(4).sm(8);
 * col4.tag("span", new Label("span4", "[col-md-4 col-sm-8]"));
 * </pre>
 * <p>
 * <p>A forma declarativa resulta em um código hierarquizado, semelhante à estrutura do HTML que será gerada:
 * <pre>
 * new BSGrid("grid").setAddContainerFluidClass(true)
 * .row(r -> r
 * .col(2, "span", new Label("span1", "[col-md-2]"))
 * .col(4, "span", new Label("span2", "[col-md-4]"))
 * .col(2, c -> c
 * .sm(4)
 * .tag("span", new Label("span3", "[col-md-2 col-sm-4]"))
 * )
 * .col(4, c -> {
 * c.sm(8);
 * c.tag("span", new Label("span4", "[col-md-4 col-sm-8]"));
 * })
 * )
 * </pre>
 * <p>
 * Ambos os códigos acima geram algo do tipo:
 * <pre>
 * &lt;div class="container-fluid">
 * &lt;div class="row">
 * &lt;div class="col-md-2">&lt;span>[col-md-2]&lt;/span>&lt;/div>
 * &lt;div class="col-md-4">&lt;span>[col-md-4]&lt;/span>&lt;/div>
 * &lt;div class="col-md-2 col-sm-4">&lt;span>[col-md-2 col-sm-4]&lt;/span>&lt;/div>
 * &lt;div class="col-md-4 col-sm-8">&lt;span>[col-md-4 col-sm-8]&lt;/span>&lt;/div>
 * &lt;/div>
 * &lt;/div>
 * </pre>
 * <p>
 * <p>Ambos os estilos são válidos, e podem ser misturados de acordo com a necessidade. </p>
 */
public class BSGrid extends BSContainer<BSGrid> {

    public static final int MAX_COLS = IBSGridCol.MAX_COLS;

    private static final AttributeModifier CONTAINER_BEHAVIOR       = new AttributeAppender("class", "container", " ");
    private static final AttributeModifier CONTAINER_FLUID_BEHAVIOR = new AttributeAppender("class", "container-fluid", " ");

    private IBSGridCol.BSGridSize defaultGridSize = IBSGridCol.BSGridSize.MD;

    public BSGrid(String id) {
        super(id);
    }

    public BSGrid(String id, IModel<?> model) {
        super(id, model);
    }

    public IBSGridCol.BSGridSize getDefaultGridSize() {
        return defaultGridSize;
    }

    public BSGrid setDefaultGridSize(IBSGridCol.BSGridSize defaultGridSize) {
        this.defaultGridSize = defaultGridSize;
        return this;
    }

    public BSRow newRow() {
        return newRow(BSRow::new);
    }

    public <R extends BSRow> R newRow(IBiFunction<String, IBSGridCol.BSGridSize, R> factory) {
        R comp = factory.apply(newChildId(), getDefaultGridSize());
        getItems().add(comp);
        return comp;
    }

    public BSGrid appendRow(IBSComponentFactory<BSRow> factory) {
        newComponent(factory).setDefaultGridSize(getDefaultGridSize());
        return this;
    }

    public BSGrid appendRow1Col(IBSComponentFactory<BSCol> factory) {
        newRow().appendCol(BSCol.MAX_COLS, factory);
        return this;
    }

    public BSCol newColInRow() {
        return newColInRow(BSCol.MAX_COLS);
    }

    public BSCol newColInRow(int colspan) {
        return newRow()
                .newCol(colspan);
    }

    public BSGrid setAddContainerClass(boolean add) {
        add(CONTAINER_BEHAVIOR, CONTAINER_FLUID_BEHAVIOR);
        remove(CONTAINER_BEHAVIOR, CONTAINER_FLUID_BEHAVIOR);
        if (add)
            add(CONTAINER_BEHAVIOR);
        return this;
    }

    public BSGrid setAddContainerFluidClass(boolean add) {
        add(CONTAINER_BEHAVIOR, CONTAINER_FLUID_BEHAVIOR);
        remove(CONTAINER_BEHAVIOR, CONTAINER_FLUID_BEHAVIOR);
        if (add)
            add(CONTAINER_FLUID_BEHAVIOR);
        return this;
    }

    @Override
    public BSGrid add(Behavior... behaviors) {
        return (BSGrid) super.add(behaviors);
    }
}
