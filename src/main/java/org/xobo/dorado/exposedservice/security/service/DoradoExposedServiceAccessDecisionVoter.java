package org.xobo.dorado.exposedservice.security.service;

/**
 *  权限投票器
 * @author cnxobo
 *
 */
public interface DoradoExposedServiceAccessDecisionVoter {

  int ACCESS_GRANTED = 1;
  int ACCESS_ABSTAIN = 0;
  int ACCESS_DENIED = -1;

  int vote(String doradoService);
}
