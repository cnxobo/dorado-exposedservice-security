package org.xobo.dorado.exposedservice.security.service.impl;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Value;
import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceUrlCacheService;
import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceViewMappingService;

public class DoradoExposedServiceUrlCacheServiceImpl
    implements DoradoExposedServiceUrlCacheService {

  public Collection<String> findUrlsByDoradoServiceUrl(String doradoService) {
    if (enableCacheServiceUrl) {
      return doradoExposedServiceViewParserService.loadCachedServiceUrlMapping().get(doradoService);
    } else {
      return doradoExposedServiceViewParserService.loadServiceUrlMapping().get(doradoService);
    }
  }

  private DoradoExposedServiceViewMappingService doradoExposedServiceViewParserService;

  public DoradoExposedServiceUrlCacheServiceImpl(
      DoradoExposedServiceViewMappingService doradoExposedServiceViewParserService) {
    this.doradoExposedServiceViewParserService = doradoExposedServiceViewParserService;
  }

  @Value("${dorado.exposedservice.mapping.enablecache:true}")
  private boolean enableCacheServiceUrl;


}
