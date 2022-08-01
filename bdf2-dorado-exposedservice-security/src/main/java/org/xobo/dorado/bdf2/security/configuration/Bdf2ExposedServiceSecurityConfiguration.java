package org.xobo.dorado.bdf2.security.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.xobo.dorado.bdf2.security.dao.ExposedserviceSecurityDao;
import org.xobo.dorado.bdf2.security.service.BDF2UrlAuthorizationProvider;
import org.xobo.dorado.exposedservice.security.configuration.DoradoExposedServiceSecurityConfiguration;

@Configuration
@Import(DoradoExposedServiceSecurityConfiguration.class)
public class Bdf2ExposedServiceSecurityConfiguration {
  @Bean
  public BDF2UrlAuthorizationProvider BDF2UrlAuthorizationProvider(
      ExposedserviceSecurityDao exposedserviceSecurityDao) {
    return new BDF2UrlAuthorizationProvider(exposedserviceSecurityDao);
  }

  @Bean
  public ExposedserviceSecurityDao exposedserviceSecurityDao() {
    return new ExposedserviceSecurityDao();
  }

}
