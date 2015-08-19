package br.net.mirante.singular.util.wicket.bootstrap.layout;

import java.io.Serializable;

import org.apache.wicket.Component;

/**
 * Interface funcional para a criação de um componente com um ID determinado.
 */
public interface IBSComponentFactory<C extends Component> extends Serializable {
    C newComponent(String componentId);
}
