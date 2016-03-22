package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.mform.options.SOptionsConfig;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.string.StringValue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by nuk on 22/03/16.
 */
public class BloodhoundDataBehaviorTest {

    private WebRequest request;
    private WebResponse response;
    @Captor ArgumentCaptor<String> captor;


    @Before public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test public void setsEncoding(){
        BloodhoundDataBehavior b = createBehavior(null, null);
        b.respond(null);

        verify(response).setHeader("Content-Type", "text/html; charset=utf8");
    }

    @Test public void returnOptions(){

        final String[] DOMAINS = {"@gmail.com", "@hotmail.com", "@yahoo.com"};
        STypeComposite<? extends SIComposite> baseType = SDictionary.create().createNewPackage("pkg").createCompositeType("basetype");
        STypeString base = baseType.addFieldString("myHero");
        base.withSelectionFromProvider(new SOptionsProvider() {
            @Override
            public SIList<? extends SInstance> listOptions(SInstance instance) {
                SIList<?> r = instance.getDictionary().getType(STypeString.class).newList();
                for(String d : DOMAINS){
                    r.addNew().setValue(d);
                }
                return r;
            }
        });
        base.withView(new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC));

        SInstance instance = SDocumentFactory.empty().createInstance(new RefType() {
            protected SType<?> retrieve() {
                return base;
            }
        });

        BloodhoundDataBehavior b = createBehavior(null, instance.getOptionsConfig());
        b.respond(null);

//        verify(response).write("[{\"value\":\"Abacate\"}]");
        verify(response).write(captor.capture());
        JSONArray expected = new JSONArray();
        JSONObject value = new JSONObject();
        value.put("value","Abacate");
        expected.put(value);
        assertThat(new JSONArray(captor.getValue())).isEqualTo(expected);
    }

    private BloodhoundDataBehavior createBehavior(final StringValue filter,
                                                  SOptionsConfig optionsProvider) {
        return new BloodhoundDataBehavior(optionsProvider){
                @Override
                protected RequestCycle requestCycle() {
                    RequestCycle cycle = Mockito.mock(RequestCycle.class);

                    request = Mockito.mock(WebRequest.class);
                    Mockito.when(cycle.getRequest()).thenReturn(request);

                    IRequestParameters params = Mockito.mock(IRequestParameters.class);
                    Mockito.when(request.getRequestParameters()).thenReturn(params);

                    Mockito.when(params.getParameterValue("filter")).thenReturn(filter);

                    response = Mockito.mock(WebResponse.class);
                    Mockito.when(cycle.getResponse()).thenReturn(response);

                    return cycle;
                }
            };
    }
}
