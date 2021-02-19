package org.xobo.dorado.exposedservice.security.service.impl;

import java.beans.Introspector;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceAuthorizationService;
import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceUrlCacheService;
import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceWhiteListProvider;
import org.xobo.dorado.exposedservice.security.service.UrlAuthorizationProvider;

import com.bstek.dorado.web.DoradoContext;

public class DoradoExposedServiceAuthorizationServiceImpl
    implements DoradoExposedServiceAuthorizationService {
  private static Logger logger =
      LoggerFactory.getLogger(DoradoExposedServiceAuthorizationServiceImpl.class);

  private Collection<UrlAuthorizationProvider> urlAuthorizationProviders;
  private Set<String> whiteListSet;

  private DoradoExposedServiceUrlCacheService doradoExposedServiceUrlCacheService;

  public DoradoExposedServiceAuthorizationServiceImpl(
      DoradoExposedServiceUrlCacheService doradoExposedServiceUrlCacheService,
      Collection<UrlAuthorizationProvider> urlAuthorizationProviders,
      Collection<DoradoExposedServiceWhiteListProvider> doradoExposedServiceWhiteListProviders) {
    this.doradoExposedServiceUrlCacheService = doradoExposedServiceUrlCacheService;
    this.urlAuthorizationProviders = urlAuthorizationProviders;
    setWhilteList(doradoExposedServiceWhiteListProviders);
  }

  public void setWhilteList(
      Collection<DoradoExposedServiceWhiteListProvider> doradoExposedServiceWhiteListProviders) {
    this.whiteListSet = new HashSet<String>();
    for (DoradoExposedServiceWhiteListProvider provider : doradoExposedServiceWhiteListProviders) {
      whiteListSet.addAll(provider.getWhiteList());
    }
  }


  public Boolean checkAuthorization(ProceedingJoinPoint joinPoint) {
    String doradoService = getDoradoService(joinPoint);

    if (whiteListSet.contains(doradoService)) {
      logger.debug("doradoService {} in whitelist", doradoService);
      return true;
    }

    DoradoContext doradoContext = DoradoContext.getCurrent();
    if (doradoContext == null) {
      logger.error("doradoService {} not in doradocontext", doradoService);
      // 非 dorado 上下文，不做权限效验
      return true;
    }

    boolean authorization = authorize(doradoService);
    if (!authorization) {
      logger.error("doradoService {} no authorization.", doradoService);
    } else {
      logger.debug("doradoService {} has authorization.", doradoService);
    }
    return authorization;
  }


  public boolean authorize(String doradoService) {
    Collection<String> urls =
        doradoExposedServiceUrlCacheService.findUrlsByDoradoServiceUrl(doradoService);
    if (urls == null || urls.isEmpty()) {
      logger.error("doradoService {} does not associate with url.", doradoService);
      return true;
    }
    for (String url : urls) {
      for (UrlAuthorizationProvider urlAuthorizationProvider : urlAuthorizationProviders) {
        boolean authorization = urlAuthorizationProvider.authorize(url);
        if (authorization) {
          return true;
        }
      }
    }
    return false;
  }

  public String getDoradoService(JoinPoint joinPoint) {
    String beanName = getBeanName(joinPoint.getTarget().getClass());
    String methodName = joinPoint.getSignature().getName();
    return beanName + "#" + methodName;
  }

  public String getBeanName(Class<?> clazz) {
    Service[] services = clazz.getAnnotationsByType(Service.class);
    String beanName = null;
    for (Service service : services) {
      String value = service.value();
      if (StringUtils.isEmpty(value)) {
        beanName = value;
        break;
      }
    }

    return StringUtils.isEmpty(beanName) ? Introspector.decapitalize(clazz.getSimpleName())
        : beanName;
  }

}
