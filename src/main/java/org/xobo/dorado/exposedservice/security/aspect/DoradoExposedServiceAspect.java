package org.xobo.dorado.exposedservice.security.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xobo.dorado.exposedservice.security.exception.AccessDeniedException;
import org.xobo.dorado.exposedservice.security.service.DoradoExposedServiceAuthorizationService;

@Aspect
public class DoradoExposedServiceAspect {

  private static Logger logger = LoggerFactory.getLogger(DoradoExposedServiceAspect.class);

  public DoradoExposedServiceAspect(int status,
      DoradoExposedServiceAuthorizationService doradoExposedServiceAuthorizationService) {
    this.errorHandle = status;
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
      authorization = false;
    }

    if (!authorization) {
      logger.error("{} has no authorization", joinPoint.getSignature());
      if (errorHandle == 0) {
        logger.error("log only");
      } else if (errorHandle == 1) {
        logger.error("skip method invoke");
        return null;
      } else if (errorHandle == 2) {
        throw new AccessDeniedException("没有权限执行该方法");
      }
    }

    return joinPoint.proceed();

  }

  private DoradoExposedServiceAuthorizationService doradoExposedServiceAuthorizationService;

  /**
   * 0 只记录日志 1 跳过方法执行 2 抛出AccessDeniedException异常
   */
  private int errorHandle;

}
