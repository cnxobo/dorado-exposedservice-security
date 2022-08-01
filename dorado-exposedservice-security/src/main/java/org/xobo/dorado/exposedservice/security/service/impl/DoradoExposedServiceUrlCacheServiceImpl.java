package org.xobo.dorado.exposedservice.security.service.impl;

import java.util.Collection;
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
      DoradoExposedServiceViewMappingService doradoExposedServiceViewParserService,
      boolean enableCacheServiceUrl) {
    this.doradoExposedServiceViewParserService = doradoExposedServiceViewParserService;
    this.enableCacheServiceUrl = enableCacheServiceUrl;
  }


  private boolean enableCacheServiceUrl;

  public void setEnableCacheServiceUrl(boolean enableCacheServiceUrl) {
    this.enableCacheServiceUrl = enableCacheServiceUrl;
  }

}
