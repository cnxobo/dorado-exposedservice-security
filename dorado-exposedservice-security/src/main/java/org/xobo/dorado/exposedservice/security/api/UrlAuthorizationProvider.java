package org.xobo.dorado.exposedservice.security.api;

/**
 * 
 * 判断当前用户能否访问URL
 * 
 * @author xobo
 *
 */
public interface UrlAuthorizationProvider {
  boolean authorize(String url);
}
