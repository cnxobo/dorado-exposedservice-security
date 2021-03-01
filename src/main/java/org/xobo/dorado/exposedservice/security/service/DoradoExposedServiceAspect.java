package org.xobo.dorado.exposedservice.security.service;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xobo.dorado.exposedservice.security.exception.AccessDeniedException;

@Aspect
public class DoradoExposedServiceAspect {

  private static Logger logger = LoggerFactory.getLogger(DoradoExposedServiceAspect.class);

  public DoradoExposedServiceAspect(int status,
      DoradoExposedServiceAuthorizationService doradoExposedServiceAuthorizationService) {
    this.status = status;
    this.doradoExposedServiceAuthorizationService = doradoExposedServiceAuthorizationService;
  }

  @Around(value = "@annotation(com.bstek.dorado.annotation.DataProvider)"
      + "|| @annotation(com.bstek.dorado.annotation.DataResolver) "
      + "|| @annotation(com.bstek.dorado.annotation.Expose)")
  public Object aroundExposedMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    Boolean authorization = false;
    try {
      authorization = doradoExposedServiceAuthorizationService.checkAuthorization(joinPoint);
    } catch (Exception e) {
      logger.error("exception ", e);
      authorization = true;
    }

    if (!authorization) {
      logger.error("{} has no authorization", joinPoint.getSignature());
      if (status == 0) {
        logger.error("log only");
      } else if (status == 1) {
        logger.error("skip");
        return null;
      } else if (status == 2) {
        throw new AccessDeniedException("没有权限执行该方法");
      }
    }

    return joinPoint.proceed();

  }

  private DoradoExposedServiceAuthorizationService doradoExposedServiceAuthorizationService;

  /**
   * 0 只记录日志 1 跳过方法执行 2 抛出AccessDeniedException异常
   */
  private int status;

}
