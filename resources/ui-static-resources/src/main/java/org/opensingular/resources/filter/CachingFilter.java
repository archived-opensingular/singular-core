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
public class CachingFilter implements Filter {

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
        httpServletResponse.setHeader(CACHE_CONTROL, String.format(MAX_AGE_PATTERN, THIRTY_DAYS + RANDOM.longs(0, TWELVE_HOURS).findFirst().orElse(0L)));
        chain.doFilter(request, httpServletResponse);
    }

    @Override
    public void destroy() {
    }

}