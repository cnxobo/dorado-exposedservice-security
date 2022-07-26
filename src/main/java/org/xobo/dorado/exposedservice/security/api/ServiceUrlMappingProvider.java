package org.xobo.dorado.exposedservice.security.api;

import java.util.Collection;
import java.util.Map;

/**
 * 加载 dorado service 和 view 关系映射
 * 
 * @author xobo
 *
 */
public interface ServiceUrlMappingProvider {
  Map<String, Collection<String>> loadServiceUrlMapping();
}
