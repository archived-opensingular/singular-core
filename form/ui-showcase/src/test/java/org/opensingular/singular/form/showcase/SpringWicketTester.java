package org.opensingular.singular.form.showcase;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.wicket.util.tester.WicketTester;
import org.springframework.stereotype.Component;

import org.opensingular.singular.form.showcase.wicket.ShowcaseApplication;

@Component
public class SpringWicketTester {

    @Inject
    private ShowcaseApplication app;

    private WicketTester wt;

    @PostConstruct
    public void init(){
        wt = new WicketTester(app, true);
    }

    public WicketTester wt() {
        return wt;
    }
}
