package org.xobo.dorado.exposedservice.security.configuration;

import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.xobo.dorado.exposedservice.security.api.DoradoExposedServiceWhiteListProvider;
import org.xobo.dorado.exposedservice.security.api.DoradoUrlProvider;
import org.xobo.dorado.exposedservice.security.api.ServiceUrlMappingProvider;
import org.xobo.dorado.exposedservice.security.api.UrlAuthorizationProvider;
import org.xobo.dorado.exposedservice.security.aspect.DoradoExposedServiceAspect;
import org.xobo.dorado.exposedservice.security.service.*;
import org.xobo.dorado.exposedservice.security.service.impl.*;
import com.bstek.dorado.view.manager.ViewConfigFactoryRegister;
import com.bstek.dorado.view.manager.ViewConfigManager;

@Configuration
@EnableAspectJAutoProxy
public class DoradoExposedServiceSecurityConfiguration {
  @Bean
  public DoradoExposedServiceAuthorizationService doradoExposedServiceAuthorizationService(
      List<DoradoExposedServiceAccessDecisionVoter> doradoExposedServiceAccessDecisionVoterList,
      Collection<DoradoExposedServiceWhiteListProvider> doradoExposedServiceWhiteListProviders) {
    return new DoradoExposedServiceAuthorizationServiceImpl(
        doradoExposedServiceAccessDecisionVoterList, doradoExposedServiceWhiteListProviders);
  }

  @Bean
  public DoradoExposedServiceUrlCacheService DoradoExposedServiceUrlCacheService(
      DoradoExposedServiceViewMappingService doradoExposedServiceViewParserService,
      @Value("${dorado.exposedservice.mapping.enablecache:true}") boolean enableCacheServiceUrl) {
    return new DoradoExposedServiceUrlCacheServiceImpl(doradoExposedServiceViewParserService,
        enableCacheServiceUrl);
  }

  @Bean
  public DoradoExposedServiceViewMappingService DoradoExposedServiceViewParserService(
      Collection<ServiceUrlMappingProvider> serviceUrlMappingProviders) {
    return new DoradoExposedServiceViewServiceMappingImpl(serviceUrlMappingProviders);
  }

  @Bean
  public DoradoExposedServiceAspect doradoExposedServiceAspect(
      @Value("${dorado.exposedservice.errorhandle:0}") int errorHandle,
      DoradoExposedServiceAuthorizationService doradoExposedServiceAuthorizationService) {
    return new DoradoExposedServiceAspect(errorHandle, doradoExposedServiceAuthorizationService);
  }

  @Bean
  DoradoExposedServiceWhiteListContainerProvider doradoExposedServiceWhiteListContainerProvider(
      @Value("${dorado.exposedservice.whitelist:}") Collection<String> whiteList) {
    return new DoradoExposedServiceWhiteListContainerProvider(whiteList);
  }

  @Bean
  public DoradoUrlRedirectMappingProvider doradoUrlRedirectMappingProvider(
      Collection<ViewConfigFactoryRegister> viewConfigFactoryRegisterList) {
    return new DoradoUrlRedirectMappingProvider(viewConfigFactoryRegisterList);
  }

  @Bean
  public DoradoExposedServiceAccessDecisionVoter doradoExposedServiceAccessDecisionByUrlVoter(
      DoradoExposedServiceUrlCacheService doradoExposedServiceUrlCacheService,
      Collection<UrlAuthorizationProvider> urlAuthorizationProviders) {
    return new DoradoExposedServiceAccessDecisionByUrlVoter(doradoExposedServiceUrlCacheService,
        urlAuthorizationProviders);
  }

  @Bean
  DoradoExposedServiceViewParserServiceImpl doradoExposedServiceViewParserServiceImpl(
      ViewConfigManager viewConfigManager, Collection<DoradoUrlProvider> doradoUrlProviders) {
    return new DoradoExposedServiceViewParserServiceImpl(viewConfigManager, doradoUrlProviders);
  }

}
