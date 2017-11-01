/*
 *
 *  * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package org.opensingular.resources.filter;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CachingAndEncodingFilterTest {

    Logger logger = LoggerFactory.getLogger(CachingAndEncodingFilterTest.class);

    @Test
    public void testCache() throws Exception {
        CachingAndEncodingFilter filter   = new CachingAndEncodingFilter();
        HttpServletRequest  request  = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain         chain    = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain, new org.mockito.internal.verification.Times(1)).doFilter(request, response);

        ArgumentCaptor<String> name  = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> value = ArgumentCaptor.forClass(String.class);

        verify(response).setHeader(name.capture(), value.capture());

        Assert.assertEquals(CachingAndEncodingFilter.CACHE_CONTROL, name.getValue());
        Assert.assertTrue(value.getValue().startsWith("max-age="));

        logger.info(value.getValue());

    }
}
