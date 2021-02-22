package org.xobo.dorado.exposedservice.security.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UrlRedirectFilter implements Filter {

  private Map<String, String> urlMapping = new TreeMap<String, String>(new Comparator<String>() {
    public int compare(String a, String b) {
      return b.length() - a.length();
    }
  });

  public UrlRedirectFilter(Collection<UrlRedirectMappingProvider> urlRedirectMappingProviders) {
    for (UrlRedirectMappingProvider urlRedirectMappingProvider : urlRedirectMappingProviders) {
      urlMapping.putAll(urlRedirectMappingProvider.getMapping());
    }
  }

  public void init(FilterConfig filterConfig) throws ServletException {

  }

  public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    if (req instanceof HttpServletRequest) {
      HttpServletRequest request = (HttpServletRequest) req;
      // Getting servlet request URL
      String url = request.getRequestURL().toString();

      // Getting servlet request query string.
      String queryString = request.getQueryString();

      // Getting request information without the hostname.
      String uri = request.getRequestURI();

      // Below we extract information about the request object path
      // information.
      String scheme = request.getScheme();
      String serverName = request.getServerName();
      int portNumber = request.getServerPort();
      String contextPath = request.getContextPath();
      String servletPath = request.getServletPath();
      String pathInfo = request.getPathInfo();
      String query = request.getQueryString();


      String myUri = uri.substring(request.getContextPath().length() + 1);

      int indexSemicolon = myUri.indexOf(';');

      String semicolnString = "";
      if (indexSemicolon >= 0) {
        semicolnString = myUri.substring(indexSemicolon);
        myUri = myUri.substring(0, indexSemicolon);
      }

      String targetUri = null;

      for (Entry<String, String> entry : urlMapping.entrySet()) {
        String sourceUrl = entry.getKey();
        String replacementUrl = entry.getValue();

        if (myUri.startsWith(sourceUrl)) {
          targetUri = myUri.replace(sourceUrl, replacementUrl);
          break;
        }
      }

      if (targetUri != null) {
        String targetUrl = contextPath + "/" + targetUri + semicolnString;
        if (queryString != null) {
          targetUrl += "?" + queryString;
        }
        System.out.println(targetUrl);
        ((HttpServletResponse) response).sendRedirect(targetUrl);
        return;
      }
    }

    chain.doFilter(req, response);

  }

  public void destroy() {

  }

}
