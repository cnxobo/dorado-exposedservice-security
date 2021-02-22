package org.xobo.dorado.exposedservice.security.service;

import java.util.Map;

public interface UrlRedirectMappingProvider {
  Map<String, String> getMapping();
}
