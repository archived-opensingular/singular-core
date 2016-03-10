package br.net.mirante.singular.util.wicket.bootstrap.layout;

import org.apache.wicket.behavior.Behavior;

import br.net.mirante.singular.util.wicket.util.WicketUtils;

public class BSCol extends BSContainer<BSCol> implements IBSGridCol<BSCol> {

    public BSCol(String id) {
        super(id);
        add(newBSGridColBehavior());
        /**
         * Configuração temporaria para correção do problema de grid ( espaço em branco ao lado do campo),
         * essa config padroniza o tamanho minimo dos componentes inseridos,
         * evitando tamanhos aleatorios para componentes simples.
         */
        add(WicketUtils.$b.attrAppender("style", "min-height: 75px", ";"));
    }

    @Override
    public BSCol add(Behavior... behaviors) {
        return (BSCol) super.add(behaviors);
    }
}
