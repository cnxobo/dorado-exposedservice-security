package org.xobo.dorado.exposedservice.security.service;

import java.util.Collection;
import java.util.Map;

public interface DoradoExposedServiceViewParserService {

  Map<String, Collection<String>> loadServiceUrlMapping();

  Map<String, Collection<String>> loadCachedServiceUrlMapping();

  void evictCache();

}
