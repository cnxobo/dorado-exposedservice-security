package org.xobo.dorado.exposedservice.security.service.impl;

import java.util.Collection;

import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceUrlCacheService;
import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceViewParserService;

public class DoradoExposedServiceUrlCacheServiceImpl
    implements DoradoExposedServiceUrlCacheService {

  public Collection<String> findUrlsByDoradoServiceUrl(String doradoService) {
    return doradoExposedServiceViewParserService.loadServiceUrlMapping().get(doradoService);
  }

  private DoradoExposedServiceViewParserService doradoExposedServiceViewParserService;

  public DoradoExposedServiceUrlCacheServiceImpl(
      DoradoExposedServiceViewParserService doradoExposedServiceViewParserService) {
    this.doradoExposedServiceViewParserService = doradoExposedServiceViewParserService;
  }


}
