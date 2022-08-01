package org.xobo.dorado.exposedservice.security.api;

import java.util.Collection;

/**
 * 白名单提供者, service集合，对于白名单内的接口，所有用户都可以访问。
 * 
 * @author xobo
 *
 */
public interface DoradoExposedServiceWhiteListProvider {
  Collection<String> getWhiteList();
}
