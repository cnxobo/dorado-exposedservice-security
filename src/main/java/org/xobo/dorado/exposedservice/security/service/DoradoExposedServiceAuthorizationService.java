package org.xobo.dorado.exposedservice.security.service;

import org.aspectj.lang.ProceedingJoinPoint;

public interface DoradoExposedServiceAuthorizationService {

  Boolean checkAuthorization(ProceedingJoinPoint joinPoint);
}
