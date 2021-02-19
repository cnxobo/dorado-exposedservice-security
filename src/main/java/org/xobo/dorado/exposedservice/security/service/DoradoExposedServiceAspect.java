package org.xobo.dorado.exposedservice.security.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class DoradoExposedServiceAspect {

  private static Logger logger = LoggerFactory.getLogger(DoradoExposedServiceAspect.class);

  public DoradoExposedServiceAspect(
      DoradoExposedServiceAuthorizationService doradoExposedServiceAuthorizationService) {
    this.doradoExposedServiceAuthorizationService = doradoExposedServiceAuthorizationService;
  }

  @Around(
      value = "@annotation(com.bstek.dorado.annotation.DataProvider) || @annotation(com.bstek.dorado.annotation.DataResolver)|| @annotation(com.bstek.dorado.annotation.Expose)")
  public void aroundExposedMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    Boolean authorization = false;
    try {
      authorization = doradoExposedServiceAuthorizationService.checkAuthorization(joinPoint);
    } catch (Exception e) {
      logger.error("exception ", e);
      authorization = true;
    }

    if (!authorization) {
      return;
    }

    joinPoint.proceed();

  }

  private DoradoExposedServiceAuthorizationService doradoExposedServiceAuthorizationService;


}
