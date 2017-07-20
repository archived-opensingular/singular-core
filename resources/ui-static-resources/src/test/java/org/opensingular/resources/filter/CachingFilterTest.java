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

public class CachingFilterTest {

    Logger logger = LoggerFactory.getLogger(CachingFilterTest.class);

    @Test
    public void testCache() throws Exception {
        CachingFilter       filter   = new CachingFilter();
        HttpServletRequest  request  = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain         chain    = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain, new org.mockito.internal.verification.Times(1)).doFilter(request, response);

        ArgumentCaptor<String> name  = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> value = ArgumentCaptor.forClass(String.class);

        verify(response).setHeader(name.capture(), value.capture());

        Assert.assertEquals(CachingFilter.CACHE_CONTROL, name.getValue());
        Assert.assertTrue(value.getValue().startsWith("max-age="));

        logger.info(value.getValue());

    }
}
