package br.net.mirante.singular.util.wicket.bootstrap.layout;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;

public class BSCol extends BSContainer<BSCol> implements IBSGridCol<BSCol> {

    public BSCol(String id) {
        super(id);
        add(newBSGridColBehavior());
    }

    public BSCol appendGrid(IBSComponentFactory<BSGrid> factory) {
        return super.appendComponent(factory);
    }

    public BSControls newFormGroup() {
        return super.newComponent(new IBSComponentFactory<BSControls>() {
            @Override
            public BSControls newComponent(String componentId) {
                BSControls controls = new BSControls(componentId, false)
                    .setCssClass("form-group");
                controls.add(new AttributeAppender("class", "can-have-error", " "));
                return controls;
            }
        });
    }

    @Override
    public BSCol add(Behavior... behaviors) {
        return (BSCol) super.add(behaviors);
    }
}
