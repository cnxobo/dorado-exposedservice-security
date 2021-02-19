package org.xobo.dorado.exposedservice.security.configuration;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceAspect;
import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceAuthorizationService;
import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceUrlCacheService;
import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceViewParserService;
import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceWhiteListProvider;
import org.xobo.dorado.exposedservice.security.service.DoradoUrlProvider;
import org.xobo.dorado.exposedservice.security.service.UrlAuthorizationProvider;
import org.xobo.dorado.exposedservice.security.service.impl.DoradoExposedServiceAuthorizationServiceImpl;
import org.xobo.dorado.exposedservice.security.service.impl.DoradoExposedServiceUrlCacheServiceImpl;
import org.xobo.dorado.exposedservice.security.service.impl.DoradoExposedServiceViewParserServiceImpl;
import org.xobo.dorado.exposedservice.security.service.impl.DoradoExposedServiceWhiteListContainerProvider;

import com.bstek.dorado.view.manager.ViewConfigManager;

@Configuration
public class DoradoExposedServiceSecurityConfiguration {
  @Bean
  public DoradoExposedServiceAuthorizationService doradoExposedServiceAuthorizationService(
      DoradoExposedServiceUrlCacheService doradoExposedServiceUrlCacheService,
      Collection<UrlAuthorizationProvider> urlAuthorizationProviders,
      Collection<DoradoExposedServiceWhiteListProvider> doradoExposedServiceWhiteListProviders) {
    return new DoradoExposedServiceAuthorizationServiceImpl(doradoExposedServiceUrlCacheService,
        urlAuthorizationProviders, doradoExposedServiceWhiteListProviders);
  }

  @Bean
  public DoradoExposedServiceUrlCacheService DoradoExposedServiceUrlCacheService(
      DoradoExposedServiceViewParserService doradoExposedServiceViewParserService) {
    return new DoradoExposedServiceUrlCacheServiceImpl(doradoExposedServiceViewParserService);
  }

  @Bean
  public DoradoExposedServiceViewParserService DoradoExposedServiceViewParserService(
      ViewConfigManager viewConfigManager, Collection<DoradoUrlProvider> doradoUrlProviders) {
    return new DoradoExposedServiceViewParserServiceImpl(viewConfigManager, doradoUrlProviders);
  }

  @Bean
  public DoradoExposedServiceAspect doradoExposedServiceAspect(
      DoradoExposedServiceAuthorizationService doradoExposedServiceAuthorizationService) {
    return new DoradoExposedServiceAspect(doradoExposedServiceAuthorizationService);
  }

  @Bean
  DoradoExposedServiceWhiteListContainerProvider doradoExposedServiceWhiteListContainerProvider(
      @Value("${dorado.exposedservice.whitelist:}") Collection<String> whiteList) {
    return new DoradoExposedServiceWhiteListContainerProvider(whiteList);
  }

}
