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
import java.util.Date;
import java.util.Random;

@WebFilter(urlPatterns = "*")
public class CachingFilter implements Filter {

    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String MAX_AGE_PATTERN = "max-age=%d";
    public static       long   THIRTY_DAYS     = 86400 * 30; // 30 days in seconds
    public static       long   TWELVE_HOURS    = 86400 / 2; // 12 hours in seconds
    public static       Random random          = new Random(new Date().getTime());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader(CACHE_CONTROL, String.format(MAX_AGE_PATTERN, THIRTY_DAYS + random.longs(0, TWELVE_HOURS).findFirst().getAsLong()));
        chain.doFilter(request, httpServletResponse);
    }

    @Override
    public void destroy() {
    }
}
