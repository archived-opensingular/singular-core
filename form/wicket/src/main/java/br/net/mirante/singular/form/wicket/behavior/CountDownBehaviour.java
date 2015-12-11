package br.net.mirante.singular.form.wicket.behavior;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

public class CountDownBehaviour extends Behavior {

    private Integer max;

    public CountDownBehaviour(Integer max) {
        this.max = max;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {

        String js = "";

        js += " (function(id, max) {                               " ;
        js += "      var parent = $(id).parent(),                  " ;
        js += "          count = document.createElement('h6'),     " ;
        js += "          updateContent = function() {              " ;
        js += "             var output = max - $(id).val().length; " ;
        js += "             $(count).html(output);                 " ;
        js += "          };                                        " ;
        js += "      updateContent();                              " ;
        js += "      parent.append(count);                         " ;
        js += "      $(count).addClass('pull-right');              " ;
        js += "      $(id).on('keyup', updateContent);             " ;
        js += " })('#%s', %d);                                     " ;

        String formatedJs = String.format(js, component.getMarkupId(true), max);
        response.render(OnDomReadyHeaderItem.forScript(formatedJs));
        super.renderHead(component, response);
    }

}
