package org.xobo.dorado.exposedservice.security.service;

public interface UrlAuthorizationProvider {
  boolean authorize(String url);
}
