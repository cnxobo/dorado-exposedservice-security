package org.xobo.dorado.exposedservice.security.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.xobo.dorado.exposedservice.security.api.ServiceUrlMappingProvider;
import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceViewMappingService;

public class DoradoExposedServiceViewServiceMappingImpl
    implements DoradoExposedServiceViewMappingService {

  private static Logger logger =
      LoggerFactory.getLogger(DoradoExposedServiceViewServiceMappingImpl.class);


  private Collection<ServiceUrlMappingProvider> serviceUrlMappingProviderList;


  public DoradoExposedServiceViewServiceMappingImpl(
      Collection<ServiceUrlMappingProvider> doradoUrlProviders) {
    this.serviceUrlMappingProviderList = doradoUrlProviders;
  }

  @CacheEvict(value = "xobo:dorado:security", key = "'SERVICE_URL_MAPPING'")
  public void evictCache() {
    logger.debug("evictCache");

  }

  @Cacheable(value = "xobo:dorado:security", key = "'SERVICE_URL_MAPPING'")
  public Map<String, Collection<String>> loadCachedServiceUrlMapping() {
    return loadServiceUrlMapping();
  }

  /**
   * 返回dorado servive 对应URL映射关系。 key是dorado service 形如 demoController#hello； value是 dorado URL集合，形如
   * ['aa.aaa.d', 'bb.bbb.d']。
   * 
   * @return dorado service map
   */
  public Map<String, Collection<String>> loadServiceUrlMapping() {
    Map<String, Collection<String>> serviceUrlMapping = new HashMap<String, Collection<String>>();

    if (serviceUrlMappingProviderList == null) {
      return serviceUrlMapping;
    }

    for (ServiceUrlMappingProvider serviceUrlMappingProvider : serviceUrlMappingProviderList) {
      Map<String, Collection<String>> myserviceUrlMapping =
          serviceUrlMappingProvider.loadServiceUrlMapping();
      merge(serviceUrlMapping, myserviceUrlMapping);
    }

    return serviceUrlMapping;
  }

  /**
   * 合并Map
   * 
   * @param destination
   * @param from
   */
  private void merge(Map<String, Collection<String>> destination,
      Map<String, Collection<String>> from) {

    for (Entry<String, Collection<String>> entry : from.entrySet()) {
      String key = entry.getKey();
      Collection<String> fromValue = entry.getValue();
      Collection<String> destValue = destination.get(key);
      if (destValue == null) {
        destValue = new HashSet<String>();
        destination.put(key, destValue);
      }

      if (fromValue != null && !fromValue.isEmpty()) {
        destValue.addAll(fromValue);
      }
    }

  }


}
