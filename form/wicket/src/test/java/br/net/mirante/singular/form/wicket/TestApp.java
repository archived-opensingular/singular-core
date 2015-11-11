package br.net.mirante.singular.form.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

public class TestApp extends WebApplication {

    @Override
    public Class<? extends Page> getHomePage() {
	return TestPage.class;
    }
}
