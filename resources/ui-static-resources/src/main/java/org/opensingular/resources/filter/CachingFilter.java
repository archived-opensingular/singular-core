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
import java.util.stream.LongStream;

@WebFilter(urlPatterns = "*")
public class CachingFilter implements Filter {

    private static long       THIRTY_DAYS  = 86400 * 30; // 30 days in seconds
    private static long       TWELVE_HOURS = 86400 / 2; // 12 hours in seconds
    private static LongStream random       = new Random(new Date().getTime()).longs(0, TWELVE_HOURS);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Cache-Control", String.format("max-age=%d", THIRTY_DAYS + random.findFirst().getAsLong()));
        chain.doFilter(request, httpServletResponse);
    }

    @Override
    public void destroy() {
    }
}
