package org.xobo.dorado.exposedservice.security.service;

import java.util.Collection;

public interface DoradoExposedServiceWhiteListProvider {
  Collection<String> getWhiteList();
}
