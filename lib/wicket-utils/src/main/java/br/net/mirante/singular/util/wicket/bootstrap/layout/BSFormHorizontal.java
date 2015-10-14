package br.net.mirante.singular.util.wicket.bootstrap.layout;

import static br.net.mirante.singular.util.wicket.util.WicketUtils.$b;

import java.io.Serializable;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;

import br.net.mirante.singular.util.wicket.bootstrap.layout.IBSGridCol.BSGridSize;

/**
<p>Componente para gerar formularios horizontais do bootstrap de forma programática (sem ter que escrever HTML).
Este componente possui facilidades para a criação de componentes, com métodos para a criação de inputs, selects,
textareas, etc., com suporte para feedback de erro abaixo do campo, labels nos componentes e marcador de 
obrigatoriedade.</p>

<p>Ele utiliza uma grid unificada de 12 colunas, ao invés de grid dentro de grid. Isto evita o cálculo de divisores
comuns para alinhar as labels, porém restringe o layout a 12 colunas. Se necessário, é possível adicionar um
BSFormHorizontal dentro de um BSGrid, para criar um layout grid dentro de grid.</p>

<p>Os métodos desta API que geram containers (groups, labels, controls) podem ser utilizados de duas formas:</p>
<ul>
  <li>os métodos prefixados com new- (newGroup(), newLabel(), newControls()) <u>retornam</u> a instância do container
    criado, permitindo um uso mais 'procedural' da API.</li>
  <li>os métodos prefixados com append- (appendGroup(), appendLabel(), appendControls()) <u>recebem</u> callbacks que
    consomem o container criado (ou um componente pré-inicializado), mas retornam 'this' (o container no qual o método
    foi chamado), para encadeamento, permitindo um uso mais 'declarativo' da API.</li>
</ul>

<p>A forma procedural resulta em um código mais natural se você precisa guardar referências para os containers,
por exemplo, para ter controle fino da renderização de respostas Ajax.</p>

<pre>
    // TODO
</pre>

<p>A forma declarativa resulta em um código hierarquizado, semelhante à estrutura do HTML que será gerada:
<pre>
    // TODO
</pre>

Ambos os códigos acima geram algo do tipo:
<pre>
    // TODO
</pre>

<p>Ambos os estilos são válidos, e podem ser misturados de acordo com a necessidade. </p>
 */
public class BSFormHorizontal extends BSContainer<BSFormHorizontal> {

    private BSGridSize defaultGridSize = BSGridSize.MD;

    public BSFormHorizontal(String id, IModel<?> model) {
        this(id);
        setDefaultModel(model);
    }

    public BSFormHorizontal(String id) {
        super(id);
        add($b.classAppender("form-horizontal"));
    }

    public BSGridSize getDefaultGridSize() {
        return defaultGridSize;
    }
    public BSFormHorizontal setDefaultGridSize(BSGridSize defaultGridSize) {
        this.defaultGridSize = defaultGridSize;
        return this;
    }

    public BSFormHorizontal appendGroup(IBSComponentFactory<BSFormGroup> factory) {
        newComponent(factory)
            .setDefaultGridSize(getDefaultGridSize());
        return this;
    }

    public BSFormHorizontal appendGroupLabelControlsFeedback(int labelColspan, Component labelFor, Serializable labelValueOrModel, int controlsColspan, IBSComponentFactory<BSControls> factory) {
        newGroup()
            .appendLabel(labelColspan, labelFor, labelValueOrModel)
            .newControls(controlsColspan, factory)
            .appendFeedback();
        return this;
    }

    public BSFormHorizontal appendGroupLabelControlsFeedback(int labelColspan, Serializable labelValueOrModel, int controlsColspan, IBSComponentFactory<BSControls> factory) {
        newGroup()
            .appendLabel(labelColspan, labelValueOrModel)
            .newControls(controlsColspan, factory)
            .appendFeedback();
        return this;
    }

    public BSFormHorizontal appendGroupLabelControls(int labelColspan, Serializable labelValueOrModel, int controlsColspan, IBSComponentFactory<BSControls> factory) {
        newGroup()
            .appendLabel(labelColspan, labelValueOrModel)
            .newControls(controlsColspan, factory);
        return this;
    }

    public BSFormHorizontal appendGroupOffsetControls(int mdOffset, int controlsColspan, IBSComponentFactory<BSControls> factory) {
        newGroup()
            .newControls(controlsColspan, factory)
            .mdOffset(mdOffset);
        return this;
    }

    public BSFormGroup newGroup() {
        return super.newComponent(id -> new BSFormGroup(id, getDefaultGridSize()));
    }

    public BSControls newControlsInGroup(int labelColspan, Serializable labelValueOrModel, int controlsColspan) {
        return newGroup()
            .appendLabel(labelColspan, labelValueOrModel)
            .newControls(controlsColspan);
    }

    public BSControls newControlsInGroup(int mdOffset, int controlsColspan) {
        return newGroup()
            .newControls(controlsColspan)
            .mdOffset(mdOffset);
    }

    @Override
    public BSFormHorizontal add(Behavior... behaviors) {
        return (BSFormHorizontal) super.add(behaviors);
    }

}
