/*
 * Copyright (C) 2016 Singular Studios (a.k.a Atom Tecnologia) - www.opensingular.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensingular.resources.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;

@WebFilter(urlPatterns = "*")
public class CachingAndEncodingFilter implements Filter {

    public static final String       CACHE_CONTROL   = "Cache-Control";
    public static final String       MAX_AGE_PATTERN = "max-age=%d";
    public static final long         THIRTY_DAYS     = 86400L * 30; // 30 days in seconds
    public static final long         TWELVE_HOURS    = 86400L / 2; // 12 hours in seconds
    public static final SecureRandom RANDOM          = new SecureRandom(SecureRandom.getSeed(4));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setHeader(CACHE_CONTROL, String.format(MAX_AGE_PATTERN, THIRTY_DAYS + RANDOM.longs(0, TWELVE_HOURS).findFirst().orElse(0L)));
        chain.doFilter(request, httpServletResponse);
    }

    @Override
    public void destroy() {
    }

}