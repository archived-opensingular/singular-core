package br.net.mirante.singular.util.wicket.util;

import br.net.mirante.singular.util.wicket.lambda.IConsumer;
import org.apache.wicket.Component;

public interface IOnAfterPopulateItemConfigurable {

    IOnAfterPopulateItemConfigurable setOnAfterPopulateItem(IConsumer<Component> onAfterPopulateItem);
}
