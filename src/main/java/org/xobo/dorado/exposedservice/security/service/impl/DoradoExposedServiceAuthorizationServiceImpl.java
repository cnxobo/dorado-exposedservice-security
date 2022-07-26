package org.xobo.dorado.exposedservice.security.service.impl;

import java.beans.Introspector;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.xobo.dorado.exposedservice.security.api.DoradoExposedServiceWhiteListProvider;
import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceAccessDecisionVoter;
import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceAuthorizationService;
import com.bstek.dorado.web.DoradoContext;

public class DoradoExposedServiceAuthorizationServiceImpl
    implements DoradoExposedServiceAuthorizationService {
  private static Logger logger =
      LoggerFactory.getLogger(DoradoExposedServiceAuthorizationServiceImpl.class);

  private Set<String> whiteListSet;
  private List<DoradoExposedServiceAccessDecisionVoter> doradoExposedServiceAccessDecisionVoterList;


  public DoradoExposedServiceAuthorizationServiceImpl(
      List<DoradoExposedServiceAccessDecisionVoter> doradoExposedServiceAccessDecisionVoterList,
      Collection<DoradoExposedServiceWhiteListProvider> doradoExposedServiceWhiteListProviders) {

    AnnotationAwareOrderComparator.sort(doradoExposedServiceAccessDecisionVoterList);
    this.doradoExposedServiceAccessDecisionVoterList = doradoExposedServiceAccessDecisionVoterList;

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

    if (!isInDoradoContext()) {
      logger.debug("doradoService {} not in doradocontext", doradoService);
      // 非 dorado 上下文，不做权限效验
      return true;
    }

    boolean authorization = authorize(doradoService);
    if (!authorization) {
      logger.debug("doradoService {} no authorization.", doradoService);
    } else {
      logger.debug("doradoService {} has authorization.", doradoService);
    }
    return authorization;
  }

  private boolean isInDoradoContext() {
    DoradoContext doradoContext = null;
    try {
      doradoContext = DoradoContext.getCurrent();
    } catch (Exception e) {
      logger.debug("get dorado context error ", doradoContext);
    }
    return doradoContext != null;
  }


  public boolean authorize(String doradoService) {

    for (DoradoExposedServiceAccessDecisionVoter doradoExposedServiceAccessDecisionVoter : doradoExposedServiceAccessDecisionVoterList) {
      int vote = doradoExposedServiceAccessDecisionVoter.vote(doradoService);
      if (vote == DoradoExposedServiceAccessDecisionVoter.ACCESS_GRANTED) {
        return true;
      } else if (vote == DoradoExposedServiceAccessDecisionVoter.ACCESS_DENIED) {
        return false;
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
