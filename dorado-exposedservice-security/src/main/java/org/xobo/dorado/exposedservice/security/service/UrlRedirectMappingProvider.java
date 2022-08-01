package org.xobo.dorado.exposedservice.security.service;

import java.util.Map;

/**
 * 对于配置了ViewConfigFactoryRegister的项目可以同时通过 org.xobo.demo.xxx.d 和 demo.xxx.d 来访问页面。
 * 
 * @author xobo
 *
 */
public interface UrlRedirectMappingProvider {
  Map<String, String> getMapping();
}
