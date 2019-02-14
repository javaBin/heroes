package no.javabin.heroes;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EnforceHttpsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (isInsecureHttp((HttpServletRequest) servletRequest)) {
            ((HttpServletResponse)servletResponse).sendRedirect(rewriteRequestToHttps((HttpServletRequest) servletRequest));
            return;
        }


        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String rewriteRequestToHttps(HttpServletRequest req) {
        return "https://" + req.getRemoteHost() + req.getRequestURI()
                + ((req.getQueryString() == null) ? "" : ("?" + req.getQueryString()));
    }

    private boolean isInsecureHttp(HttpServletRequest req) {
        return req.getProtocol().equals("http") && "http".equals(req.getHeader("X-Forwarded-Proto"));
    }

    @Override
    public void destroy() {

    }
}
