package org.xobo.dorado.exposedservice.security.service.impl;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceAccessDecisionVoter;
import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceUrlCacheService;
import org.xobo.dorado.exposedservice.security.service.UrlAuthorizationProvider;

public class DoradoExposedServiceAccessDecisionByUrlVoter
    implements DoradoExposedServiceAccessDecisionVoter {

  private static Logger logger =
      LoggerFactory.getLogger(DoradoExposedServiceAccessDecisionByUrlVoter.class);

  private DoradoExposedServiceUrlCacheService doradoExposedServiceUrlCacheService;
  private Collection<UrlAuthorizationProvider> urlAuthorizationProviders;



  public DoradoExposedServiceAccessDecisionByUrlVoter(
      DoradoExposedServiceUrlCacheService doradoExposedServiceUrlCacheService,
      Collection<UrlAuthorizationProvider> urlAuthorizationProviders) {
    this.doradoExposedServiceUrlCacheService = doradoExposedServiceUrlCacheService;
    this.urlAuthorizationProviders = urlAuthorizationProviders;
  }

  public int vote(String doradoService) {

    Collection<String> urls =
        doradoExposedServiceUrlCacheService.findUrlsByDoradoServiceUrl(doradoService);
    if (urls == null || urls.isEmpty()) {
      logger.error("doradoService {} does not associate with url.", doradoService);
      return ACCESS_GRANTED;
    }

    boolean authorization = false;
    for (String url : urls) {
      for (UrlAuthorizationProvider urlAuthorizationProvider : urlAuthorizationProviders) {
        authorization = urlAuthorizationProvider.authorize(url);
        if (authorization) {
          break;
        }
      }
    }
    return authorization ? ACCESS_GRANTED : ACCESS_DENIED;
  }


}
