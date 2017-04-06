package org.opensingular.lib.wicket.util;

import java.util.Optional;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.opensingular.lib.commons.lambda.IPredicate;
import org.opensingular.lib.wicket.util.util.WicketUtils;

public class WicketUtilTester extends WicketTester {

    public WicketUtilTester() {
        super();
    }
    public WicketUtilTester(Class<? extends Page> homePage) {
        super(homePage);
    }
    public WicketUtilTester(WebApplication application, boolean init) {
        super(application, init);
    }
    public WicketUtilTester(WebApplication application, ServletContext servletCtx, boolean init) {
        super(application, servletCtx, init);
    }
    public WicketUtilTester(WebApplication application, ServletContext servletCtx) {
        super(application, servletCtx);
    }
    public WicketUtilTester(WebApplication application, String path) {
        super(application, path);
    }
    public WicketUtilTester(WebApplication application) {
        super(application);
    }

    public static WicketUtilTester withDummyApplication() {
        return new WicketUtilTester(new WicketUtilsDummyApplication());
    }

    public <C extends Component> Optional<C> findChild(Class<C> componentClass, IPredicate<C> filter) {
        return WicketUtils.findFirstChild(getLastRenderedPage(), componentClass, filter);
    }
    public Optional<Component> childById(String id) {
        return findChild(Component.class, it -> id.equals(it.getId()));
    }
    public <C extends Component> Optional<C> childById(Class<C> componentClass, String id) {
        return findChild(componentClass, it -> id.equals(it.getId()));
    }
    
    public boolean assertHtmlContains(String s) {
        return getLastResponseAsString().contains(s);
    }
    public boolean assertHtmlMatches(String regex) {
        return Pattern.matches(regex, getLastResponseAsString());
    }
}
