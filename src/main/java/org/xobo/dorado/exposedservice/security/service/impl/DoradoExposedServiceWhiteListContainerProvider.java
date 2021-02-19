package org.xobo.dorado.exposedservice.security.service.impl;

import java.util.Collection;

import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceWhiteListProvider;

public class DoradoExposedServiceWhiteListContainerProvider
    implements DoradoExposedServiceWhiteListProvider {

  public Collection<String> getWhiteList() {
    return whiteList;
  }

  public DoradoExposedServiceWhiteListContainerProvider(Collection<String> whiteList) {
    this.whiteList = whiteList;
  }

  private Collection<String> whiteList;

}
