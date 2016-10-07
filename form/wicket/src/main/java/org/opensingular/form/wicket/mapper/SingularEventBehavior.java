package org.opensingular.form.wicket.mapper;

import static org.apache.commons.lang3.ObjectUtils.*;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

import org.opensingular.form.wicket.IWicketComponentMapper;
import org.opensingular.lib.wicket.util.jquery.JQuery;

public class SingularEventBehavior extends Behavior {

    private static final Component[] EMPTY = new Component[0];

    private Component   processComponent;
    private Component   validateComponent;
    private Component[] supportComponents;
    private String      validateSourceEvent = "blur";
    private String      processSourceEvent  = "change";

    public SingularEventBehavior() {}
    public SingularEventBehavior(Component component) {
        this(component, component);
    }
    public SingularEventBehavior(Component validateComponent, Component processComponent) {
        setValidateComponent(validateComponent);
        setProcessComponent(processComponent);
    }

    public SingularEventBehavior setProcessEvent(String event, Component source) {
        return setProcessSourceEvent(event).setProcessComponent(source);
    }
    public SingularEventBehavior setValidateEvent(String event, Component source) {
        return setValidateSourceEvent(event).setValidateComponent(source);
    }

    //@formatter:off
    protected Component    getValidateComponent()      { return validateComponent;   }
    protected Component    getProcessComponent()       { return processComponent;    }
    protected Component[]  getSupportComponents()      { return supportComponents;   }
    protected String       getValidateSourceEvent()    { return validateSourceEvent; }
    protected String       getProcessSourceEvent()     { return processSourceEvent;  }
    protected SingularEventBehavior setValidateComponent   (Component      obj) { this.validateComponent   = obj; return this; }
    protected SingularEventBehavior setProcessComponent    (Component      obj) { this.processComponent    = obj; return this; }
    public    SingularEventBehavior setSupportComponents   (Component...   obj) { this.supportComponents   = obj; return this; }
    protected SingularEventBehavior setValidateSourceEvent (String         obj) { this.validateSourceEvent = obj; return this; }
    protected SingularEventBehavior setProcessSourceEvent  (String         obj) { this.processSourceEvent  = obj; return this; }
    //@formatter:on

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        Component valComp = getValidateComponent();
        Component prcComp = getProcessComponent();

        response.render(OnDomReadyHeaderItem.forScript("(function(){'use strict';"
            + String.format(""
                + "\n var $this = %s;"
                + "\n var $validate = %s;"
                + "\n var $process = %s;"
                + "\n var $support = %s;"
                + "\n var sngValEvt = '%s';"
                + "\n var sngPrcEvt = '%s';"
                + "\n var srcValEvt = '%s';"
                + "\n var srcPrcEvt = '%s';"
                + "\n ",
                JQuery.$(component),
                JQuery.$(defaultIfNull(valComp, component)),
                JQuery.$(defaultIfNull(prcComp, component)),
                JQuery.$(defaultIfNull(supportComponents, EMPTY)),
                IWicketComponentMapper.SINGULAR_VALIDATE_EVENT,
                IWicketComponentMapper.SINGULAR_PROCESS_EVENT,
                getValidateSourceEvent(),
                getProcessSourceEvent())
            + "\n var clearVal = function() { clearTimeout($validate.data('sngValEvt')); };"
            + "\n var clearPrc = function() { clearTimeout($process.data('sngPrcEvt')); };"
            + "\n var clearAll = function() { clearVal(); clearPrc(); };"
            + "\n $validate.on(srcValEvt, function(){"
            + "\n   clearVal();"
            + "\n   var handler = setTimeout(function(){ $this.trigger(sngValEvt); }, 50);"
            + "\n   $validate.data('sngValEvt', handler);"
            + "\n });"
            + "\n $process.on(srcPrcEvt, function(){"
            + "\n   clearAll();"
            + "\n   var handler = setTimeout(function(){ $this.trigger(sngPrcEvt); }, 50);"
            + "\n   $process.data('sngPrcEvt', handler);"
            + "\n });"
            + "\n $support"
            + "\n   .on(srcValEvt, function(){ $validate.trigger(srcValEvt); })"
            + "\n   .on('click focus mousedown', function(){ clearAll(); });"
            + "\n $validate"
            + "\n   .on('focus', function(){ clearAll(); });"
            + "\n $process"
            + "\n   .on('focus', function(){ clearAll(); });"
            + "\n "
            + "\n })();\n"));
    }
}
