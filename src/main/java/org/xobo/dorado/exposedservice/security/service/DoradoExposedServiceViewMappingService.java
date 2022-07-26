package org.xobo.dorado.exposedservice.security.service;

import java.util.Collection;
import java.util.Map;

/**
 * 汇总 dorado service 和 view 关系映射
 * 
 * @author xobo
 *
 */
public interface DoradoExposedServiceViewMappingService {

  /**
   * 加载映射
   * 
   * @return
   */
  Map<String, Collection<String>> loadServiceUrlMapping();

  /**
   * 加载映射并缓存结果
   * 
   * @return
   */
  Map<String, Collection<String>> loadCachedServiceUrlMapping();

  /**
   * 清除缓存
   */
  void evictCache();

}
