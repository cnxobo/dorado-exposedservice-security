package org.xobo.dorado.exposedservice.security.service;

import java.util.Collection;

public interface DoradoExposedServiceUrlCacheService {

  Collection<String> findUrlsByDoradoServiceUrl(String doradoService);

}
