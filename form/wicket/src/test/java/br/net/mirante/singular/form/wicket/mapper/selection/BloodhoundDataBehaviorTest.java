package br.net.mirante.singular.form.wicket.mapper.selection;

import br.net.mirante.singular.form.mform.*;
import br.net.mirante.singular.form.mform.basic.view.SViewAutoComplete;
import br.net.mirante.singular.form.mform.core.STypeString;
import br.net.mirante.singular.form.mform.document.RefType;
import br.net.mirante.singular.form.mform.document.SDocumentFactory;
import br.net.mirante.singular.form.mform.options.SOptionsConfig;
import br.net.mirante.singular.form.mform.options.SOptionsProvider;
import br.net.mirante.singular.form.wicket.model.MInstanceRootModel;
import br.net.mirante.singular.util.wicket.util.WicketUtils;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebRequest;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.util.string.StringValue;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BloodhoundDataBehaviorTest {

    private WebRequest request;
    private WebResponse response;
    @Captor ArgumentCaptor<String> captor;
    private RequestCycle cycle;


    @Before public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test public void setsEncoding(){
        SInstance instance = createInstance(createBaseType());
        BloodhoundDataBehavior b = createBehavior(null, new MInstanceRootModel<>(instance));
        b.respond(null);

        verify(response).setHeader("Content-Type", "application/json; charset=utf8");
    }

    @Test public void specifyThatItWillHandleTheResponseByItself(){
        SInstance instance = createInstance(createBaseType());
        BloodhoundDataBehavior b = createBehavior(null, new MInstanceRootModel<>(instance));
        b.respond(null);

        verify(cycle).scheduleRequestHandlerAfterCurrent(null);
    }

    @Test public void returnOptions(){
        SInstance instance = createInstance(createBaseType());

        BloodhoundDataBehavior b = createBehavior(null, new MInstanceRootModel<>(instance));
        b.respond(null);

        verify(response).write(captor.capture());
        JSONArray expected = new JSONArray();
        expected.put(createValue("@gmail.com"));
        expected.put(createValue("@hotmail.com"));
        expected.put(createValue("@yahoo.com"));

        JSONAssert.assertEquals(expected,new JSONArray(captor.getValue()),false);
    }

    @Test public void applyFilterToOptions(){
        SInstance instance = createInstance(createBaseType());

        BloodhoundDataBehavior b = createBehavior("bruce", new MInstanceRootModel<>(instance));
        b.respond(null);

        verify(response).write(captor.capture());
        JSONArray expected = new JSONArray();
        expected.put(createValue("bruce@gmail.com"));
        expected.put(createValue("bruce@hotmail.com"));
        expected.put(createValue("bruce@yahoo.com"));

        JSONAssert.assertEquals(expected,new JSONArray(captor.getValue()),false);
    }

    private STypeString createBaseType() {
        STypeComposite<? extends SIComposite> baseType = SDictionary.create().createNewPackage("pkg").createCompositeType("basetype");
        STypeString base = baseType.addFieldString("myHero");
        base.withSelectionFromProvider(createProvider());
        base.withView(new SViewAutoComplete(SViewAutoComplete.Mode.DYNAMIC));
        return base;
    }

    private SOptionsProvider createProvider() {
        return new SOptionsProvider() {
            @Override
            public SIList<? extends SInstance> listOptions(SInstance instance, String filter) {
                if(filter == null) filter = "";
                SIList<?> r = instance.getType().newList();
                r.addNew().setValue(filter+"@gmail.com");
                r.addNew().setValue(filter+"@hotmail.com");
                r.addNew().setValue(filter+"@yahoo.com");
                return r;
            }
        };
    }

    private SInstance createInstance(final STypeString base) {
        return SDocumentFactory.empty().createInstance(new RefType() {
            protected SType<?> retrieve() {
                return base;
            }
        });
    }

    private JSONObject createValue(String v) {
        JSONObject value = new JSONObject();
        value.put("value", v);
        return value;
    }

    private BloodhoundDataBehavior createBehavior(final String filter,
                                                  IModel model) {
        cycle = Mockito.mock(RequestCycle.class);

        request = Mockito.mock(WebRequest.class);
        Mockito.when(cycle.getRequest()).thenReturn(request);

        IRequestParameters params = Mockito.mock(IRequestParameters.class);
        Mockito.when(request.getRequestParameters()).thenReturn(params);

        Mockito.when(params.getParameterValue("filter")).thenReturn(StringValue.valueOf(filter));

        response = Mockito.mock(WebResponse.class);
        Mockito.when(cycle.getResponse()).thenReturn(response);
        return new BloodhoundDataBehavior(new MOptionsModel(model)){
                @Override
                protected RequestCycle requestCycle() {
                    return cycle;
                }
            };
    }
}
