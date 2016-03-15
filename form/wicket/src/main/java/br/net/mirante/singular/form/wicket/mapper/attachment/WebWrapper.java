/*
 * Copyright (c) 2016, Mirante and/or its affiliates. All rights reserved.
 * Mirante PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package br.net.mirante.singular.form.wicket.mapper.attachment;

import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;

/**
 * This class allows to now depend on the interfaces from Wicket to have access
 * to the Request and Response. Thus enabling easier and more testable
 * code.
 *
 * @author Fabricio Buzeto
 */
@Deprecated
public class WebWrapper {
    private WebResponse _response;

    public void setResponse(WebResponse _response) {
        this._response = _response;
    }

    public WebResponse response() {
        return _response != null ? _response : (WebResponse) RequestCycle.get().getResponse();
    }

    private ServletWebRequest _request;

    public void setRequest(ServletWebRequest _request) {
        this._request = _request;
    }

    public ServletWebRequest request() {
        return _request != null ? _request : (ServletWebRequest) RequestCycle.get().getRequest();
    }
}
