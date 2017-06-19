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

    public static final String CACHE_CONTROL   = "Cache-Control";
    public static final String MAX_AGE_PATTERN = "max-age=%d";
    public static final long   THIRTY_DAYS     = 86400l * 30l; // 30 days in seconds
    public static final long   TWELVE_HOURS    = 86400l / 2l; // 12 hours in seconds
    public static final Random random          = new Random(new Date().getTime()); //NOSONAR

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
