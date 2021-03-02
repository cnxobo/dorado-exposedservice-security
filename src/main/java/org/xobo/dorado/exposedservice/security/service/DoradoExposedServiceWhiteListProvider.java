package org.xobo.dorado.exposedservice.security.service;

import java.util.Collection;

/**
 * 白名单提供者
 * @author xobo
 *
 */
public interface DoradoExposedServiceWhiteListProvider {
  Collection<String> getWhiteList();
}
